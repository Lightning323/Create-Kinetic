package com.lightning323.createkinetic.blocks.rudder;

import com.lightning323.createkinetic.registries.KineticPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;


public class RudderVisual extends KineticBlockEntityVisual<RudderBlockEntity> {

    protected final OrientedInstance blade;
    protected final RotatingInstance shaft;
    protected final Direction facing;

    public RudderVisual(VisualizationContext context, RudderBlockEntity blockEntity, float partialTicks) {
        super(context, blockEntity, partialTicks);

        BlockState state = blockEntity.getBlockState();
        this.facing = state.getValue(BlockStateProperties.FACING);

        // 1. The Shaft (Static, pointing into the wall)
        // We use RotatingInstance but set speed to 0 so it stays still like a normal shaft
        this.shaft = instancerProvider()
                .instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF))
                .createInstance();

        shaft.setup(blockEntity)
                .rotateToFace(Direction.SOUTH, facing.getOpposite()) // South is the "back" of the shaft model
                .setPosition(getVisualPosition())
                .setChanged();

        // 2. The Blade (The part that actually moves)
        this.blade = instancerProvider()
                .instancer(InstanceTypes.ORIENTED, Models.partial(KineticPartialModels.RUDDER_COPPER_BLADE))
                .createInstance();

        blade.position(getVisualPosition())
                .setChanged();

        updateRotation(partialTicks);
    }

    @Override
    public void update(float partialTicks) {
        super.update(partialTicks);
        updateRotation(partialTicks);
    }

    private void updateRotation(float partialTicks) {
//        BlockState blockState = movementContext.state;
//        float angle =  KineticBlockEntityVisual.rotationAxis(blockState);

        // Align the blade rotation to the block's facing axis
        blade.rotation(facing.getRotation().rotateAxis((float) Math.toRadians(0), 0, 1, 0))
                .setChanged();
    }

    @Override
    public void updateLight(float partialTicks) {

        relight(blade, shaft);
    }

    @Override
    protected void _delete() {

        blade.delete();
        shaft.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {

    }
}