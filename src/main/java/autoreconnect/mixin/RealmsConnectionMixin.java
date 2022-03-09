package autoreconnect.mixin;

import autoreconnect.AutoReconnect;
import autoreconnect.reconnect.RealmsReconnectHandler;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.realms.RealmsConnection;
import net.minecraft.client.realms.dto.RealmsServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RealmsConnection.class)
public class RealmsConnectionMixin {
    @Inject(at = @At("HEAD"), method = "connect")
    private void connect(RealmsServer server, ServerAddress address, CallbackInfo info) {
        AutoReconnect.getInstance().setReconnectHandler(new RealmsReconnectHandler(server));
    }
}
