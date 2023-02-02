package autoreconnect.mixin;

import autoreconnect.AutoReconnect;
import autoreconnect.reconnect.SingleplayerReconnectStrategy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.objectweb.asm.Opcodes.PUTFIELD;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow private Screen currentScreen;

    @Inject(method = "startIntegratedServer", at = @At("HEAD"))
    private void startIntegratedServer(String levelName, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, boolean newWorld, CallbackInfo info) {
        AutoReconnect.getInstance().setReconnectHandler(new SingleplayerReconnectStrategy(levelName));
    }

    @Inject(method = "setScreen", at = @At(value = "FIELD", opcode = PUTFIELD,
        target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"))
    private void setScreen(Screen newScreen, CallbackInfo info) {
        AutoReconnect.getInstance().onScreenChanged(currentScreen, newScreen);
    }
}
