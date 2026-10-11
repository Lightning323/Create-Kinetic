package org.lightning323.createkinetic.content.blocks.beam_lantern;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.lightning323.createkinetic.config.KineticConfig;

import java.util.ArrayList;
import java.util.List;

public class BeamLanternBlockEntity extends SmartBlockEntity {
    // Remember the direction the beam is currently propagating in
    private Direction lastFacing = null;
    private int targetLit = 0;
    private int chargeTicks = 0;
    private boolean lastPowered = false;
    private final List<BlockPos> beamPositions = new ArrayList<>();
    private boolean beamCleared = true;

    public BeamLanternBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) {
            return;
        }

        BlockState state = getBlockState();
        boolean powered = state.getValue(BeamLanternBlock.POWERED);
        int maxCharge = Math.max(0, KineticConfig.beamLanternChargeUpTicks());
        boolean shouldBeLit = powered;

        int shouldBeLitInt = shouldBeLit ? 1 : 0;
        if (shouldBeLitInt != targetLit) {
            targetLit = shouldBeLitInt;
            chargeTicks = 0;
        }

        int currentLit = state.getValue(BeamLanternBlock.LIT) ? 1 : 0;
        if (currentLit != targetLit) {
            if (chargeTicks < maxCharge) {
                chargeTicks++;
                return;
            }
            chargeTicks = 0;
            boolean lit = targetLit == 1;
            BlockState newState = state.setValue(BeamLanternBlock.LIT, lit);
            if (newState != state) {
                level.setBlockAndUpdate(worldPosition, newState);
                if (lit) {
                    placeBeam((ServerLevel) level, worldPosition, newState);
                } else {
                    removeBeam((ServerLevel) level, worldPosition, state);
                }
            }
            return;
        }

        if (state.getValue(BeamLanternBlock.LIT)) {
            Direction currentDir = BeamLanternBlock.getFacing(state);
            // If the direction changed since last tick, clean up the old beam first
            if (lastFacing != null && !lastFacing.equals(currentDir)) {
                removeBeam((ServerLevel) level, worldPosition, state);
            }
            placeBeam((ServerLevel) level, worldPosition, state);
            lastFacing = currentDir;
        } else {
            // Not lit – clear stored direction
            lastFacing = null;
        }
    }

    public void updatePowered() {
        if (level == null || level.isClientSide) {
            return;
        }
        BlockState state = getBlockState();
        boolean hasSignal = level.hasNeighborSignal(worldPosition);
        if (hasSignal != state.getValue(BeamLanternBlock.POWERED)) {
            level.setBlockAndUpdate(worldPosition, state.setValue(BeamLanternBlock.POWERED, hasSignal));
        }
    }

    private void placeBeam(ServerLevel level, BlockPos origin, BlockState state) {
        Direction dir = BeamLanternBlock.getFacing(state);
        int maxLen = KineticConfig.beamLanternMaxPropagationLength();
        int len = 0;
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        m.set(origin);
        List<BlockPos> newBeam = new ArrayList<>();
        while (len < maxLen) {
            m.move(dir);
            len++;
            BlockPos pos = m.immutable();
            if (!level.isLoaded(pos)) {
                break;
            }
            BlockState bs = level.getBlockState(pos);
            if (bs.isAir() || bs.getBlock() == Blocks.LIGHT) {
                level.setBlockAndUpdate(pos, Blocks.LIGHT.defaultBlockState());
                newBeam.add(pos);
                beamCleared = false;
            } else {
                break;
            }
        }
        beamPositions.clear();
        beamPositions.addAll(newBeam);
    }

    public void removeBeam(ServerLevel level, BlockPos origin, BlockState state) {
        for (BlockPos p : beamPositions) {
            if (level.isLoaded(p) && level.getBlockState(p).getBlock() == Blocks.LIGHT) {
                level.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
            }
        }
        beamPositions.clear();
        beamCleared = true;
    }

    static void removeBeamStatic(ServerLevel level, BlockPos origin, BlockState state) {
        if (level.getBlockEntity(origin) instanceof BeamLanternBlockEntity be) {
            be.removeBeam(level, origin, state);
            return;
        }
        Direction dir = BeamLanternBlock.getFacing(state);
        int maxLen = KineticConfig.beamLanternMaxPropagationLength();
        int len = 0;
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        m.set(origin);
        while (len < maxLen) {
            m.move(dir);
            len++;
            BlockPos pos = m.immutable();
            if (!level.isLoaded(pos)) {
                break;
            }
            if (level.getBlockState(pos).getBlock() == Blocks.LIGHT) {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            } else {
                break;
            }
        }
    }
}
