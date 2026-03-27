package com.lightning323.createkinetic.blocks.helm.renderer

import com.lightning323.createkinetic.blocks.helm.ShipHelmBlock
import com.lightning323.createkinetic.registries.KineticPartialModels
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.entity.BlockEntity

// OK so what dis does im making mc happy about states
// WheelModels has many states (wood type)
// WheelModel has 1 woodtype and represents 1 state
// In the mixin it gets queued and abused
object WheelModels {
    private val mc get() = Minecraft.getInstance()
    private val random = RandomSource.create()

    fun render(
        matrixStack: PoseStack,
        blockEntity: BlockEntity,
        buffer: MultiBufferSource,
        combinedLight: Int,
        combinedOverlay: Int
    ) {
        val level = blockEntity.level ?: return
        val blockState = blockEntity.blockState
        val woodType = (blockState.block as ShipHelmBlock).getWoodTypeEnum()

        matrixStack.pushPose()
        // Model isn't centered calculated and need to use 0.625 on y and z 0.25
        matrixStack.translate(-0.5, -0.625, -0.25)
        val blockPos = blockEntity.blockPos
        var bakedModel = KineticPartialModels.getHelmWheel(woodType).get();
//        val bakedModel = KineticPartialModels.HELM_WHEEL.get()

        mc.blockRenderer.modelRenderer.tesselateWithoutAO(
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
        )

        matrixStack.popPose()
    }
}
