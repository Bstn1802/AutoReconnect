package autoreconnect;

import autoreconnect.config.AutoReconnectConfig;
import autoreconnect.reconnect.MultiplayerReconnectStrategy;
import autoreconnect.reconnect.ReconnectStrategy;
import autoreconnect.reconnect.SingleplayerReconnectStrategy;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.text.TranslatableTextContent;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntConsumer;

public class AutoReconnect implements ClientModInitializer {
    private static final ScheduledThreadPoolExecutor EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(1);
    private static AutoReconnect instance;
    private final AtomicReference<ScheduledFuture<?>> countdown = new AtomicReference<>(null);
    private ReconnectStrategy reconnectStrategy = null;

    static {
        EXECUTOR_SERVICE.setRemoveOnCancelPolicy(true);
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        AutoReconnectConfig.load();
    }

    public static AutoReconnect getInstance() {
        return instance;
    }

    public static AutoReconnectConfig getConfig() {
        return AutoReconnectConfig.getInstance();
    }

    public static ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit timeUnit) {
        return EXECUTOR_SERVICE.schedule(command, delay, timeUnit);
    }

    public void setReconnectHandler(ReconnectStrategy reconnectStrategy) {
        if (this.reconnectStrategy != null) {
            // should imply that both handlers target the same world/server
            // we return to preserve the attempts counter
            assert this.reconnectStrategy.getClass().equals(reconnectStrategy.getClass()) &&
                this.reconnectStrategy.getName().equals(reconnectStrategy.getName());
            return;
        }
        this.reconnectStrategy = reconnectStrategy;
    }

    public void reconnect() {
        if (reconnectStrategy == null) return; // shouldn't happen normally, but can be forced
        cancelCountdown();
        reconnectStrategy.reconnect();
    }

    public void startCountdown(final IntConsumer callback) {
        // if (countdown.get() != null) return; // should not happen
        if (reconnectStrategy == null) {
            // TODO fix issue appropriately, logging error for now
            LogUtils.getLogger().error("Cannot reconnect because reconnectStrategy is null");
            callback.accept(-1); // signal reconnecting is not possible
            return;
        }
        int delay = getConfig().getDelayForAttempt(reconnectStrategy.nextAttempt());
        if (delay >= 0) {
            countdown(delay, callback);
        } else {
            // no more attempts configured
            callback.accept(-1);
        }
    }

    public void cancelAutoReconnect() {
        if (reconnectStrategy == null) return; // should not happen
        reconnectStrategy.resetAttempts();
        cancelCountdown();
    }

    public void onScreenChanged(Screen current, Screen next) {
        if (sameType(current, next)) return;
        // TODO condition could use some improvement, shouldn't cause any issues tho
        if (!isMainScreen(current) && isMainScreen(next) || isReAuthenticating(current, next)) {
            cancelAutoReconnect();
            reconnectStrategy = null;
        }
    }

    public void onGameJoined(ServerInfo serverInfo) {
        if (reconnectStrategy == null) {
            // Multimc use the --server flag, so mixins won't register the screen and the reconnect strategy won't be set,
            // I assume ServerInfo is populated only in multiplayer cases (server added to the list)
            // We also check if the config is still using the default server address, cuz if not, we can use that one as it was set by the user
            if ((serverInfo != null && !serverInfo.isLocal()) && AutoReconnectConfig.getInstance().serverAddress.equals(AutoReconnectConfig.defaultServerAddress))
                setReconnectHandler(new MultiplayerReconnectStrategy(serverInfo));
            else setReconnectHandler(new MultiplayerReconnectStrategy(new ServerInfo("Multiplayer", AutoReconnectConfig.getInstance().serverAddress, false)));

            return;
        }

        if (!reconnectStrategy.isAttempting()) return; // manual (re)connect

        reconnectStrategy.resetAttempts();

        // Send automatic messages if configured for the current context
        getConfig().getAutoMessagesForName(reconnectStrategy.getName()).ifPresent(
            autoMessages -> sendAutomatedMessages(
                MinecraftClient.getInstance().player,
                autoMessages.getMessages(),
                autoMessages.getDelay()
            )
        );
    }

    public boolean isPlayingSingleplayer() {
        return reconnectStrategy instanceof SingleplayerReconnectStrategy;
    }

    private void cancelCountdown() {
        synchronized (countdown) { // just to be sure
            if (countdown.get() == null) return;
            countdown.getAndSet(null).cancel(true); // should stop the timer
        }
    }

    // simulated timer using delayed recursion
    private void countdown(int seconds, final IntConsumer callback) {
        if (reconnectStrategy == null) return; // should not happen
        if (seconds == 0) {
            MinecraftClient.getInstance().execute(this::reconnect);
            return;
        }
        callback.accept(seconds);
        // wait at end of method for no initial delay
        synchronized (countdown) { // just to be sure
            countdown.set(schedule(() -> countdown(seconds - 1, callback), 1, TimeUnit.SECONDS));
        }
    }

    /**
     * Handle a list of messages to send by the player to the current connection.
     *
     * @param player   Player to send the message as.
     * @param messages String Iterator of messages to send.
     * @param delay    Delay in milliseconds before the first and between each following message.
     */
    private void sendAutomatedMessages(ClientPlayerEntity player, Iterator<String> messages, int delay) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(() -> {
            if (!messages.hasNext()) {
                executorService.shutdown();
                return;
            }

            sendMessage(player, messages.next());
        }, delay, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Handles sending of a single message or command by the player.
     *
     * @param player  Player to send the message as.
     * @param message String with the message or command to send.
     */
    private void sendMessage(ClientPlayerEntity player, String message) {
        if (message.startsWith("/")) {
            // The first starting slash has to be removed,
            // otherwise it will be interpreted as a double slash.
            String command = message.substring(1);
            player.networkHandler.sendCommand(command);
        } else {
            player.networkHandler.sendChatMessage(message);
        }
    }

    private static boolean sameType(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a != null && b != null) return a.getClass().equals(b.getClass());
        return false;
    }

    private static boolean isMainScreen(Screen screen) {
        return screen instanceof TitleScreen || screen instanceof SelectWorldScreen ||
            screen instanceof MultiplayerScreen || screen instanceof RealmsMainScreen;
    }

    private static boolean isReAuthenticating(Screen from, Screen to) {
        return from instanceof DisconnectedScreen && to != null &&
            to.getClass().getName().startsWith("me.axieum.mcmod.authme");
    }

    public static Optional<ButtonWidget> findBackButton(Screen screen) {
        for (Element element : screen.children()) {
            if (!(element instanceof ButtonWidget button)) continue;

            String translatableKey;
            if (button.getMessage() instanceof TranslatableTextContent translatable) {
                translatableKey = translatable.getKey();
            } else if (button.getMessage().getContent() instanceof TranslatableTextContent translatable) {
                translatableKey = translatable.getKey();
            } else continue;

            // check for gui.back, gui.toMenu, gui.toRealms, gui.toTitle, gui.toWorld (only ones starting with "gui.to")
            if (translatableKey.equals("gui.back") || translatableKey.startsWith("gui.to")) {
                return Optional.of(button);
            }
        }
        return Optional.empty();
    }
}
