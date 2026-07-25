package org.lightning323.createkinetic.registries;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.lightning323.createkinetic.debug.PropulsionDebug;

import java.util.Set;

public class PropulsionCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> propulsionCommand = Commands.literal("propulsion")
            .requires(source -> source.hasPermission(2));

        registerFloatKeys(propulsionCommand);

        dispatcher.register(propulsionCommand);
    }

    private static void registerFloatKeys(LiteralArgumentBuilder<CommandSourceStack> propulsionCommand) {
        Set<String> floatKeys = PropulsionDebug.getFloatKeys();
        if (floatKeys.size() > 0) {
            LiteralArgumentBuilder<CommandSourceStack> varNode = Commands.literal("var");
            for (String key : floatKeys) {
                varNode.then(Commands.literal(key)
                    .then(Commands.argument("val", FloatArgumentType.floatArg())
                    .executes(ctx -> setVar(key, FloatArgumentType.getFloat(ctx, "val")))));
            }
            propulsionCommand.then(varNode);
        }
    }

    private static int setVar(String key, float val) {
        PropulsionDebug.registerFloat(key, val);
        return 1;
    }
}
