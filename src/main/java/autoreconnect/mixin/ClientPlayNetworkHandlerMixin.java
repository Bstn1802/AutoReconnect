package autoreconnect.mixin;

import autoreconnect.AutoReconnect;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.apache.logging.log4j.core.jmx.Server;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow
    @Final
    private ServerInfo serverInfo;

    @Inject(at = @At("TAIL"), method = "onGameJoin")
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        AutoReconnect.getInstance().onGameJoined(serverInfo);
    }
}
