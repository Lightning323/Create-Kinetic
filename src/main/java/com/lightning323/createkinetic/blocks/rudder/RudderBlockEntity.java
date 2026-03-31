package com.lightning323.createkinetic.blocks.rudder;

import com.lightning323.createkinetic.blocks.shipHelm.ShipHelmBlockEntity;
import com.lightning323.createkinetic.ship.KineticShipControl;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;

public class RudderBlockEntity extends KineticBlockEntity {

    //For visual purposes only
    public final Quaternionf rudderRotation = new Quaternionf();
    public final Quaternionf rudderIdentityRotation = new Quaternionf();
    float renderAngle;

    public RudderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        //We setup the rotation of the rudder in quaternion form for the renderer and visual
        if (level.isClientSide) {
            updateRenderOrientation();
        } else {
            KineticShipControl controller = KineticShipControl.getOrAddController((ServerLevel) level, getBlockPos());
            if (controller != null) controller.addRudder(getBlockPos());
        }
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        updateRenderOrientation();
    }

    protected void updateRenderOrientation() {
        if (level.isClientSide) {
            Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
            int planeRot = getBlockState().getValue(RudderBlock.PLANE_ROTATION);

            // 1. Reset to identity
            rudderIdentityRotation.identity();

            // 2. WORLD ORIENTATION (Apply these LAST in code, but they define the "Slot")
            float xRot = 0;
            float yRot = 0;

            switch (facing) {
                case NORTH -> { xRot = 90;  yRot = -180;   }
                case SOUTH -> { xRot = 90;  yRot = 0; }
                case EAST  -> { xRot = 90;  yRot = 90;  }
                case WEST  -> { xRot = 90;  yRot = 270; }
                case UP    -> { xRot = 0;   yRot = 0;   }
                case DOWN  -> { xRot = 180; yRot = 0;   }
            }

            // 3. THE MAGIC ORDER
            // We apply the world position first
            rudderIdentityRotation.rotateY((float) Math.toRadians(yRot));
            rudderIdentityRotation.rotateX((float) Math.toRadians(xRot));

            // 4. THE PLANE ROTATION (The "Roll")
            // By rotating AFTER the world orientation, we rotate around the NEW local axis.
            // If the block is on a wall, this "Y" rotation is now pointing out of the wall.
            float planeAngle = (float) Math.toRadians(planeRot * 90);
            rudderIdentityRotation.rotateY(planeAngle);
        }
    }

    public void remove() {
        super.remove();
        if (!level.isClientSide) {
            KineticShipControl controller = KineticShipControl.getOrAddController((ServerLevel) level, getBlockPos());
            if (controller != null) controller.removeRudder(getBlockPos());
        }
    }

//    @Override
//    public void tick() {
//        super.tick();
//        if (!level.isClientSide) {//TODO: Could update logic have something to do with where it happens that makes it not work?
//            KineticShipControl controller = ShipUtils.getOrAddShipController((ServerLevel) level, getBlockPos());
//            if (controller != null) controller.updateRudderForces();
//        }
//    }

    /**
     *
     * @return the force between -1 and 1
     */
    public float getForce() {
        return getSpeed() / ShipHelmBlockEntity.SPEED;
    }
}