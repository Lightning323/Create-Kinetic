package com.lightning323.createkinetic.blocks.sail;

import com.lightning323.createkinetic.registries.KineticItems;
import com.lightning323.createkinetic.registries.KineticPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.pulley.AbstractPulleyRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;

public class SailRenderer extends AbstractPulleyRenderer<SailBlockEntity> {

	public SailRenderer(BlockEntityRendererProvider.Context context) {
		super(context, KineticPartialModels.ROPE_HALF, KineticPartialModels.ROPE_HALF_MAGNET);
	}

	@Override
	protected Axis getShaftAxis(SailBlockEntity be) {
		return be.getBlockState()
			.getValue(SailBlock.HORIZONTAL_AXIS);
	}

	@Override
	protected PartialModel getCoil() {
		return AllPartialModels.ROPE_COIL;
	}

	@Override
	protected SuperByteBuffer renderRope(SailBlockEntity be) {
		return CachedBuffers.block(KineticItems.ROPE.getDefaultState());
	}

	@Override
	protected SuperByteBuffer renderMagnet(SailBlockEntity be) {
		return CachedBuffers.block(KineticItems.PULLEY_MAGNET.getDefaultState());
	}

	@Override
	protected float getOffset(SailBlockEntity be, float partialTicks) {
		return getBlockEntityOffset(partialTicks, be);
	}

	@Override
	protected boolean isRunning(SailBlockEntity be) {
		return isPulleyRunning(be);
	}

	public static boolean isPulleyRunning(SailBlockEntity be) {
		return be.running || be.mirrorParent != null || be.isVirtual();
	}

	@Override
	protected SpriteShiftEntry getCoilShift() {
		return AllSpriteShifts.ROPE_PULLEY_COIL;
	}

	public static float getBlockEntityOffset(float partialTicks, SailBlockEntity blockEntity) {
		float offset = blockEntity.getInterpolatedOffset(partialTicks);

		AbstractContraptionEntity attachedContraption = blockEntity.getAttachedContraption();
		if (attachedContraption != null) {
			SailContraption c = (SailContraption) attachedContraption.getContraption();
			double entityPos = Mth.lerp(partialTicks, attachedContraption.yOld, attachedContraption.getY());
			offset = (float) -(entityPos - c.anchor.getY() - c.getInitialOffset());
		}

		return offset;
	}

}
