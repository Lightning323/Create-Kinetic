package org.lightning323.create_kinetic.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lightning323.create_kinetic.propulsion.ThrusterBlockEntity;

import static org.lightning323.create_kinetic.CreateKinetic.MODID;

public class KineticBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ThrusterBlockEntity>> THRUSTER =
            BLOCK_ENTITIES.register("thruster",
                    () -> BlockEntityType.Builder.of(
                            ThrusterBlockEntity::new,
                            KineticBlocks.THRUSTER.get()
                    ).build(null)
            );
}