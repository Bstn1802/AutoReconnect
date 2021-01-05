package net.autoreconnect.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.autoreconnect.AutoReconnect.*;

@Mixin(DisconnectedScreen.class)
public class MixinDisconnectedScreen
{
	@Shadow
	private int reasonHeight;

	// make this screen closable by pressing escape
	@Inject(at = @At("RETURN"), method = "shouldCloseOnEsc", cancellable = true)
	private void shouldCloseOnEsc(CallbackInfoReturnable<Boolean> info)
	{
		info.setReturnValue(true);
	}

	// render the text overlay
	@Inject(at = @At("RETURN"), method = "render")
	private void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info)
	{
		Window window = MinecraftClient.getInstance().getWindow();
		TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
		String text = getMessage();
		renderer.draw(matrices, text,
			(window.getScaledWidth() - renderer.getWidth(text)) / 2F, // centered
			(window.getScaledHeight() - reasonHeight) / 2F - 9 * 4, // 9 * 2 higher than the title which is 9 * 2 higher than the disconnect reason
			getColor());
	}
}