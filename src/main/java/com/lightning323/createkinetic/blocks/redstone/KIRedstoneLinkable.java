package com.lightning323.createkinetic.blocks.redstone;

import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;

public interface KIRedstoneLinkable {

    public int getTransmittedStrength();

    public void setReceivedStrength(int power);

    public boolean isListening();

    public boolean isAlive();

    public Couple<KFrequency> getNetworkKey();

    public BlockPos getLocation();

}
