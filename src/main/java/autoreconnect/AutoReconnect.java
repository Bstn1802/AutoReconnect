package autoreconnect;

import autoreconnect.config.GuiTransformers;
import autoreconnect.config.ModConfig;
import autoreconnect.reconnect.ReconnectHandler;
import autoreconnect.reconnect.SingleplayerReconnectHandler;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
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
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntConsumer;

public class AutoReconnect implements ClientModInitializer {
    public static final ScheduledExecutorService EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(1);
    private static AutoReconnect instance;
    private final AtomicReference<ScheduledFuture<?>> countdown = new AtomicReference<>(null);
    private ReconnectHandler reconnectHandler = null;

    static {
        ((ScheduledThreadPoolExecutor) EXECUTOR_SERVICE).setRemoveOnCancelPolicy(true);
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        GuiRegistry registry = AutoConfig.getGuiRegistry(ModConfig.class);
        registry.registerPredicateTransformer(
            (guis, s, f, c, d, g) -> GuiTransformers.setMinimum(guis, 1),
            field -> GuiTransformers.isField(field, ModConfig.class, "delays") ||
                GuiTransformers.isField(field, ModConfig.AutoMessages.class, "delay")
        );
        registry.registerPredicateTransformer(
            (guis, s, f, c, d, g) -> GuiTransformers.disableInsertInFront(guis),
            field -> List.class.isAssignableFrom(field.getType())
        );
    }

    public static AutoReconnect getInstance() {
        return instance;
    }

    public static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public void setReconnectHandler(ReconnectHandler reconnectHandler) {
        if (this.reconnectHandler != null) {
            // should imply that both handlers target the same world/server
            // we return to preserve the attempts counter
            assert this.reconnectHandler.getClass().equals(reconnectHandler.getClass()) &&
                this.reconnectHandler.getName().equals(reconnectHandler.getName());
            return;
        }
        this.reconnectHandler = reconnectHandler;
    }

    public void reconnect() {
        if (reconnectHandler == null) return; // shouldn't happen normally, but can be forced
        reconnectHandler.reconnect();
    }

    public void startCountdown(final IntConsumer callback) {
        try {
            assert countdown.get() == null; // should always be
            countdown(getConfig().getDelayForAttempt(reconnectHandler.nextAttempt()), callback);
        } catch (IndexOutOfBoundsException ex) {
            // no more attempts configured
            callback.accept(-1);
        }
    }

    public void cancelAutoReconnect() {
        assert reconnectHandler != null;
        reconnectHandler.resetAttempts();
        synchronized (countdown) { // just to be sure
            if (countdown.get() == null) return;
            countdown.getAndSet(null).cancel(true); // should stop the timer
        }
    }

    public void onScreenChanged(Screen current, Screen next) {
        if (sameType(current, next)) return;
        // TODO condition could use some improvement, shouldn't cause any issues tho
        if (!isMainScreen(current) && isMainScreen(next) || isReAuthenticating(current, next)) {
            reconnectHandler = null;
        }
    }

    public void onGameJoined() {
        if (!reconnectHandler.isAttempting()) return; // manual (re)connect
        reconnectHandler.resetAttempts();
        // sendMessages if configured for this world/server
        if (!getConfig().getAutoMessages().getName().equals(reconnectHandler.getName())) return;
        sendMessages(MinecraftClient.getInstance().player, getConfig().getAutoMessages().getMessages());
    }

    public boolean isPlayingSingleplayer() {
        return reconnectHandler instanceof SingleplayerReconnectHandler;
    }

    // simulated timer using delayed recursion
    private void countdown(int seconds, final IntConsumer callback) {
        if (seconds == 0) {
            MinecraftClient.getInstance().execute(reconnectHandler::reconnect);
            return;
        }
        callback.accept(seconds);
        // wait last for no initial delay
        synchronized (countdown) { // just to be sure
            countdown.set(EXECUTOR_SERVICE.schedule(
                () -> countdown(seconds - 1, callback),
                1, TimeUnit.SECONDS));
        }
    }

    // simulated timer using delayed recursion
    private void sendMessages(ClientPlayerEntity player, Iterator<String> messages) {
        if (!messages.hasNext()) return;
        // wait first for initial delay
        EXECUTOR_SERVICE.schedule(
            () -> {
                player.sendChatMessage(messages.next());
                sendMessages(player, messages);
            },
            getConfig().getAutoMessages().getDelay(),
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
            to.getClass().getName().equals("me.axieum.mcmod.authme.gui.AuthScreen");
    }
}
