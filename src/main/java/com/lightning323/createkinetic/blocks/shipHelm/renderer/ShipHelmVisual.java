package com.lightning323.createkinetic.blocks.shipHelm.renderer;

import com.lightning323.createkinetic.blocks.shipHelm.ShipHelmBlock;
import com.lightning323.createkinetic.blocks.shipHelm.ShipHelmBlockEntity;
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
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class ShipHelmVisual extends KineticBlockEntityVisual<ShipHelmBlockEntity> implements SimpleDynamicVisual {

    protected RotatingInstance shaft;
    protected OrientedInstance wheel;

    public ShipHelmVisual(VisualizationContext context, ShipHelmBlockEntity blockEntity, float partialTicks) {
        super(context, blockEntity, partialTicks);

        BlockState blockState = blockEntity.getBlockState();
        if (!(blockState.getBlock() instanceof ShipHelmBlock helmBlock)) return;
        var woodType = helmBlock.getWoodTypeEnum();

        // Initialize Shaft
        this.shaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF))
                .createInstance();
        shaft.setup(blockEntity)
                .setPosition(getVisualPosition())
                .rotateToFace(Direction.SOUTH, Direction.DOWN)
                .setChanged();

        // Initialize Wheel
        this.wheel = instancerProvider().instancer(InstanceTypes.ORIENTED,
                        Models.partial(KineticPartialModels.getHelmWheel(woodType)))
                .createInstance();

        animate();
    }

    @Override
    public void beginFrame(Context ctx) {
        animate();
    }

    private void animate() {
        shaft.setup(blockEntity).setChanged();

        BlockState state = blockEntity.getBlockState();
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        float yRot = facing.toYRot();



        // 2. Setup the rotation quaternion
        Quaternionf rotation = new Quaternionf();

        blockEntity.animateHelmRotation();
        rotation.rotateY((float) Math.toRadians(-yRot));
        switch (facing){
            case NORTH -> rotation.rotateLocalZ(blockEntity.renderHelmRotation);
            case SOUTH -> rotation.rotateLocalZ(-blockEntity.renderHelmRotation);
            case EAST -> rotation.rotateLocalX(blockEntity.renderHelmRotation);
            case WEST -> rotation.rotateLocalX(-blockEntity.renderHelmRotation);
        }


        Vector3f pos = new Vector3f(getVisualPosition().getX(), getVisualPosition().getY()+0.625f, getVisualPosition().getZ());
        wheel.position(pos).rotation(rotation)
                .setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(shaft, wheel);
    }

    @Override
    protected void _delete() {
        shaft.delete();
        wheel.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(shaft);
        consumer.accept(wheel);
    }
}