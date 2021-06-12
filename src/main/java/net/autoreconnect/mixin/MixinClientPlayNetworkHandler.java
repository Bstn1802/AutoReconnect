package net.autoreconnect.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.autoreconnect.ClientCommands;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
	@Shadow
	private CommandDispatcher<CommandSource> commandDispatcher;

	@SuppressWarnings("unchecked")
	@Inject(method = "<init>", at = @At("RETURN"))
	private void init(MinecraftClient client, Screen screen, ClientConnection connection, GameProfile profile, CallbackInfo info) {
		// register commands to the initial dispatcher
		ClientCommands.register((CommandDispatcher<ClientCommandSource>) (Object) commandDispatcher);
	}

	@SuppressWarnings("unchecked")
	@Inject(method = "onCommandTree", at = @At("TAIL"))
	private void onCommandTree(CommandTreeS2CPacket packet, CallbackInfo info) {
		// register commands to the new dispatcher
		ClientCommands.register((CommandDispatcher<ClientCommandSource>) (Object) commandDispatcher);
	}
}