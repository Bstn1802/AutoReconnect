package net.autoreconnect.mixin;

import net.autoreconnect.AutoReconnect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

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
		//TODO interpret disconnect reason
		//TODO revalidate session if needed
		if (screen instanceof DisconnectedScreen)
		{
			System.out.println(attempt);
			int time;
			if (attempt < 0) return;
			if(config.rejoinTime.length>attempt)
				time=config.rejoinTime[attempt];
			else
				time=config.rejoinTime[config.rejoinTime.length-1];
			startCountdown(time);
			attempt++;
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
			mc.openScreen(new ConnectScreen(new MultiplayerScreen(new TitleScreen()), mc, lastServerEntry));
		}
	}
}