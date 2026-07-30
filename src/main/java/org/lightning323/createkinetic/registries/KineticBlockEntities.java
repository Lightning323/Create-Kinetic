package org.lightning323.createkinetic.registries;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.KineticRegistrate;
import org.lightning323.createkinetic.content.blocks.joystick.JoystickBlockEntity;
import org.lightning323.createkinetic.content.blocks.joystick.JoystickRenderer;
import org.lightning323.createkinetic.content.blocks.reaction_wheel.ReactionWheelBlockEntity;
import org.lightning323.createkinetic.content.blocks.reaction_wheel.ReactionWheelRenderer;
import org.lightning323.createkinetic.content.blocks.sable_track.SableTrackBlockEntity;
import org.lightning323.createkinetic.content.blocks.sable_track.SableTrackRenderer;
import org.lightning323.createkinetic.content.heat.burners.liquid.LiquidBurnerBlockEntity;
import org.lightning323.createkinetic.content.heat.burners.solid.SolidBurnerBlockEntity;
import org.lightning323.createkinetic.content.heat.engine.StirlingEngineBlockEntity;
import org.lightning323.createkinetic.content.platinum.PlatinumFluidTankBlockEntity;
import org.lightning323.createkinetic.content.platinum.PlatinumFluidVesselBlockEntity;
import org.lightning323.createkinetic.content.redstone_converter.RedstoneConverterBlockEntity;
import org.lightning323.createkinetic.content.thruster.ion_thruster.IonThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.thruster.ThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.thruster.creative_thruster.CreativeThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.vector_thruster.VectorThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.vector_thruster.creative_vector_thruster.CreativeVectorThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.vector_thruster.liquid_vector_thruster.LiquidVectorThrusterBlockEntity;
import org.lightning323.createkinetic.content.wing.PropulsionCopycatWingBlockEntity;

public class KineticBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreateKinetic.ID);

    public static final KineticRegistrate REGISTRATE = CreateKinetic.getRegistrate();
    public static final BlockEntityEntry<JoystickBlockEntity> JOYSTICK = REGISTRATE
            .blockEntity("joystick", JoystickBlockEntity::new)
            .validBlocks(KineticBlocks.JOYSTICK)
            .renderer(() -> JoystickRenderer::new)
            .register();
    public static final BlockEntityEntry<ReactionWheelBlockEntity> GYROSCOPE = REGISTRATE
            .blockEntity("gyroscope", ReactionWheelBlockEntity::new)
            .validBlocks(KineticBlocks.REACTION_WHEEL)
            .renderer(() -> ReactionWheelRenderer::new)
            .register();
    public static final BlockEntityEntry<SableTrackBlockEntity> SABLE_TRACK = REGISTRATE.blockEntity("sable_track", SableTrackBlockEntity::new)
            .validBlocks(new NonNullSupplier[]{KineticBlocks.TRACK_MOUNT})
            .renderer(() -> (BlockEntityRendererProvider<SableTrackBlockEntity>) SableTrackRenderer::new)
            .register();

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ThrusterBlockEntity>> THRUSTER_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register("thruster_block_entity",
            () -> BlockEntityType.Builder.of((pos, state) -> new ThrusterBlockEntity(pos, state), KineticBlocks.THRUSTER_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeThrusterBlockEntity>> CREATIVE_THRUSTER_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register("creative_thruster_block_entity",
            () -> BlockEntityType.Builder.of((pos, state) -> new CreativeThrusterBlockEntity(pos, state), KineticBlocks.CREATIVE_THRUSTER_BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeVectorThrusterBlockEntity>> CREATIVE_VECTOR_THRUSTER_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register("creative_vector_thruster_block_entity",
            () -> BlockEntityType.Builder.of((pos, state) -> new CreativeVectorThrusterBlockEntity(pos, state), KineticBlocks.CREATIVE_VECTOR_THRUSTER_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IonThrusterBlockEntity>> ION_THRUSTER_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register("ion_thruster_block_entity",
            () -> BlockEntityType.Builder.of((pos, state) -> {
                if (state.getBlock() == KineticBlocks.VECTOR_THRUSTER_BLOCK.get()) {
                    return new VectorThrusterBlockEntity(pos, state);
                }
                return new IonThrusterBlockEntity(pos, state);
            }, KineticBlocks.ION_THRUSTER_BLOCK.get(), KineticBlocks.VECTOR_THRUSTER_BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquidVectorThrusterBlockEntity>> LIQUID_VECTOR_THRUSTER_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register("liquid_vector_thruster_block_entity",
            () -> BlockEntityType.Builder.of((pos, state) -> new LiquidVectorThrusterBlockEntity(pos, state),
                KineticBlocks.LIQUID_VECTOR_THRUSTER_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RedstoneConverterBlockEntity>> REDSTONE_CONVERTER_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register("redstone_converter_block_entity",
            () -> BlockEntityType.Builder.of((pos, state) -> new RedstoneConverterBlockEntity(pos, state),
                KineticBlocks.REDSTONE_CONVERTER_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SolidBurnerBlockEntity>> SOLID_BURNER_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register("solid_burner_block_entity",
            () -> BlockEntityType.Builder.of((pos, state) -> new SolidBurnerBlockEntity(pos, state), KineticBlocks.SOLID_BURNER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LiquidBurnerBlockEntity>> LIQUID_BURNER_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register("liquid_burner_block_entity",
            () -> BlockEntityType.Builder.of((pos, state) -> new LiquidBurnerBlockEntity(pos, state), KineticBlocks.LIQUID_BURNER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StirlingEngineBlockEntity>> STIRLING_ENGINE_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register("stirling_engine_block_entity",
            () -> BlockEntityType.Builder.of((pos, state) -> new StirlingEngineBlockEntity(pos, state), KineticBlocks.STIRLING_ENGINE_BLOCK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PropulsionCopycatWingBlockEntity>> COPYCAT_WING_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register("copycat_wing_block_entity",
            () -> BlockEntityType.Builder.of(
                (pos, state) -> new PropulsionCopycatWingBlockEntity(pos, state),
                KineticBlocks.COPYCAT_WING.get(),
                KineticBlocks.COPYCAT_WING_8.get(),
                KineticBlocks.COPYCAT_WING_12.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlatinumFluidTankBlockEntity>> PLATINUM_FLUID_TANK_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register("platinum_fluid_tank_block_entity",
            () -> BlockEntityType.Builder.of((pos, state) -> new PlatinumFluidTankBlockEntity(pos, state), KineticBlocks.PLATINUM_FLUID_TANK.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PlatinumFluidVesselBlockEntity>> PLATINUM_FLUID_VESSEL_BLOCK_ENTITY =
        BLOCK_ENTITY_TYPES.register("platinum_fluid_vessel_block_entity",
            () -> BlockEntityType.Builder.of((pos, state) -> new PlatinumFluidVesselBlockEntity(pos, state), KineticBlocks.PLATINUM_FLUID_VESSEL.get()).build(null));

    public static void register(IEventBus modBus) {
        BLOCK_ENTITY_TYPES.register(modBus);
    }
}
