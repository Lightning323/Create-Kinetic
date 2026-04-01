package com.lightning323.createkinetic.blocks.rudder;

import com.lightning323.createkinetic.blocks.shipHelm.ShipHelmBlockEntity;
import com.lightning323.createkinetic.items.RudderBladeItem;
import com.lightning323.createkinetic.ship.KineticShipControl;
import com.lightning323.createkinetic.ship.ShipUtils;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class RudderBlockEntity extends KineticBlockEntity {

    //For visual purposes only
    public final Quaternionf rudderRotation = new Quaternionf();
    public final Quaternionf rudderIdentityRotation = new Quaternionf();
    float renderAngle;
    float lastForce;

    public void animateRenderAngle() {
        renderAngle = Mth.lerp(0.05f, renderAngle,
                getForce() * (-Mth.HALF_PI / 2));//Delta,start,end
    }

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

    RudderBladeItem rudderBlade = null; // Use ItemStack instead of Item for safety

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket); // Always call super first or last consistently

        if (rudderBlade != null) {
            compound.putInt("blade", RudderBladeItem.BladeType.toInt(rudderBlade));
        }
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        updateRenderOrientation();

        // Read the ItemStack back from the tag
        if (compound.contains("blade", Tag.TAG_INT)) {
            this.rudderBlade = RudderBladeItem.BladeType.toItem(compound.getInt("blade"));
        } else {
            this.rudderBlade = null;
        }
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
                case NORTH -> {
                    xRot = 90;
                    yRot = -180;
                }
                case SOUTH -> {
                    xRot = 90;
                    yRot = 0;
                }
                case EAST -> {
                    xRot = 90;
                    yRot = 90;
                }
                case WEST -> {
                    xRot = 90;
                    yRot = 270;
                }
                case UP -> {
                    xRot = 0;
                    yRot = 0;
                }
                case DOWN -> {
                    xRot = 180;
                    yRot = 0;
                }
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

    @Override
    public void tick() {
        super.tick();

        //If the force has changed, mark the rudder forces as dirty
        if (level instanceof ServerLevel serverLevel &&
                getForce() != lastForce
//                && level.getGameTime() % 5 == 0
        ) {
            lastForce = getForce();
            KineticShipControl controller = KineticShipControl.getOrAddController(serverLevel, getBlockPos());
            if (controller != null) controller.mustUpdateRudders = true;
        }
    }


    /**
     *
     * @return the force between -1 and 1
     */
    public float getForce() {
        return getSpeed() / ShipHelmBlockEntity.SPEED;
    }
}