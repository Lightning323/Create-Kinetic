package org.lightning323.createkinetic.content.blocks.beam_lantern;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.lightning323.createkinetic.config.KineticConfig;

import java.util.ArrayList;
import java.util.List;

public class BeamLanternBlockEntity extends SmartBlockEntity {
    private Direction lastFacing = null;
    private final List<BlockPos> beamPositions = new ArrayList<>();

    public BeamLanternBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public void initialize() {
        super.initialize();
        if (level == null || level.isClientSide) {
            return;
        }
        // Runs once, on the BE's first tick. Only re-emits an already-lit beam (e.g. after a
        // chunk reload); it never writes block state, so it cannot re-enter itself.
        BlockState state = level.getBlockState(worldPosition);
        if (state.getValue(BeamLanternBlock.LIT)) {
            lastFacing = BeamLanternBlock.getFacing(state);
            placeBeam((ServerLevel) level, worldPosition, state);
        }
    }

    /**
     * Reconciles the stored {@code POWERED}/{@code LIT} state with the current redstone signal.
     * Called from {@code neighborChanged} and when the player toggles {@code DEFAULT_ON}. Reads the
     * authoritative state from the level (not the BE's cached state) so a state written during a
     * block update cannot be observed stale.
     */
    public void updatePowered() {
        if (level == null || level.isClientSide) {
            return;
        }
        BlockState state = level.getBlockState(worldPosition);
        boolean hasSignal = level.hasNeighborSignal(worldPosition);

        if (hasSignal != state.getValue(BeamLanternBlock.POWERED)) {
            state = state.setValue(BeamLanternBlock.POWERED, hasSignal);
            level.setBlockAndUpdate(worldPosition, state);
        }

        boolean shouldBeLit = BeamLanternBlock.isLitFor(state);
        if (state.getValue(BeamLanternBlock.LIT) == shouldBeLit) {
            return;
        }

        int chargeDelay = Math.max(0, KineticConfig.beamLanternChargeUpTicks());
        if (shouldBeLit && chargeDelay > 0) {
            // One-shot scheduled tick: only queued (and only once) while the lantern charges up.
            if (!level.getBlockTicks().hasScheduledTick(worldPosition, state.getBlock())) {
                level.scheduleTick(worldPosition, state.getBlock(), chargeDelay);
            }
        } else {
            setLit(shouldBeLit);
        }
    }

    /**
     * Handles delayed activation from scheduled ticks.
     */
    public void serverTick() {
        if (level == null || level.isClientSide) return;
        setLit(BeamLanternBlock.isLitFor(level.getBlockState(worldPosition)));
    }

    public void setLit(boolean lit) {
        if (level == null || level.isClientSide) return;
        BlockState state = level.getBlockState(worldPosition);
        if (state.getValue(BeamLanternBlock.LIT) == lit) return;

        BlockState newState = state.setValue(BeamLanternBlock.LIT, lit);
        level.setBlockAndUpdate(worldPosition, newState);

        if (lit) {
            lastFacing = BeamLanternBlock.getFacing(newState);
            placeBeam((ServerLevel) level, worldPosition, newState);
        } else {
            removeBeam((ServerLevel) level);
            lastFacing = null;
        }
    }

    /**
     * Called if the block rotates or direction changes.
     */
    public void handleDirectionChange(Direction newDir) {
        if (level == null || level.isClientSide) return;
        BlockState state = level.getBlockState(worldPosition);
        if (!state.getValue(BeamLanternBlock.LIT)) return;

        lastFacing = newDir;
        placeBeam((ServerLevel) level, worldPosition, state);
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
                newBeam.add(pos);
            } else {
                break;
            }
        }

        // 1. Compute diff: Find positions that were in the old beam but are NOT in the new beam
        List<BlockPos> toRemove = new ArrayList<>(beamPositions);
        toRemove.removeAll(newBeam);
        for (BlockPos p : toRemove) {
            if (level.isLoaded(p) && level.getBlockState(p).getBlock() == Blocks.LIGHT) {
                // Flag 2 = Update clients (so light disappears), but suppress neighbor updates
                level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
            }
        }

        // 2. Add/keep positions: Only set blocks that aren't already Blocks.LIGHT
        for (BlockPos p : newBeam) {
            if (level.isLoaded(p)) {
                if (level.getBlockState(p).getBlock() != Blocks.LIGHT) {
                    level.setBlock(p, Blocks.LIGHT.defaultBlockState(), 2);
                }
            }
        }

        beamPositions.clear();
        beamPositions.addAll(newBeam);
        setChanged();
    }

    public void removeBeam(ServerLevel level) {
        for (BlockPos p : beamPositions) {
            if (level.isLoaded(p) && level.getBlockState(p).getBlock() == Blocks.LIGHT) {
                // Flag 2: Clean up without triggering neighbor updates
                level.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
            }
        }
        beamPositions.clear();
        setChanged();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (lastFacing != null) {
            tag.putInt("LastFacing", lastFacing.get3DDataValue());
        }
        tag.putInt("BeamCount", beamPositions.size());
        for (int i = 0; i < beamPositions.size(); i++) {
            tag.put("BeamPos_" + i, NbtUtils.writeBlockPos(beamPositions.get(i)));
        }
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        if (tag.contains("LastFacing")) {
            lastFacing = Direction.from3DDataValue(tag.getInt("LastFacing"));
        }
        beamPositions.clear();
        if (tag.contains("BeamCount")) {
            int count = tag.getInt("BeamCount");
            for (int i = 0; i < count; i++) {
                String key = "BeamPos_" + i;
                if (tag.contains(key)) {
                    NbtUtils.readBlockPos(tag, key).ifPresent(beamPositions::add);
                }
            }
        }
    }
}