package autoreconnect.mixin;

import autoreconnect.AutoReconnect;
import autoreconnect.reconnect.SingleplayerReconnectHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.objectweb.asm.Opcodes.PUTFIELD;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    private Screen currentScreen;

    @Inject(at = @At("HEAD"), method = "startIntegratedServer")
    private void startIntegratedServer(String worldName, CallbackInfo info) {
        AutoReconnect.getInstance().setReconnectHandler(new SingleplayerReconnectHandler(worldName));
    }

    @Inject(method = "setScreen", at = @At(value = "FIELD", opcode = PUTFIELD,
        target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"))
    private void setScreen(Screen newScreen, CallbackInfo info) {
        AutoReconnect.getInstance().onScreenChanged(currentScreen, newScreen);
    }
}
