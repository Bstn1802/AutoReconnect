package net.autoreconnect.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.autoreconnect.AutoReconnect.*;
import static net.autoreconnect.Util.log;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
	@Shadow
	public Screen currentScreen;

	@Inject(at = @At("HEAD"), method = "setCurrentServerEntry")
	private void setCurrentServerEntry(ServerInfo serverInfo, CallbackInfo info) {
		//save last known non-null server entry
		if (serverInfo != null) {
			lastServerEntry = serverInfo;
		}
	}

	@Inject(at = @At("HEAD"), method = "tick")
	private void tick(CallbackInfo info) {
		// if not paused, decrements countdown until its negative, succeeds if its 0
		if (ticks >= 0 && --ticks == 0) {
			if (lastServerEntry == null) {
				resetAttempts();
			}
			else {
				MinecraftClient mc = MinecraftClient.getInstance();
				MultiplayerScreen screen = new MultiplayerScreen(new TitleScreen());
				ConnectScreen.connect(screen, mc, ServerAddress.parse(lastServerEntry.address), lastServerEntry);
			}
		}
	}

	@Inject(at = @At("INVOKE"), method = "openScreen")
	private void openScreen(Screen newScreen, CallbackInfo info) {
		// old and new screen must not be the same type, actually happens very often for some reason
		if ((currentScreen == null ? null : currentScreen.getClass()) != (newScreen == null ? null : newScreen.getClass())) {
			if (currentScreen instanceof DisconnectedScreen && ( // exited disconnect screen using...
				newScreen instanceof MultiplayerScreen || // ...cancel button on disconnect screen
					newScreen instanceof TitleScreen || // ...escape key
					newScreen != null && newScreen.getClass().getSimpleName().equals("AuthScreen")) || // ...AuthMe re-authenticate button
				(currentScreen instanceof ConnectScreen && !(newScreen instanceof DisconnectedScreen))) // connection successful or cancelled using cancel button on connect screen
			{
				resetAttempts();
			}
			// player got disconnected
			else if (newScreen instanceof DisconnectedScreen) {
				// if last known server is not null and next attempt is configured
				if (lastServerEntry != null && ++attempt < delayList.length) {
					ticks = delayList[attempt] * 20;
				}
				else {
					resetAttempts();
				}
				log("lastServerEntry: %s, attempt: %d", lastServerEntry == null ? "null" : lastServerEntry.name, attempt);
			}
		}
	}
}