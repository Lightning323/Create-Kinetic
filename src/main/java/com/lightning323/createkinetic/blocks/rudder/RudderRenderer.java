package com.lightning323.createkinetic.blocks.rudder;

import com.lightning323.createkinetic.registries.KineticPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class RudderRenderer extends KineticBlockEntityRenderer<RudderBlockEntity> {
    public RudderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(RudderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        //If visualization is enabled, skip rendering
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        BlockState state = be.getBlockState();
        Direction facing = state.getValue(BlockStateProperties.FACING);

        // 1. Render the Shaft (Static base)
        // Note: rotateToFace usually handles the orientation based on SOUTH as default
        CachedBuffers.partial(AllPartialModels.SHAFT_HALF, state)
                .rotateToFace(Direction.SOUTH)
                .light(light)
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        // 2. Render the Blade (Dynamic/Animated)
        ms.pushPose();

// 1. Move to the center of the block (8, 8, 8 in pixels)
        ms.translate(0.5f, 0.5f, 0.5f);

// 2. Apply the Identity Orientation (Facing + Plane Rotation)
// Since rudderIdentityRotation is a Quaternionf, use mulPose:
        ms.mulPose(be.rudderIdentityRotation);

// 3. Apply the Animation (The Y-axis swing)
// We lerp the angle to keep it smooth between ticks
        be.animateRenderAngle();
        ms.mulPose(com.mojang.math.Axis.YP.rotation(be.renderAngle));

// 4. Move back from the center
        ms.translate(-0.5f, -0.5f, -0.5f);

// 5. Render the model
        CachedBuffers.partial(KineticPartialModels.RUDDER_COPPER_BLADE, state)
                .light(light)
                .overlay(overlay)
                .renderInto(ms, buffer.getBuffer(RenderType.cutout()));

        ms.popPose();
    }
}