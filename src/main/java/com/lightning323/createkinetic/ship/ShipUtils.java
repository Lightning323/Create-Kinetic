package com.lightning323.createkinetic.ship;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.function.Consumer;

public class ShipUtils {
    //North = -Z (Direction.AxisDirection.NEGATIVE, Direction.Axis.Z)
    //South = +Z (Direction.AxisDirection.POSITIVE, Direction.Axis.Z)
    //East = +X  (Direction.AxisDirection.POSITIVE, Direction.Axis.X)
    //West = -X  (Direction.AxisDirection.NEGATIVE, Direction.Axis.X)

    public static void addSail(Level level, BlockPos pos, Direction.Axis sailAxis) {
        if (level.isClientSide) return;
        KineticShipControl shipController = ShipUtils.getOrAddShipController((ServerLevel) level, pos);
        if (shipController != null) {
            if (sailAxis == Direction.Axis.X) {
                shipController.sailsX.add(pos.asLong());
            } else if (sailAxis == Direction.Axis.Z) {
                shipController.sailsZ.add(pos.asLong());
            }
            shipController.updateSailCount((ServerLevel) level);
        }
    }

    public static void removeSail(Level level, BlockPos pos, Direction.Axis sailAxis) {
        if (level.isClientSide) return;
        KineticShipControl shipController = ShipUtils.getOrAddShipController((ServerLevel) level, pos);
        if (shipController != null) {
            if (sailAxis == Direction.Axis.X) {
                shipController.sailsX.remove(pos.asLong());
            } else if (sailAxis == Direction.Axis.Z) {
                shipController.sailsZ.remove(pos.asLong());
            }
            shipController.updateSailCount((ServerLevel) level);
        }
    }

    /**
     *
     * @param world the Server Level
     * @param pos   the block position
     * @return the ship controller, or null if the block is not in a ship
     */
    public static KineticShipControl getShipController(ServerLevel world, BlockPos pos) {
        if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
            LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos(world, pos);
            if (ship != null) {
                KineticShipControl controller = ship.getAttachment(KineticShipControl.class);
                if (controller != null && controller.world == null) controller.world = world;
                return controller;
            }
        }
        return null;
    }

    /**
     * Gets or creates a ship controller for the block (Creates the ship controler on a vs ship if needed)
     *
     * @param world the Server Level
     * @param pos   the block position
     * @return the ship controller, or null if the block is not in a ship
     */
    public static KineticShipControl getOrAddShipController(ServerLevel world, BlockPos pos) {
        if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
            ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                KineticShipControl controller = KineticShipControl.getOrCreate((LoadedServerShip) ship, (ServerLevel) world);
                return controller;
            } else {
                ship = VSGameUtilsKt.getShipManagingPos(world, pos);
                if (ship instanceof LoadedServerShip) {
                    KineticShipControl controller = KineticShipControl.getOrCreate((LoadedServerShip) ship, (ServerLevel) world);
                    return controller;
                }
            }
        }
        return null;
    }
}
