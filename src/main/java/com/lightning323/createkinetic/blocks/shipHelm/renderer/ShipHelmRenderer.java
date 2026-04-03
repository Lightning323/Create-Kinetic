package com.lightning323.createkinetic.blocks.shipHelm.renderer;

import com.lightning323.createkinetic.blocks.shipHelm.ShipHelmBlock;
import com.lightning323.createkinetic.blocks.shipHelm.ShipHelmBlockEntity;
import com.lightning323.createkinetic.registries.KineticPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

public class ShipHelmRenderer implements BlockEntityRenderer<ShipHelmBlockEntity> {
     private final BlockEntityRendererProvider.Context ctx;

    public ShipHelmRenderer(BlockEntityRendererProvider.Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void render(ShipHelmBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (VisualizationManager.supportsVisualization(blockEntity.getLevel()))
            return;

        matrixStack.pushPose();

        // 1. Move to the CENTER of the block (the pivot point)
        float heightAdjustment = 0.625f;
        matrixStack.translate(0.5, heightAdjustment, 0.5);

        // 2. Rotate the coordinate space for facing
        float yRot = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot();
        matrixStack.mulPose(Axis.YP.rotationDegrees(-yRot));

        // 3. Apply the steering rotation (Z-axis spin)
        blockEntity.animateHelmRotation();
        matrixStack.mulPose(Axis.ZP.rotation(blockEntity.renderHelmRotation));

        // 4. Translate BACK to align the model's internal coordinates
        // If you shifted your model in Blockbench as discussed earlier,
        // these numbers should match that shift relative to the center.
        matrixStack.translate(-0.5, -heightAdjustment, -0.5);

        // 5. Render
        renderWheel(matrixStack, blockEntity, buffer, combinedLight, combinedOverlay);

        matrixStack.popPose();
    }

    // Equivalent to the Kotlin 'object' properties
    private static final Minecraft mc = Minecraft.getInstance();
    private static final RandomSource random = RandomSource.create();

    public static void renderWheel(
            PoseStack matrixStack,
            BlockEntity blockEntity,
            MultiBufferSource buffer,
            int combinedLight,
            int combinedOverlay
    ) {
        Level level = blockEntity.getLevel();
        if (level == null) return;

        BlockState blockState = blockEntity.getBlockState();

        // Ensure the block is actually a ShipHelmBlock before casting
        if (!(blockState.getBlock() instanceof ShipHelmBlock helmBlock)) return;

        var woodType = helmBlock.getWoodTypeEnum();

        BlockPos blockPos = blockEntity.getBlockPos();
        BakedModel bakedModel = KineticPartialModels.getHelmWheel(woodType).get();

        mc.getBlockRenderer().getModelRenderer().tesselateWithoutAO(
                level,
                bakedModel,
                blockState,
                blockPos,
                matrixStack,
                buffer.getBuffer(RenderType.cutout()),
                true,
                random,
                blockState.getSeed(blockPos),
                combinedOverlay
        );

    }
}