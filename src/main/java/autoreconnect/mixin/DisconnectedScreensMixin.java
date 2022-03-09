package autoreconnect.mixin;

import autoreconnect.AutoReconnect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.DisconnectedRealmsScreen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.TimeUnit;

@Mixin({DisconnectedScreen.class, DisconnectedRealmsScreen.class})
public class DisconnectedScreensMixin extends Screen {
    private ButtonWidget reconnectButton, cancelButton;
    private boolean shouldAutoReconnect;

    public DisconnectedScreensMixin(Text text) {
        super(text);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void constructor(Screen parent, Text title, Text reason, CallbackInfo info) {
        reconnectButton = new ButtonWidget(
            0, 0, 0, 20, // init height here, can't change it later, should always be 20
            new TranslatableText("text.autoreconnect.disconnect.reconnect"),
            // schedule 100ms later to let the button click sound play
            btn -> AutoReconnect.EXECUTOR_SERVICE.schedule(
                () -> MinecraftClient.getInstance().execute(this::manualReconnect), 100, TimeUnit.MILLISECONDS));
        shouldAutoReconnect = AutoReconnect.getConfig().hasAttempts();
        if (shouldAutoReconnect) {
            AutoReconnect.getInstance().startCountdown(this::countdownCallback);
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        ButtonWidget backButton = (ButtonWidget) children().get(0);
        // put reconnect (and cancel button) where back button is and push that down
        reconnectButton.x = backButton.x;
        reconnectButton.y = backButton.y;
        if (shouldAutoReconnect) {
            reconnectButton.setWidth(backButton.getWidth() - backButton.getHeight() - 4);
            cancelButton = new ButtonWidget(
                backButton.x + backButton.getWidth() - backButton.getHeight(),
                backButton.y,
                backButton.getHeight(),
                backButton.getHeight(),
                new LiteralText("✕").styled(s -> s.withColor(Formatting.RED)),
                btn -> cancelCountdown()
            );
            addDrawableChild(cancelButton);
        } else {
            reconnectButton.setWidth(backButton.getWidth());
        }
        addDrawableChild(reconnectButton);
        backButton.y += backButton.getHeight() + 4;
    }

    private void manualReconnect() {
        AutoReconnect.getInstance().cancelAutoReconnect();
        AutoReconnect.getInstance().reconnect();
    }

    private void cancelCountdown() {
        AutoReconnect.getInstance().cancelAutoReconnect();
        shouldAutoReconnect = false;
        remove(cancelButton);
        reconnectButton.active = true; // in case it was deactivated after running out of attempts
        reconnectButton.setMessage(new TranslatableText("text.autoreconnect.disconnect.reconnect"));
        reconnectButton.setWidth(((ButtonWidget) children().get(0)).getWidth()); // reset to full width
    }

    private void countdownCallback(int seconds) {
        if (seconds < 0) {
            // indicates that we're out of attempts
            reconnectButton.setMessage(new TranslatableText("text.autoreconnect.disconnect.reconnect_failed")
                .styled(s -> s.withColor(Formatting.RED)));
            reconnectButton.active = false;
        } else {
            reconnectButton.setMessage(new TranslatableText("text.autoreconnect.disconnect.reconnect_in", seconds)
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
