package net.autoreconnect;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.network.ClientCommandSource;

import java.util.*;

import static net.autoreconnect.AutoReconnect.MOD_ID;

public final class ClientCommands
{
	private ClientCommands() { }

	private static final List<LiteralArgumentBuilder<ClientCommandSource>> commands = new ArrayList<>();

	public static boolean contains(String command)
	{
		// checks if command starts with '<MOD_ID> ' and looks for a command literal to match the second word in the command
		return command.startsWith(MOD_ID + " ") && commands.stream().map(LiteralArgumentBuilder::getLiteral).anyMatch(command.substring(MOD_ID.length() + 1).split(" ", 2)[0]::equals);
	}

	// register commands more or less the usual way
	public static void register(LiteralArgumentBuilder<ClientCommandSource> command)
	{
		commands.add(command);
	}

	// variants of static methods literal and argument from net.minecraft.server.command.CommandManager replacing ServerCommandSource with ClientCommandSource
	public static LiteralArgumentBuilder<ClientCommandSource> literal(String literal)
	{
		return LiteralArgumentBuilder.literal(literal);
	}

	public static <T> RequiredArgumentBuilder<ClientCommandSource, T> argument(String name, ArgumentType<T> type)
	{
		return RequiredArgumentBuilder.argument(name, type);
	}

	// actually register the commands and add the prefix node
	public static void register(CommandDispatcher<ClientCommandSource> dispatcher)
	{
		commands.stream().map(literal(MOD_ID)::then).forEach(dispatcher::register);
	}
}