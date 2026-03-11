package com.lightning323.createkinetic.sprite;

import com.simibubi.create.content.processing.burner.ScrollInstance;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

import dev.engine_room.flywheel.api.instance.InstanceHandle;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.lib.instance.ColoredLitOverlayInstance;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.core.Vec3i;

public class KineticScrollInstance extends ScrollInstance {

	public KineticScrollInstance(InstanceType<? extends ColoredLitOverlayInstance> type, InstanceHandle handle) {
		super(type, handle);
	}

	public KineticScrollInstance setSpriteShift2(KineticSpriteShiftEntry spriteShift) {
		return setSpriteShift2(spriteShift, 0.5f, 0.5f);
	}
	public KineticScrollInstance setSpriteShift2(KineticSpriteShiftEntry spriteShift, float factorU, float factorV) {
		float spriteWidth = spriteShift.getTarget()
				.getU1()
				- spriteShift.getTarget()
				.getU0();

		float spriteHeight = spriteShift.getTarget()
				.getV1()
				- spriteShift.getTarget()
				.getV0();

		scaleU = spriteWidth * factorU;
		scaleV = spriteHeight * factorV;

		diffU = spriteShift.getTarget().getU0() - spriteShift.getOriginal().getU0();
		diffV = spriteShift.getTarget().getV0() - spriteShift.getOriginal().getV0();

		return this;
	}

}