package com.lightning323.createkinetic.blocks.shipHelm.renderer;

import com.lightning323.createkinetic.blocks.shipHelm.ShipHelmBlock;
import com.lightning323.createkinetic.blocks.shipHelm.ShipHelmBlockEntity;
import com.lightning323.createkinetic.registries.KineticPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
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
    public static final int SIXTEENTH = 1 / 16;
    private final BlockEntityRendererProvider.Context ctx;

    public ShipHelmRenderer(BlockEntityRendererProvider.Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void render(
            ShipHelmBlockEntity blockEntity,
            float partialTicks,
            PoseStack matrixStack,
            MultiBufferSource buffer,
            int combinedLight,
            int combinedOverlay
    ) {
        if (VisualizationManager.supportsVisualization(blockEntity.getLevel()))
            return;

        matrixStack.pushPose();

        // Wheel offset of the base
        matrixStack.translate(0.5, 1.125, 0.5);

        // Rotate wheel towards the direction it's facing
        float yRot = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot();
        matrixStack.mulPose(
                new Quaternionf(
                        new AxisAngle4f(
                                (float) (-yRot * Math.PI / 180.0),
                                0.0f, 1.0f, 0.0f
                        )
                )
        );

        blockEntity.animateHelmRotation();
        matrixStack.mulPose(new Quaternionf(new AxisAngle4f(blockEntity.renderHelmRotation, 0.0f, 0.0f, 1.0f)));

        // Render the wheel
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

        matrixStack.pushPose();

        // Model isn't centered: calculated and need to use 0.625 on y and z 0.25
        matrixStack.translate(-0.5, -0.625, -0.25);

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

        matrixStack.popPose();
    }
}