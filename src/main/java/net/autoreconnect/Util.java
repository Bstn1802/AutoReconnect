package net.autoreconnect;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import static net.minecraft.util.Formatting.RED;

public class Util
{
	// sends message to player if in game or logs to console with prefix '[<MOD_NAME>]'
	public static void send(Text text)
	{
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null)
		{
			// if player is not in game then print to console in error or default stream depending on weather the text is red or not
			Logger logger = LogManager.getLogger("AutoReconnect");
			if (Objects.equals(text.getStyle().getColor(), TextColor.fromFormatting(RED)))
			{
				logger.error("[AutoReconnect] " + text.getString());
			}
			else
			{
				logger.info("[AutoReconnect] " + text.getString());
			}
		}
		else player.sendMessage(text, false);
	}

	// logs in console with prefix '[<MOD_NAME>]'
	public static void log(String format, Object... args)
	{
		LogManager.getLogger("AutoReconnect").info("[AutoReconnect] " + String.format(format, args));
	}

	// easy text creation methods
	public static Text colored(String text, Formatting formatting)
	{
		return new LiteralText(text).formatted(formatting);
	}

	public static Text err(Exception ex)
	{
		return colored(ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage(), RED);
	}
}