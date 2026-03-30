package com.lightning323.createkinetic.blocks.helm.renderer;

import com.lightning323.createkinetic.blocks.helm.ShipHelmBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class ShipHelmRenderer implements BlockEntityRenderer<ShipHelmBlockEntity> {

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
        matrixStack.translate(0.5, 0.60, 0.5);

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

        Level level = blockEntity.getLevel();
        if (level != null) {
            Ship ship = VSGameUtilsKt.getShipManagingPos(level, blockEntity.getBlockPos());
            if (ship != null) {
                // Update the smoothed rotation based on ship angular velocity
                blockEntity.setSmoothedHelmRotation(lerp(
                    blockEntity.getSmoothedHelmRotation(),
                    ship.getAngularVelocity().y() * 100.0,
                    0.1
                ));
            }
        }

        // Add offset of the base based on rotation
        matrixStack.translate(0.0, 0.0, 0.19);

        // Rotate the wheel based on the ship omega
        float rotationRad = (float) (blockEntity.getSmoothedHelmRotation() / 20.0f * Math.PI);
        matrixStack.mulPose(new Quaternionf(new AxisAngle4f(rotationRad, 0.0f, 0.0f, 1.0f)));

        // Render the wheel
        WheelModels.render(matrixStack, blockEntity, buffer, combinedLight, combinedOverlay);

        matrixStack.popPose();
    }

    public double lerp(double start, double end, double factor) {
        return start + factor * (end - start);
    }
}