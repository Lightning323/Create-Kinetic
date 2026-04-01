package com.lightning323.createkinetic.blocks.rudder;

import com.lightning323.createkinetic.items.RudderBladeItem;
import com.lightning323.createkinetic.registries.KineticPartialModels;
import com.lightning323.createkinetic.utils.MiscUtils;
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
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.lwjgl.system.MathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static oshi.util.platform.windows.WmiQueryHandler.createInstance;


public class RudderVisual extends KineticBlockEntityVisual<RudderBlockEntity> implements SimpleDynamicVisual {

    protected OrientedInstance blade;
    private RudderBladeItem bladeItem;

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
        animate();
    }


    @Override
    public void beginFrame(Context ctx) {
        animate();
    }

    private void animate() {
        shaft.setup(blockEntity).setChanged();

        if (blade != null) {
            blockEntity.animateRenderAngle();
            blade.rotation(blockEntity.rudderIdentityRotation);//set to identity
            blade.rotate(blockEntity.renderAngle, Direction.Axis.Y);//rotate
            blade.setChanged();
        }
    }

    @Override
    public void update(float pt) {
        super.update(pt);
        // Check if the item in the BlockEntity changed
        if (blockEntity.rudderBlade != bladeItem) {
            // 1. ALWAYS delete the old instance if it exists
            if (blade != null) {
                blade.delete();
                blade = null; // Clear reference
            }
            if (blockEntity.rudderBlade != null) {
                //We set the rendertype to "render_type": "minecraft:cutout", in the model json file
                PartialModel model = KineticPartialModels.RUDDER_COPPER_BLADE;
                if (blockEntity.rudderBlade.type == RudderBladeItem.BladeType.IRON) {
                    model = KineticPartialModels.RUDDER_IRON_BLADE;
                }

                blade = instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(model))
                        .createInstance();
                blade.position(getVisualPosition())
                        .rotation(blockEntity.rudderIdentityRotation)
                        .setChanged();
                relight(blade);
            }
            bladeItem = blockEntity.rudderBlade;
        }
    }

    @Override
    public void updateLight(float partialTick) {
        relight(shaft);
        if (blade != null) {
            relight(blade);
        }
    }

    @Override
    protected void _delete() {
        if (blade != null) blade.delete();
        shaft.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(shaft);
    }
}