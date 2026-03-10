package com.lightning323.createkinetic.blocks.sailPulley;

import com.lightning323.createkinetic.registries.KineticContraptions;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class SailContraption extends TranslatingContraption {

	int initialOffset;

	@Override
	public ContraptionType getType() {
		return KineticContraptions.SAIL.value();
	}

	public SailContraption() {}

	public SailContraption(int initialOffset) {
		this.initialOffset = initialOffset;
	}

	@Override
	public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
		if (!searchMovedStructure(world, pos, null))
			return false;
		startMoving(world);
		return true;
	}

	@Override
	protected boolean isAnchoringBlockAt(BlockPos pos) {
		if (pos.getX() != anchor.getX() || pos.getZ() != anchor.getZ())
			return false;
		int y = pos.getY();
		if (y <= anchor.getY() || y > anchor.getY() + initialOffset + 1)
			return false;
		return true;
	}

	@Override
	public CompoundTag writeNBT(boolean spawnPacket) {
		CompoundTag tag = super.writeNBT(spawnPacket);
		tag.putInt("InitialOffset", initialOffset);
		return tag;
	}

	@Override
	public void readNBT(Level world, CompoundTag nbt, boolean spawnData) {
		initialOffset = nbt.getInt("InitialOffset");
		super.readNBT(world, nbt, spawnData);
	}

    public int getInitialOffset() {
		return initialOffset;
	}
}
