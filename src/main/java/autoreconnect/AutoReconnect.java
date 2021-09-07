package autoreconnect;

import autoreconnect.config.GuiTransformers;
import autoreconnect.config.ModConfig;
import autoreconnect.event.*;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

import static autoreconnect.config.ModConfig.*;
import static java.lang.Math.ceil;
import static java.lang.Math.min;
import static org.apache.logging.log4j.LogManager.getRootLogger;

public class AutoReconnect implements ModInitializer, DisconnectScreenRenderListener, DisconnectListener, ClientTickEvents.StartTick, PlayerJoinedListener, ScreenChangedListener, ServerEntryChangedListener {
    private int attempt = -1;
    private int ticks = -1;

    private ServerInfo server = null;

    @Override
    public void onInitialize() {
        ClientTickEvents.START_CLIENT_TICK.register(this);
        DisconnectListener.EVENT.register(this);
        PlayerJoinedListener.EVENT.register(this);
        DisconnectScreenRenderListener.EVENT.register(this);
        ScreenChangedListener.EVENT.register(this);
        ServerEntryChangedListener.EVENT.register(this);

        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        GuiRegistry registry = AutoConfig.getGuiRegistry(ModConfig.class);
        registry.registerPredicateTransformer(
            (guis, s, f, c, d, g) -> GuiTransformers.setMinimum(guis, 1),
            field -> GuiTransformers.isField(field, ModConfig.class, "delays") || GuiTransformers.isField(field, ServerMessages.class, "delay")
        );
        registry.registerPredicateTransformer(
            (guis, s, f, c, d, g) -> GuiTransformers.disableInsertInFront(guis),
            field -> List.class.isAssignableFrom(field.getType())
        );
    }

    private ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @Override
    public void onScreenChanged(Screen current, Screen next) {
        // return if there is no server to reconnect to
        if (server == null) return;
        // exiting disconnect screen using...
        if (current instanceof DisconnectedScreen) {
            // ...back button or escape key
            if (next instanceof MultiplayerScreen || next instanceof TitleScreen) {
                resetAttempts("exited disconnect screen");
            }
            // ...AuthMe mod to revalidate session
            if (next != null && next.getClass().getName().equals("me.axieum.mcmod.authme.gui.AuthScreen")) {
                resetAttempts("re-authenticating using AuthMe mod");
            }
        }
        if (current instanceof ConnectScreen && next instanceof MultiplayerScreen) {
            // cancelled connecting FIXME bug MC-74984 still connecting to game? if connect screen mixin needed then make it closable using escape as well
            resetAttempts("cancelled reconnecting");
        }
    }

    @Override
    public void onStartTick(MinecraftClient client) {
        // decrements countdown until its negative, succeeds if its 0
        if (ticks >= 0 && --ticks == 0) {
            // reconnect to server cancel reconnecting process
            if (server != null) {
                ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), client, ServerAddress.parse(server.address), server);
            } else {
                resetAttempts("no server to reconnect to");
            }
        }
    }

    @Override
    public void onRender(MatrixStack matrices, int reasonHeight) {
        Window window = MinecraftClient.getInstance().getWindow();
        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
        final String text = attempt < 0 ? "Could not reconnect" : "Reconnect in %d...".formatted((int) ceil(ticks / 20.0));
        renderer.draw(matrices, text,
            (window.getScaledWidth() - renderer.getWidth(text)) / 2F, // centered horizontally
            (window.getScaledHeight() - reasonHeight) / 2F - 9 * 4, // 9 * 2 higher than the title which is 9 * 2 higher than the disconnect reason
            attempt < 0 ? 0xFF5555 : 0x55FF55); // minecraft text color red and green
    }

    @Override
    public void onDisconnected(Text reason) {
        // if next attempt is configured
        if (++attempt < getConfig().getDelays().length || getConfig().isInfinite()) {
            ticks = getConfig().getDelays()[min(getConfig().getDelays().length - 1, attempt)] * 20;
            getRootLogger().info("ATTEMPT #%d, SERVER=%s".formatted(attempt, server == null ? "null" : server.name));
        } else {
            resetAttempts("no more attempts configured");
        }
    }

    @Override
    public void onPlayerJoined(ClientPlayerEntity player) {
        // return if this was not a reconnect
        if (attempt < 0) return;
        resetAttempts("successfully reconnected");
        // send configured messages for this server
        ServerMessages serverMessages = getConfig().getServerMessages();
        // if server address equals this servers address and theres at least one message configured
        if (server.address.equals(serverMessages.getServerAddress()) && serverMessages.getMessages().length > 0) {
            // create a thread to send each message with a certain delay
            // TODO use timer instead and make it based on game ticks similar to the reconnect timer
            new Thread(() -> {
                try {
                    for (String message : serverMessages.getMessages()) {
                        player.sendChatMessage(message);
                        Thread.sleep(serverMessages.getDelay());
                    }
                } catch (InterruptedException ignored) { }
            }).start();
        }
    }

    @Override
    public void onServerEntryChanged(ServerInfo server) {
        if (server != null) {
            getRootLogger().info("SERVER: " + server.name);
            this.server = server;
        }
    }

    public void resetAttempts(String reason) {
        ticks = -1;
        attempt = -1;
        getRootLogger().info(reason == null ? "RESET" : "RESET, reason: " + reason);
    }
}