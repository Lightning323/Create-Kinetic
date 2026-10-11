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

    // Completely removed tick()! We handle everything via state changes and scheduled ticks.

    /**
     * Called by the Block when redstone power changes.
     */
    public void updatePowered() {
        if (level == null || level.isClientSide) {
            return;
        }
        BlockState state = getBlockState();
        boolean hasSignal = level.hasNeighborSignal(worldPosition);

        if (hasSignal != state.getValue(BeamLanternBlock.POWERED)) {
            level.setBlockAndUpdate(worldPosition, state.setValue(BeamLanternBlock.POWERED, hasSignal));

            int chargeDelay = Math.max(0, KineticConfig.beamLanternChargeUpTicks());
            if (chargeDelay > 0 && hasSignal) {
                // Schedule the lighting action after the charge delay
                level.scheduleTick(worldPosition, state.getBlock(), chargeDelay);
            } else {
                // Instant toggle if no charge-up config
                setLit(hasSignal);
            }
        }
    }

    /**
     * Handles delayed activation from scheduled ticks.
     */
    public void serverTick() {
        if (level == null || level.isClientSide) return;
        boolean powered = getBlockState().getValue(BeamLanternBlock.POWERED);
        setLit(powered);
    }

    public void setLit(boolean lit) {
        if (level == null || level.isClientSide) return;
        BlockState state = getBlockState();
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
        if (!getBlockState().getValue(BeamLanternBlock.LIT)) return;

        // Clean up old beam, update facing, place new beam
        removeBeam((ServerLevel) level);
        lastFacing = newDir;
        placeBeam((ServerLevel) level, worldPosition, getBlockState());
    }

    private void placeBeam(ServerLevel level, BlockPos origin, BlockState state) {
        Direction dir = BeamLanternBlock.getFacing(state);
        int maxLen = KineticConfig.beamLanternMaxPropagationLength();
        int len = 0;
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        m.set(origin);

        beamPositions.clear();
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
                beamPositions.add(pos);
            } else {
                break;
            }
        }
    }

    public void removeBeam(ServerLevel level) {
        for (BlockPos p : beamPositions) {
            if (level.isLoaded(p) && level.getBlockState(p).getBlock() == Blocks.LIGHT) {
                level.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
            }
        }
        beamPositions.clear();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        if (lastFacing != null) {
            tag.putInt("LastFacing", lastFacing.get3DDataValue());
        }
        // Save beam positions so we can clean them up properly if unloaded/reloaded
        tag.put("BeamCount", net.minecraft.nbt.IntTag.valueOf(beamPositions.size()));
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
                    // Pass both the tag and the key string, then safely add if present
                    NbtUtils.readBlockPos(tag, key).ifPresent(beamPositions::add);
                }
            }
        }
    }
}