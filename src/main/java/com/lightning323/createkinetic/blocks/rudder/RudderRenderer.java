package com.lightning323.createkinetic.blocks.rudder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class RudderRenderer extends KineticBlockEntityRenderer<RudderBlockEntity> {
    public RudderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(RudderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if (VisualizationManager.supportsVisualization(be.getLevel()))
            return;

        BlockState state = be.getBlockState();

        // 1. Setup the shared base orientation
        ms.pushPose();
        ms.translate(0.5f, 0.5f, 0.5f);
        ms.mulPose(be.rudderIdentityRotation); // This puts 'Local Up' where it needs to be

//        // --- RENDER SHAFT ---//TODO: Fix this
//        // We push another pose so the shaft doesn't get the blade's swing
//        ms.pushPose();
//        // If your SHAFT_HALF model is designed to point UP by default:
//        CachedBuffers.partial(AllPartialModels.SHAFT_HALF, state)
//                .center() // Centers the buffer relative to (0,0,0)
//
//                .light(light)
//                .renderInto(ms, buffer.getBuffer(RenderType.solid()));
//        ms.popPose();

        // --- RENDER BLADE ---

        if (be.rudderBlade != null) {
            ms.pushPose();
            be.animateRenderAngle(); // Note: Ideally lerp this with partialTicks for smoothness
            ms.mulPose(com.mojang.math.Axis.YP.rotation(be.renderAngle));

            ms.translate(-0.5f, -0.5f, -0.5f);
            CachedBuffers.partial(RudderVisual.getBladePartial(be), state)
                    .light(light)
                    .overlay(overlay)
                    .renderInto(ms, buffer.getBuffer(RenderType.cutout()));
            ms.popPose();
        }

        ms.popPose(); // Final pop
    }
}