package autoreconnect.mixin;

import autoreconnect.AutoReconnect;
import autoreconnect.reconnect.MultiplayerReconnectHandler;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin {
    @Inject(at = @At("TAIL"), method = "connect(Lnet/minecraft/client/network/ServerInfo;)V")
    private void connect(ServerInfo entry, CallbackInfo info) {
        AutoReconnect.getInstance().setReconnectHandler(new MultiplayerReconnectHandler(entry));
    }
}
