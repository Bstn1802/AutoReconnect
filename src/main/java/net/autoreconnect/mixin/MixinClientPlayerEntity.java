package net.autoreconnect.mixin;

import net.autoreconnect.ClientCommands;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.autoreconnect.Util.err;
import static net.autoreconnect.Util.send;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity
{
	@Shadow @Final
	protected MinecraftClient client;

	@Shadow @Final
	public ClientPlayNetworkHandler networkHandler;


	@Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
	private void sendChatMessage(String message, CallbackInfo info)
	{
		if (message.charAt(0) == '/')
		{
			String command = message.substring(1);
			if(ClientCommands.contains(command))
			{
				// prevent message from being sent
				info.cancel();
				try
				{
					networkHandler.getCommandDispatcher().execute(command, new ClientCommandSource(networkHandler, client));
				}
				catch (Exception ex)
				{
					send(err(ex));
				}
			}
		}
	}
}