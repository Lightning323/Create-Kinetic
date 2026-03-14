package com.lightning323.createkinetic.ship;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class ShipUtils {
    //North = -Z (Direction.AxisDirection.NEGATIVE, Direction.Axis.Z)
    //South = +Z (Direction.AxisDirection.POSITIVE, Direction.Axis.Z)
    //East = +X  (Direction.AxisDirection.POSITIVE, Direction.Axis.X)
    //West = -X  (Direction.AxisDirection.NEGATIVE, Direction.Axis.X)

    public static void addBlockSail(Level level, BlockPos pos, Direction axis) {
        KineticShipControl shipController = getOrCreateShipController(level, pos);
        if (shipController != null) {
            switch (axis) {
                case NORTH -> shipController.blockSailsZ++;
                case SOUTH -> shipController.blockSailsZ++;
                case EAST -> shipController.blockSailsX++;
                case WEST -> shipController.blockSailsX++;
            }
            shipController.updateSailCount();
        }
    }

    public static void removeBlockSail(Level level, BlockPos pos, Direction axis) {
        KineticShipControl shipController = getOrCreateShipController(level, pos);
        if (shipController != null) {
            switch (axis) {
                case NORTH -> shipController.blockSailsZ--;
                case SOUTH -> shipController.blockSailsZ--;
                case EAST -> shipController.blockSailsX--;
                case WEST -> shipController.blockSailsX--;
            }
            shipController.updateSailCount();
        }
    }

    public static KineticShipControl getOrCreateShipController(Level world, BlockPos pos) {
        if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
            ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                KineticShipControl controller = KineticShipControl.getOrCreate((LoadedServerShip) ship, world);
                return controller;
            } else { //ship is being loaded from template
                ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
                if (ship instanceof LoadedServerShip) {
                    KineticShipControl controller = KineticShipControl.getOrCreate((LoadedServerShip) ship, world);
                    return controller;
                }
            }
        }
        return null;
    }
}
