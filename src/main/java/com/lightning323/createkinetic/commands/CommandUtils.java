package com.lightning323.createkinetic.commands;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

public class CommandUtils {
    /**
     * Parses and executes a command string using the given source.
     *
     * @param source The command source.
     * @param command The command to execute (without a leading '/').
     * @return The number of affected entities or a result code.
     */
    public static int executeParsedCommand(CommandSourceStack source, String command) {
        // Use the server's command dispatcher
        MinecraftServer server = source.getServer();
        var dispatcher = server.getCommands().getDispatcher();

        // Parse the command string (if a leading slash exists, remove it)
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        ParseResults<CommandSourceStack> parseResults = dispatcher.parse(command, source);
        try {
            // Execute the parsed command and return the result
            return dispatcher.execute(parseResults);
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.literal("Error executing command: " + e.getMessage()));
            return 0;
        }
    }

//    public static String executeParsedCommand(CommandSourceStack source, String command) {
//        MinecraftServer server = source.getServer();
//        var dispatcher = server.getCommands().getDispatcher();
//
//        if (command.startsWith("/")) {
//            command = command.substring(1);
//        }
//
//        // Wrap original source with capturing source
//        CapturingCommandSourceStack capturingSource = new CapturingCommandSourceStack(source);
//
//        ParseResults<CommandSourceStack> parseResults = dispatcher.parse(command, capturingSource);
//        try {
//            dispatcher.execute(parseResults);
//        } catch (CommandSyntaxException e) {
//            return "Error executing command: " + e.getMessage();
//        }
//
//        return capturingSource.getCapturedMessages();
//    }

}
