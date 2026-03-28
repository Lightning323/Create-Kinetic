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
import dev.engine_room.flywheel.lib.material.Materials;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.engine_room.flywheel.lib.visual.util.SmartRecycler;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.function.Consumer;


public class RudderVisual extends KineticBlockEntityVisual<RudderBlockEntity> implements SimpleDynamicVisual {

    protected final OrientedInstance blade;
    protected final RotatingInstance shaft;
    protected final Direction facing;

    public RudderVisual(VisualizationContext context, RudderBlockEntity blockEntity, float partialTicks) {
        super(context, blockEntity, partialTicks);
        this.facing = blockState.getValue(BlockStateProperties.FACING);

        this.shaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF))
                .createInstance();
        shaft.setup(blockEntity)
                .setPosition(getVisualPosition())
                .rotateToFace(Direction.SOUTH, facing.getOpposite())
                .setChanged();

        //We set the rendertype to "render_type": "minecraft:cutout", in the model json file
        this.blade = instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(KineticPartialModels.RUDDER_COPPER_BLADE))
                .createInstance();

// 1. Replicate your blockstate logic using JOML
        float xRot = 90;
        float yRot = 180;
        switch (facing) {
            case SOUTH -> yRot = 0;
            case WEST  -> yRot = 270;
            case EAST  -> yRot = 90;
            case UP    -> xRot = 270 + 90; // 360 or 0
            case DOWN  -> xRot = 90 + 90;  // 180
        }
        Quaternionf q = new Quaternionf();
        // Minecraft Blockstate order: Y then X
        q.rotationY((float) Math.toRadians(yRot));
        q.rotateX((float) Math.toRadians(xRot));

        blade.position(getVisualPosition())
                .rotation(q.x, q.y, q.z, q.w)
                .setChanged();

        animate();
    }

    @Override
    public void beginFrame(Context ctx) {
        animate();
    }

    private void animate() {
        shaft.setup(blockEntity).setChanged();

        float time = net.createmod.catnip.animation.AnimationTickHolder.getRenderTime();
        float testAngle = time * 0.001f;
        blade.rotate(testAngle, Direction.Axis.Y).setChanged();
    }

//    @Override
//    public void update(float pt) {
//        shaft.setup(blockEntity)
//                .setChanged();
//    }

    @Override
    public void updateLight(float partialTick) {
        relight(shaft, blade);
    }

    @Override
    protected void _delete() {
        blade.delete();
        shaft.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(shaft);
    }
}