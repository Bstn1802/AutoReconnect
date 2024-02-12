package autoreconnect.mixin;

import autoreconnect.AutoReconnect;
import autoreconnect.reconnect.MultiplayerReconnectStrategy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {
    @Inject(at = @At("HEAD"), method = "connect(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;)V")
    private void connect(MinecraftClient client, ServerAddress address, ServerInfo serverInfo, CallbackInfo info) {
        if (serverInfo == null) return;
        AutoReconnect.getInstance().setReconnectHandler(new MultiplayerReconnectStrategy(serverInfo));
    }
}
