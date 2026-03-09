package com.lightning323.createkinetic.blocks.sail;


import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.pulley.AbstractPulleyVisual;
import com.simibubi.create.content.processing.burner.ScrollInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import net.createmod.catnip.render.SpriteShiftEntry;

public class SailVisual extends AbstractPulleyVisual<SailBlockEntity> {
	public SailVisual(VisualizationContext context, SailBlockEntity blockEntity, float partialTick) {
		super(context, blockEntity, partialTick);
	}

	@Override
	protected Instancer<TransformedInstance> getRopeModel() {
		return instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.ROPE));
	}

	@Override
	protected Instancer<TransformedInstance> getMagnetModel() {
		return instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.PULLEY_MAGNET));
	}

	@Override
	protected Instancer<TransformedInstance> getHalfMagnetModel() {
		return instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.ROPE_HALF_MAGNET));
	}

	@Override
	protected Instancer<ScrollInstance> getCoilModel() {
		return instancerProvider().instancer(AllInstanceTypes.SCROLLING, Models.partial(AllPartialModels.ROPE_COIL));
	}

	@Override
	protected Instancer<TransformedInstance> getHalfRopeModel() {
		return instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(AllPartialModels.ROPE_HALF));
	}

	@Override
	protected float getOffset(float pt) {
		return SailRenderer.getBlockEntityOffset(pt, blockEntity);
	}

	@Override
	protected boolean isRunning() {
		return SailRenderer.isPulleyRunning(blockEntity);
	}

	@Override
	protected SpriteShiftEntry getCoilAnimation() {
		return AllSpriteShifts.ROPE_PULLEY_COIL;
	}

}
