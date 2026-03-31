package com.lightning323.createkinetic.blocks.shipHelm.renderer;

import com.lightning323.createkinetic.blocks.shipHelm.ShipHelmBlock;
import com.lightning323.createkinetic.registries.KineticPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

public class WheelModels {

    // Equivalent to the Kotlin 'object' properties
    private static final Minecraft mc = Minecraft.getInstance();
    private static final RandomSource random = RandomSource.create();

    public static void render(
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