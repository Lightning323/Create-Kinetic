package org.lightning323.createkinetic.compat.simulated;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface DockingConnectorBEAccess {
   DockEnergyStorage getEnergyStorage();

   BlockPos getOtherConnectorPosition();

   Level getLevel();

   BlockPos getBlockPos();
}
