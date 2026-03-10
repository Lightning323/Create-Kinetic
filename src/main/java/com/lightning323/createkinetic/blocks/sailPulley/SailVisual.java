package com.lightning323.createkinetic.blocks.sailPulley;


import com.lightning323.createkinetic.registries.KineticPartialModels;
import com.lightning323.createkinetic.registries.KineticSpriteShifts;
import com.simibubi.create.content.processing.burner.ScrollInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import net.createmod.catnip.render.SpriteShiftEntry;

public class SailVisual extends AbstractSailPulleyVisual<SailBlockEntity> {
	public SailVisual(VisualizationContext context, SailBlockEntity blockEntity, float partialTick) {
		super(context, blockEntity, partialTick);
	}


	@Override
	protected Instancer<TransformedInstance> getRopeModel() {

		return instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(KineticPartialModels.ROPE));
	}

	@Override
	protected Instancer<TransformedInstance> getMagnetModel() {
		return instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(KineticPartialModels.PULLEY_MAGNET));
	}

	@Override
	protected Instancer<TransformedInstance> getHalfMagnetModel() {
		return instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(KineticPartialModels.ROPE_HALF_MAGNET));
	}

	@Override
	protected Instancer<ScrollInstance> getCoilModel() {
		return instancerProvider().instancer(AllInstanceTypes.SCROLLING, Models.partial(KineticPartialModels.SAIL_COIL));
	}

	@Override
	protected Instancer<TransformedInstance> getHalfRopeModel() {
		return instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(KineticPartialModels.ROPE_HALF));
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
		return KineticSpriteShifts.SAIL_COIL;
	}

}
