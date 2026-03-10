package com.lightning323.createkinetic.ship;

import com.lightning323.createkinetic.utils.VSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ShipUtils {
    public static void addSail(Level level, BlockPos pos){
        KineticShipControl shipController = VSUtils.getOrCreateShipController(level,pos);
        if (shipController != null) {
            shipController.blockSails++;
            shipController.updateSailCount();
        }
    }

    public static void removeSail(Level level, BlockPos pos){
        KineticShipControl shipController = VSUtils.getOrCreateShipController(level,pos);
        if (shipController != null) {
            shipController.blockSails--;
            shipController.updateSailCount();
        }
    }
}
