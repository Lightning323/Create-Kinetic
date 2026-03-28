package com.lightning323.createkinetic.blocks.rudder;

import com.lightning323.createkinetic.registries.KineticPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;


public class RudderRenderer extends KineticBlockEntityRenderer<RudderBlockEntity> {
    public RudderRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(RudderBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
//
//        // 1. Render the standard shaft (if your block has one)
//        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
//
//        // 2. Skip manual rendering if Flywheel (Instancing) is handling it
//        if (VisualizationManager.supportsVisualization(be.getLevel()))
//            return;
//
//        // 3. Get the orientation of the block
//        Direction facing = be.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
//
//        // 4. Calculate the flap angle
//        // If you want it to move based on the shaft speed:
//        float angle = be.getIndependentAngle(partialTicks);
//
//        // 5. Render the Flap
//        // .partial() takes your PartialModel (e.g., MyPartialModels.RUDDER_FLAP)
//        kineticRotationTransform(CachedBufferer.partial(MyPartialModels.RUDDER_FLAP, be.getBlockState()),
//                be,
//                facing.getAxis(),
//                angle,
//                light)
//                .renderInto(ms, buffer.getBuffer(RenderType.cutout()));
    }
}
