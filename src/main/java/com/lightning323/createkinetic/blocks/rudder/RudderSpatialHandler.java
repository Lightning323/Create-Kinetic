package com.lightning323.createkinetic.blocks.rudder;

import com.lightning323.createkinetic.KineticConfig;
import com.lightning323.createkinetic.items.RudderBladeItem;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.primitives.AABBi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

public class RudderSpatialHandler {
    public enum ObstructionCheckLogic {
        APPROXIMATE, OFF
    }

    private static final AABBi HARD_OBSTRUCTION_REGION = new AABBi(-1, -1, 0, 1, 1, 0);

    private static final List<BlockPos> SCAN_POSITIONS;

    static {
        SCAN_POSITIONS = StreamSupport.stream(
                        BlockPos.betweenClosed(HARD_OBSTRUCTION_REGION.minX, HARD_OBSTRUCTION_REGION.minY, HARD_OBSTRUCTION_REGION.minZ, HARD_OBSTRUCTION_REGION.maxX, HARD_OBSTRUCTION_REGION.maxY, HARD_OBSTRUCTION_REGION.maxZ).spliterator(), false)
                .map(BlockPos::immutable)
                .toList();
    }

    private final Set<BlockPos> obstructedBlocks = new HashSet<>();
    final SmartBlockEntity blockEntity;

    public RudderSpatialHandler(SmartBlockEntity be) {
        this.blockEntity = be;
    }

    public Set<BlockPos> getObstructionsFor(RudderBladeItem blade) {
        Set<BlockPos> set = new HashSet<>();
        if (!(blockEntity instanceof RudderBlockEntity pbe)) return set;

        Direction facing = pbe.getBlockState().getValue(RudderBlock.FACING);
        for (BlockPos relativePos : SCAN_POSITIONS) {
            BlockPos worldCheckPos = blockEntity.getBlockPos().offset(rotate(relativePos, facing));
            if (isPositionObstructed(worldCheckPos)) {
                set.add(worldCheckPos);
            }
        }
        return set;
    }

    private boolean isPositionObstructed(BlockPos worldCheckPos) {
        if (worldCheckPos.equals(blockEntity.getBlockPos())) {
            return false;
        }
        ObstructionCheckLogic logic = KineticConfig.rudderObstructionLogic;
        if (logic == ObstructionCheckLogic.OFF) {
            return false;
        }

        BlockState state = blockEntity.getLevel().getBlockState(worldCheckPos);
        if (state.isAir()) {
            return false;
        }
        if (state.getBlock() instanceof RudderBlock) {
            return true;
        }
        if (logic == ObstructionCheckLogic.APPROXIMATE) {
            return true;
        }

//        //Precise check
//        if (blade == null) { return false; }
//
//        VoxelShape shape = state.getCollisionShape(getWorld(), worldCheckPos);
//        if (shape.isEmpty()) { return false; }
//
//        AABB worldBladeAABB = getPreciseBladeAABB(pbe.getBlockPos(), pbe.getBlockState().getValue(RudderBlock.FACING), blade);
//        AABB localBladeAABB = worldBladeAABB.move(-worldCheckPos.getX(), -worldCheckPos.getY(), -worldCheckPos.getZ());
//        if (!shape.bounds().intersects(localBladeAABB)) { return false; } //Broad
//
//        //Narrow
//        for (AABB shapeAABB : shape.toAabbs()) {
//            if (shapeAABB.intersects(localBladeAABB)) {
//                return true;
//            }
//        }
//
        return false;
    }

    private void applyObstructionConsequences(RudderBlockEntity pbe) {
        if (!obstructedBlocks.isEmpty() && pbe.rudderBlade != null) {
            dropBlade(pbe);
        }
    }

    private void dropBlade(RudderBlockEntity pbe) {
        Level level = pbe.getLevel();
        BlockPos pos = pbe.getBlockPos();

        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(pbe.rudderBlade));
        pbe.rudderBlade = null;

        pbe.setChanged();
        pbe.sendData();

        level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_IRON, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    public void triggerImmediateScan() {
        if (!(blockEntity instanceof RudderBlockEntity pbe)) return;

        obstructedBlocks.clear();

        if (pbe.rudderBlade != null) {
            Direction facing = pbe.getBlockState().getValue(RudderBlock.FACING);
            for (BlockPos relativePos : SCAN_POSITIONS) {
                BlockPos worldCheckPos = blockEntity.getBlockPos().offset(rotate(relativePos, facing));
                if (isPositionObstructed(worldCheckPos)) {
                    obstructedBlocks.add(worldCheckPos);
                }
            }
        }

        applyObstructionConsequences(pbe);
    }

    public static BlockPos rotate(BlockPos pos, Direction targetDirection) {
        return switch (targetDirection) {
            case NORTH -> new BlockPos(pos.getX(), pos.getY(), pos.getZ());
            case SOUTH -> new BlockPos(-pos.getX(), pos.getY(), -pos.getZ());
            case WEST -> new BlockPos(pos.getZ(), pos.getY(), -pos.getX());
            case EAST -> new BlockPos(-pos.getZ(), pos.getY(), pos.getX());
            case DOWN -> new BlockPos(pos.getX(), -pos.getZ(), pos.getY());
            case UP -> new BlockPos(pos.getX(), pos.getZ(), -pos.getY());
        };
    }
//
//    public static Vector3f rotate(Vector3f vec, Direction targetDirection) {
//    return switch (targetDirection) {
//        case NORTH -> new Vector3f(-vec.x, -vec.y, -vec.z);
//        case SOUTH -> new Vector3f(vec.x, -vec.y, vec.z);
//        case WEST -> new Vector3f(-vec.z, vec.y, -vec.x);
//        case EAST -> new Vector3f(vec.z, -vec.y, -vec.x);
//        case DOWN -> new Vector3f(vec.x, -vec.z, vec.y);
//        case UP -> new Vector3f(vec.x, vec.z, -vec.y);
//    };

}
