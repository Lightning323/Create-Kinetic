package com.lightning323.createkinetic.registries;

import com.simibubi.create.AllShapes;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.core.Direction.UP;

public class KineticShapes {

    // Independent Shapers
    public static final VoxelShaper
            //Z=up/down
            //X is front/back
            SAIL_CLOTH = shape(7, 0, 0, 9, 16, 16).forAxis(),
            SAIL_MAGNET = shape(3, 0, 10, 13, 16, 16)//bar
                    .add(shape(7, 0, 0, 9, 16, 16).build())//cloth
                    .forAxis(),
            RUDDER_BASE = shape(3, 0, 0, 13, 16, 16).forAxis();

    private static AllShapes.Builder shape(VoxelShape shape) {
        return new AllShapes.Builder(shape);
    }


    private static AllShapes.Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    public static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }

//    class DirectionalShape(VoxelShape shape) {
//        VoxelShape north = shape.build();
//        VoxelShape east = shape.rotate90().build();
//        VoxelShape south = shape.rotate180().build();
//        VoxelShape west = shape.rotate270().build();
//
//
//        public VoxelShape get(Direction direction) {
//            switch (direction) {
//                case Direction.NORTH:
//                    return north;
//                case Direction.EAST:
//                    return east;
//                case Direction.SOUTH:
//                    return south;
//                case Direction.WEST:
//                    return west;
//                default:
//                    throw new IllegalArgumentException();
//            }
//        }
//    }

}
