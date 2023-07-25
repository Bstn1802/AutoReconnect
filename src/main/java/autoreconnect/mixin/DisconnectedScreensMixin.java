package autoreconnect.mixin;

import autoreconnect.AutoReconnect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.DisconnectedRealmsScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Mixin({ DisconnectedScreen.class, DisconnectedRealmsScreen.class })
public class DisconnectedScreensMixin extends Screen {
    @Unique
    private ButtonWidget reconnectButton, cancelButton, backButton;
    @Unique
    private boolean shouldAutoReconnect;

    protected DisconnectedScreensMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        backButton = AutoReconnect.findBackButton(this)
            .orElseThrow(() -> new NoSuchElementException("Couldn't find the back button on the disconnect screen"));

        shouldAutoReconnect = AutoReconnect.getConfig().hasAttempts();

        reconnectButton = ButtonWidget.builder(
                Text.translatable("text.autoreconnect.disconnect.reconnect"),
                btn -> AutoReconnect.schedule(() -> MinecraftClient.getInstance().execute(this::manualReconnect), 100, TimeUnit.MILLISECONDS))
            .dimensions(0, 0, 0, 20).build();

        // put reconnect (and cancel button) where back button is and push that down
        reconnectButton.setX(backButton.getX());
        reconnectButton.setY(backButton.getY());
        if (shouldAutoReconnect) {
            reconnectButton.setWidth(backButton.getWidth() - backButton.getHeight() - 4);

            cancelButton = ButtonWidget.builder(
                    Text.literal("✕")
                        .styled(s -> s.withColor(Formatting.RED)),
                    btn -> cancelCountdown())
                .dimensions(
                    backButton.getX() + backButton.getWidth() - backButton.getHeight(),
                    backButton.getY(),
                    backButton.getHeight(),
                    backButton.getHeight())
                .build();

            addDrawableChild(cancelButton);
        } else {
            reconnectButton.setWidth(backButton.getWidth());
        }
        addDrawableChild(reconnectButton);
        backButton.setY(backButton.getY() + backButton.getHeight() + 4);

        if (shouldAutoReconnect) {
            AutoReconnect.getInstance().startCountdown(this::countdownCallback);
        }
    }

    @Unique
    private void manualReconnect() {
        AutoReconnect.getInstance().cancelAutoReconnect();
        AutoReconnect.getInstance().reconnect();
    }

    @Unique
    private void cancelCountdown() {
        AutoReconnect.getInstance().cancelAutoReconnect();
        shouldAutoReconnect = false;
        remove(cancelButton);
        reconnectButton.active = true; // in case it was deactivated after running out of attempts
        reconnectButton.setMessage(Text.translatable("text.autoreconnect.disconnect.reconnect"));
        reconnectButton.setWidth(backButton.getWidth()); // reset to full width
    }

    @Unique
    private void countdownCallback(int seconds) {
        if (seconds < 0) {
            // indicates that we're out of attempts
            reconnectButton.setMessage(Text.translatable("text.autoreconnect.disconnect.reconnect_failed")
                .styled(s -> s.withColor(Formatting.RED)));
            reconnectButton.active = false;
        } else {
            reconnectButton.setMessage(Text.translatable("text.autoreconnect.disconnect.reconnect_in", seconds)
                .styled(s -> s.withColor(Formatting.GREEN)));
        }
    }

    // cancel auto reconnect when pressing escape, higher priority than exiting the screen
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && shouldAutoReconnect) {
            cancelCountdown();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
}
