package org.lightning323.create_kinetic;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = CreateKinetic.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue JET_THRUST_MULTIPLIER = BUILDER.comment("Jet Thruster Thrust Multiplier").defineInRange("thrustMult", 5, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue JET_AIRFLOW_MULTIPLIER = BUILDER.comment("Jet Thruster Airflow Multiplier").defineInRange("airflowMult", 5, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
    public static double thrustMultiplier;
    public static double airflowMultiplier;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        thrustMultiplier = JET_THRUST_MULTIPLIER.get();
        airflowMultiplier = JET_AIRFLOW_MULTIPLIER.get();
    }
}
