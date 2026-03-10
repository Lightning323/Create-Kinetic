package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.blocks.sailPulley.SailContraption;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

import static com.lightning323.createkinetic.Createkinetic.MOD_ID;

public class KineticContraptions {
    public static final Holder.Reference<ContraptionType> SAIL = register("sail", SailContraption::new);

    private static Holder.Reference<ContraptionType> register(String name, Supplier<? extends Contraption> factory) {
        ContraptionType type = new ContraptionType(factory);
        return Registry.registerForHolder(CreateBuiltInRegistries.CONTRAPTION_TYPE, new ResourceLocation(MOD_ID, name), type);
    }

    public static void init() {
    }
}
