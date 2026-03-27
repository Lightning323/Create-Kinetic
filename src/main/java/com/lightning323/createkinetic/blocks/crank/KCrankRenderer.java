package com.lightning323.createkinetic.blocks.crank;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.crank.HandCrankBlockEntity;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class KCrankRenderer extends KineticBlockEntityRenderer<KCrankBlockEntity> {

	public KCrankRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(KCrankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
		int light, int overlay) {
		if (be.shouldRenderShaft())
			super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

		if (VisualizationManager.supportsVisualization(be.getLevel()))
			return;

		Direction facing = be.getBlockState()
			.getValue(FACING);
		kineticRotationTransform(be.getRenderedHandle(), be, facing.getAxis(), AngleHelper.rad(be.getIndependentAngle(partialTicks)), light)
			.renderInto(ms, buffer.getBuffer(RenderType.solid()));
	}

}
