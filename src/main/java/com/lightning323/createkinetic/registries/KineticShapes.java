package com.lightning323.createkinetic.registries;

import com.simibubi.create.AllShapes;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.core.Direction.UP;

public class KineticShapes {

    // Independent Shapers
    public static final VoxelShaper
            //Z=up/down
            //X is front/back
            SAIL_CLOTH = shape(7, 0, 0, 9, 16, 16).forAxis(),
            SAIL_MAGNET = shape(3, 0, 8, 13, 16, 16)//bar
                    .add(shape(7, 0, 2, 9, 16, 16).build())//cloth
                    .forAxis();

    private static AllShapes.Builder shape(VoxelShape shape) {
        return new AllShapes.Builder(shape);
    }


    private static AllShapes.Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }
}
