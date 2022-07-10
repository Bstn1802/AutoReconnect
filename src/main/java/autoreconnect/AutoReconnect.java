package autoreconnect;

import autoreconnect.config.AutoReconnectConfig;
import autoreconnect.reconnect.ReconnectStrategy;
import autoreconnect.reconnect.SingleplayerReconnectStrategy;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;

import java.util.Iterator;
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

    public static void schedule(Runnable command, long delay, TimeUnit timeUnit) {
        EXECUTOR_SERVICE.schedule(command, delay, timeUnit);
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

    public void onGameJoined() {
        if (reconnectStrategy == null) return; // should not happen
        if (!reconnectStrategy.isAttempting()) return; // manual (re)connect

        reconnectStrategy.resetAttempts();
        // sendMessages if configured for this world/server
        getConfig().getAutoMessagesForName(reconnectStrategy.getName())
            .ifPresent(autoMessages -> sendMessages(
                MinecraftClient.getInstance().player,
                autoMessages.getMessages(),
                autoMessages.getDelay()));
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
            countdown.set(EXECUTOR_SERVICE.schedule(() ->
                countdown(seconds - 1, callback), 1, TimeUnit.SECONDS));
        }
    }

    // simulated timer using delayed recursion
    private void sendMessages(ClientPlayerEntity player, Iterator<String> messages, int delay) {
        if (!messages.hasNext()) return;
        // wait at beginning of method for initial delay
        EXECUTOR_SERVICE.schedule(
            () -> {
                player.sendChatMessage(messages.next());
                sendMessages(player, messages, delay);
            },
            delay,
            TimeUnit.MILLISECONDS);
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
}
