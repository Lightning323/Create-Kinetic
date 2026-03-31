package com.lightning323.createkinetic.blocks.shipHelm.renderer;

import com.lightning323.createkinetic.blocks.shipHelm.ShipHelmBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
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
        matrixStack.pushPose();

        // Wheel offset of the base
        matrixStack.translate(0.5, .8, 0.5);

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

        // Add offset of the base based on rotation
        matrixStack.translate(0.0, 0.0, 0.19);

        //We have to use blockenity speed because its available on the client side
        blockEntity.renderHelmRotation = (float) lerp(blockEntity.renderHelmRotation,
                (blockEntity.getSpeed() / ShipHelmBlockEntity.SPEED) * Mth.PI,
                0.01f);
        matrixStack.mulPose(new Quaternionf(new AxisAngle4f(blockEntity.renderHelmRotation, 0.0f, 0.0f, 1.0f)));

        // Render the wheel
        WheelModels.render(matrixStack, blockEntity, buffer, combinedLight, combinedOverlay);

        matrixStack.popPose();
    }

    public double lerp(double start, double end, double factor) {
        return start + factor * (end - start);
    }
}