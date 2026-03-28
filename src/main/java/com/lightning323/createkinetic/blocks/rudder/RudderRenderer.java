package com.lightning323.createkinetic.blocks.rudder;

import com.lightning323.createkinetic.registries.KineticPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Vector3f;


public class RudderRenderer extends KineticBlockEntityRenderer<RudderBlockEntity> {
    public RudderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(RudderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        BlockState state = be.getBlockState();
        // Use your custom property to find the attachment direction
        Direction facing = state.getValue(BlockStateProperties.FACING);

        // 1. DO NOT call super.renderSafe if it is spinning your base.
        // Instead, render the SHAFT manually on the back.
        CachedBuffers.partial(AllPartialModels.SHAFT, state)
                .rotateToFace(facing.getOpposite()) // Points shaft into the wall
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        // 2. Skip manual blade rendering if Flywheel is handling it
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        // 3. Render the Flap/Blade
        // Use the actual kinetic angle from the BE
        float angle = 0;

        kineticRotationTransform(
                CachedBuffers.partial(KineticPartialModels.RUDDER_COPPER_BLADE, state),
                be,
                facing.getAxis(),
                angle,
                light
        ).renderInto(ms, buffer.getBuffer(RenderType.cutout()));
    }
}
