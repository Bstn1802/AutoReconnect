package net.autoreconnect.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.autoreconnect.AutoReconnect.*;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient
{
	private ServerInfo lastServerEntry;

	@Inject(at = @At("HEAD"), method = "setCurrentServerEntry")
	private void setCurrentServerEntry(ServerInfo info, CallbackInfo ci)
	{
		if (info != null)
		{
			lastServerEntry = info;
		}
	}

	@Inject(at = @At("RETURN"), method = "openScreen")
	private void openScreen(Screen screen, CallbackInfo info)
	{
		System.out.println(screen == null ? null : screen.getClass().getSimpleName());
		//TODO interpret disconnect reason
		if (screen instanceof DisconnectedScreen)
		{
			if (attempt < 0) return;
			switch (attempt++)
			{
				case 0:
					startCountdown(3);
					break;
				case 1:
					startCountdown(10);
					break;
				case 2:
					startCountdown(60);
					break;
				case 3:
					startCountdown(300);
					break;
				default:
					attempt = -1;
			}
		}
		else if (screen instanceof MultiplayerScreen || MinecraftClient.getInstance().player != null)
		{
			//TODO find better conditions to reset
			reset();
		}
	}

	@Inject(at = @At("RETURN"), method = "tick")
	private void tick(CallbackInfo info)
	{
		//TODO find better way to call connect on main thread from after timer countdown finished
		if (connect)
		{
			connect = false;
			MinecraftClient mc = MinecraftClient.getInstance();
			if (lastServerEntry == null)
			{
				attempt = -1;
				return;
			}
			mc.disconnect();
			mc.openScreen(new ConnectScreen(new TitleScreen(), mc, lastServerEntry));
		}
	}
}