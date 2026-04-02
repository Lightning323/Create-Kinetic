package com.lightning323.createkinetic.client;

import com.lightning323.createkinetic.blocks.shipHelm.ShipHelmBlockEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class KineticClientData {
    private static BlockPos controllingBePos;
    private static ShipHelmBlockEntity controllingHelm;

    public static void setControllingBlockEntity(ClientLevel level, BlockPos entityPos2) {
        controllingBePos = entityPos2;
        BlockEntity be = level.getBlockEntity(entityPos2);
        if (be instanceof ShipHelmBlockEntity) {
            controllingHelm = (ShipHelmBlockEntity) be;
        } else {
            controllingHelm = null;
        }
    }

    public static ShipHelmBlockEntity getControllingBlockEntity() {
        return controllingHelm;
    }

    public static BlockPos getControllingBlockPos() {
        return controllingBePos;
    }
}
