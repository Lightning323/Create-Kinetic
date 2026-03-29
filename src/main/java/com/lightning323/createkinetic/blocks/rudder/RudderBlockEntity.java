package com.lightning323.createkinetic.blocks.rudder;

import com.lightning323.createkinetic.ship.KineticShipControl;
import com.lightning323.createkinetic.ship.ShipUtils;
import com.lightning323.createkinetic.utils.VSUtils;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.valkyrienskies.physics_api.voxel.updates.VoxelShapeUpdateIterator;

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
            Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
            float xRot = 90;
            float yRot = 180;
            switch (facing) {
                case SOUTH -> yRot = 0;
                case WEST -> yRot = 270;
                case EAST -> yRot = 90;
                case UP -> xRot = 270 + 90; // 360 or 0
                case DOWN -> xRot = 90 + 90;  // 180
            }
            rudderIdentityRotation.identity();
            rudderIdentityRotation.rotationY((float) Math.toRadians(yRot));
            rudderIdentityRotation.rotateX((float) Math.toRadians(xRot));
        } else {
            KineticShipControl controller = ShipUtils.getOrAddShipController((ServerLevel) level, getBlockPos());
            if (controller != null) controller.addRudder(getBlockPos());
        }
    }

    public void remove() {
        super.remove();
        if (!level.isClientSide) {
            KineticShipControl controller = ShipUtils.getOrAddShipController((ServerLevel) level, getBlockPos());
            if (controller != null) controller.removeRudder(getBlockPos());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(!level.isClientSide){//TODO: Could update logic have something to do with where it happens that makes it not work?
        KineticShipControl controller = ShipUtils.getOrAddShipController((ServerLevel) level, getBlockPos());
        if (controller != null) controller.updateRudderForces();}
    }
    public float getForce() {
        return getSpeed();
    }
}