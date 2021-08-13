package autoreconnect.mixin;

import autoreconnect.event.ScreenChangedListener;
import autoreconnect.event.ServerEntryChangedListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.objectweb.asm.Opcodes.PUTFIELD;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    public Screen currentScreen;

    @Inject(at = @At("HEAD"), method = "setCurrentServerEntry")
    private void setCurrentServerEntry(ServerInfo serverInfo, CallbackInfo info) {
        ServerEntryChangedListener.EVENT.invoker().onServerEntryChanged(serverInfo);
    }

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", opcode = PUTFIELD), method = "setScreen")
    private void setScreen(Screen newScreen, CallbackInfo info) {
        // old and new screen must not be the same type, actually happens very often for some reason
        if ((currentScreen == null ? null : currentScreen.getClass()) != (newScreen == null ? null : newScreen.getClass())) {
            ScreenChangedListener.EVENT.invoker().onScreenChanged(currentScreen, newScreen);
        }
    }
}