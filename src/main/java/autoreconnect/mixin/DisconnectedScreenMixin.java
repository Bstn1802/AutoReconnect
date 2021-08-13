package autoreconnect.mixin;

import autoreconnect.event.DisconnectListener;
import autoreconnect.event.DisconnectScreenRenderListener;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DisconnectedScreen.class)
public class DisconnectedScreenMixin {
    @Shadow
    private int reasonHeight;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(Screen parent, Text title, Text reason, CallbackInfo info) {
        DisconnectListener.EVENT.invoker().onDisconnected(reason);
    }

    // make this screen closable by pressing escape
    @Inject(at = @At("RETURN"), method = "shouldCloseOnEsc", cancellable = true)
    private void shouldCloseOnEsc(CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(true);
    }

    @Inject(at = @At("RETURN"), method = "render")
    private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        DisconnectScreenRenderListener.EVENT.invoker().onRender(matrices, reasonHeight);
    }
}