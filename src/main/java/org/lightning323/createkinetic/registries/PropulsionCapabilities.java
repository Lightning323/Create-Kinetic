package org.lightning323.createkinetic.registries;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.content.heat.IHeatConsumer;
import org.lightning323.createkinetic.content.heat.IHeatSource;

public class PropulsionCapabilities {
    public static final BlockCapability<IHeatSource, Direction> HEAT_SOURCE =
        BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(CreateKinetic.ID, "heat_source"), IHeatSource.class);

    public static final BlockCapability<IHeatConsumer, Direction> HEAT_CONSUMER =
        BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(CreateKinetic.ID, "heat_consumer"), IHeatConsumer.class);
}

