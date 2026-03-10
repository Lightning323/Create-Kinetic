package com.lightning323.createkinetic.blocks.sailPulley;

import com.lightning323.createkinetic.Createkinetic;
import com.lightning323.createkinetic.registries.KineticItems;
import com.lightning323.createkinetic.ship.KineticShipControl;
import com.lightning323.createkinetic.utils.VSUtils;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.piston.LinearActuatorBlockEntity;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SailBlockEntity extends LinearActuatorBlockEntity implements ThresholdSwitchObservable {


    @Override
    public void onLoad() {
        super.onLoad();
        if (level.isClientSide) return;
        KineticShipControl shipController = VSUtils.getOrCreateShipController(level, getBlockPos());
        if (shipController != null) {
            shipController.sailPulleys.add(getBlockPos().asLong());
        }
    }

    @Override
    public void remove() {
        super.remove();
        if (level.isClientSide) return;
        KineticShipControl shipController = VSUtils.getOrCreateShipController(level, getBlockPos());
        if (shipController != null) {
            shipController.sailPulleys.remove(getBlockPos().asLong());
            totalSails = 0;
            shipController.updateSailCount();
        }
    }

    protected int initialOffset;
    private float prevAnimatedOffset;

    protected BlockPos mirrorParent;
    protected List<BlockPos> mirrorChildren;
    public WeakReference<AbstractContraptionEntity> sharedMirrorContraption;

    public SailBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        double expandY = -offset;
        if (sharedMirrorContraption != null) {
            AbstractContraptionEntity ace = sharedMirrorContraption.get();
            if (ace != null)
                expandY = ace.getY() - worldPosition.getY();
        }
        return super.createRenderBoundingBox().expandTowards(0, expandY, 0);
    }
//
//    @Override
//    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
//        super.addBehaviours(behaviours);
//        registerAwardables(behaviours, AllAdvancements.PULLEY_MAXED);
//    }

    @Override
    public void tick() {
        float prevOffset = offset;
        super.tick();

        if (level.isClientSide() && mirrorParent != null)
            if (sharedMirrorContraption == null || sharedMirrorContraption.get() == null
                    || !sharedMirrorContraption.get()
                    .isAlive()) {
                sharedMirrorContraption = null;
                if (level.getBlockEntity(mirrorParent) instanceof SailBlockEntity pte && pte.movedContraption != null)
                    sharedMirrorContraption = new WeakReference<>(pte.movedContraption);
            }

        if (isVirtual())
            prevAnimatedOffset = offset;
        invalidateRenderBoundingBox();
    }

    @Override
    protected boolean isPassive() {
        return mirrorParent != null;
    }

    @Nullable
    public AbstractContraptionEntity getAttachedContraption() {
        return mirrorParent != null && sharedMirrorContraption != null ? sharedMirrorContraption.get()
                : movedContraption;
    }

    @Override
    protected void assemble() throws AssemblyException {
        if (!(level.getBlockState(worldPosition)
                .getBlock() instanceof SailPulleyBlock))
            return;
        if (speed == 0 && mirrorParent == null)
            return;
        int maxLength = AllConfigs.server().kinetics.maxRopeLength.get();
        int i = 1;
        while (i <= maxLength) {
            BlockPos ropePos = worldPosition.below(i);
            BlockState ropeState = level.getBlockState(ropePos);
            if (!KineticItems.PULLEY_SAIL_CLOTH.has(ropeState) && !KineticItems.PULLEY_SAIL_MAGNET.has(ropeState)) {
                break;
            }
            ++i;
        }
        offset = i - 1;
        if (offset >= getExtensionRange() && getSpeed() > 0)
            return;
        if (offset <= 0 && getSpeed() < 0)
            return;

        // Collect Construct
        if (!level.isClientSide && mirrorParent == null) {
            needsContraption = false;
            BlockPos anchor = worldPosition.below(Mth.floor(offset + 1));
            initialOffset = Mth.floor(offset);
            SailContraption contraption = new SailContraption(initialOffset);
            boolean canAssembleStructure = contraption.assemble(level, anchor);

            if (canAssembleStructure) {
                Direction movementDirection = getSpeed() > 0 ? Direction.DOWN : Direction.UP;
                if (ContraptionCollider.isCollidingWithWorld(level, contraption, anchor.relative(movementDirection),
                        movementDirection))
                    canAssembleStructure = false;
            }

            if (!canAssembleStructure && getSpeed() > 0)
                return;

            removeRopes();

            if (!contraption.getBlocks()
                    .isEmpty()) {
                contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
                movedContraption = ControlledContraptionEntity.create(level, this, contraption);
                movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
                level.addFreshEntity(movedContraption);
                forceMove = true;
                needsContraption = true;

                if (contraption.containsBlockBreakers())
                    award(AllAdvancements.CONTRAPTION_ACTORS);

                for (BlockPos pos : contraption.createColliders(level, Direction.UP)) {
                    if (pos.getY() != 0)
                        continue;
                    pos = pos.offset(anchor);
                    if (level.getBlockEntity(
                            new BlockPos(pos.getX(), worldPosition.getY(), pos.getZ())) instanceof SailBlockEntity pbe)
                        pbe.startMirroringOther(worldPosition);
                }
            }
        }

        if (mirrorParent != null)
            removeRopes();

        clientOffsetDiff = 0;
        running = true;
        sendData();
    }

    private void removeRopes() {
        for (int i = ((int) offset); i > 0; i--) {
            BlockPos offset = worldPosition.below(i);
            BlockState oldState = level.getBlockState(offset);
            level.setBlock(offset, oldState.getFluidState()
                    .createLegacyBlock(), 66);
        }
    }


    public int getTotalSails() {
        return totalSails;
    }


    @Override
    public void disassemble() {
        if (!running && movedContraption == null && mirrorParent == null)
            return;
        offset = getGridOffset(offset);
        if (movedContraption != null)
            resetContraptionToOffset();

        if (!level.isClientSide) {
            int actuallyPlacedSails = 0;

            if (shouldCreateRopes()) {
                if (offset > 0) {
                    BlockPos magnetPos = worldPosition.below((int) offset);
                    FluidState ifluidstate = level.getFluidState(magnetPos);
                    if (level.getBlockState(magnetPos)
                            .getDestroySpeed(level, magnetPos) != -1) {

                        level.destroyBlock(magnetPos, level.getBlockState(magnetPos)
                                .getCollisionShape(level, magnetPos)
                                .isEmpty());
                        boolean success = level.setBlock(magnetPos, KineticItems.PULLEY_SAIL_MAGNET.getDefaultState()
                                        .setValue(BlockStateProperties.WATERLOGGED, //Waterlogged property
                                                Boolean.valueOf(ifluidstate.getType() == Fluids.WATER))
                                        .setValue(BlockStateProperties.HORIZONTAL_AXIS, //Horizontal axis property
                                                this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_AXIS))
                                , 66);
                        if (success) actuallyPlacedSails++;
                    }
                }

                boolean[] waterlog = new boolean[(int) offset];


                for (boolean destroyPass : Iterate.trueAndFalse) {
                    for (int i = 1; i <= ((int) offset) - 1; i++) {
                        BlockPos ropePos = worldPosition.below(i);
                        if (level.getBlockState(ropePos)
                                .getDestroySpeed(level, ropePos) == -1)
                            continue;

                        if (destroyPass) {
                            FluidState ifluidstate = level.getFluidState(ropePos);
                            waterlog[i] = ifluidstate.getType() == Fluids.WATER;
                            level.destroyBlock(ropePos, level.getBlockState(ropePos)
                                    .getCollisionShape(level, ropePos)
                                    .isEmpty());
                            continue;
                        }

                        BlockPos sailPos = worldPosition.below(i);
                        boolean success = level.setBlock(sailPos, KineticItems.PULLEY_SAIL_CLOTH.getDefaultState()
                                        .setValue(BlockStateProperties.WATERLOGGED, waterlog[i]) //Waterlogged property
                                        .setValue(BlockStateProperties.HORIZONTAL_AXIS, //Horizontal axis property
                                                this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_AXIS))
                                , 66);
                        if (success) actuallyPlacedSails++;
                    }
                }
            }

            this.totalSails = (int) actuallyPlacedSails;
//            Createkinetic.LOGGER.info("Total sails: " + totalSails + " Offset: " + offset);
            KineticShipControl shipController = VSUtils.getOrCreateShipController(getLevel(), getBlockPos());
            if (shipController != null) {
                shipController.updateSailCount();
            }

            if (movedContraption != null && mirrorParent == null)
                movedContraption.disassemble();
            notifyMirrorsOfDisassembly();
        }

        if (movedContraption != null)
            movedContraption.discard();

        movedContraption = null;
        initialOffset = 0;
        running = false;
        sendData();
    }

    protected boolean shouldCreateRopes() {
        return !remove;
    }

    @Override
    protected Vec3 toPosition(float offset) {
        if (movedContraption.getContraption() instanceof SailContraption contraption) {
            return Vec3.atLowerCornerOf(contraption.anchor)
                    .add(0, contraption.getInitialOffset() - offset, 0);

        }
        return Vec3.ZERO;
    }

    @Override
    protected void visitNewPosition() {
        super.visitNewPosition();
        if (level.isClientSide)
            return;
        if (movedContraption != null)
            return;
        if (getSpeed() <= 0)
            return;

        BlockPos posBelow = worldPosition.below((int) (offset + getMovementSpeed()) + 1);
        BlockState state = level.getBlockState(posBelow);
        if (!BlockMovementChecks.isMovementNecessary(state, level, posBelow))
            return;
        if (BlockMovementChecks.isBrittle(state))
            return;

        disassemble();
        //We want to disable this, so when we hit a block in our sail, (and we dont have a contraption) We stop instead of keep going
//        assembleNextTick = true;
    }

    int totalSails = 0;

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        initialOffset = compound.getInt("InitialOffset");
        needsContraption = compound.getBoolean("NeedsContraption");
        super.read(compound, clientPacket);

        BlockPos prevMirrorParent = mirrorParent;
        mirrorParent = null;
        mirrorChildren = null;

        if (compound.contains("TotalSails")) {
            this.totalSails = compound.getInt("TotalSails");
        }

        if (compound.contains("MirrorParent")) {
            mirrorParent = NbtUtils.readBlockPos(compound.getCompound("MirrorParent"));
            offset = 0;
            if (prevMirrorParent == null || !prevMirrorParent.equals(mirrorParent))
                sharedMirrorContraption = null;
        }

        if (compound.contains("MirrorChildren"))
            mirrorChildren = NBTHelper.readCompoundList(compound.getList("MirrorChildren", Tag.TAG_COMPOUND),
                    NbtUtils::readBlockPos);

        if (mirrorParent == null)
            sharedMirrorContraption = null;
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("InitialOffset", initialOffset);
        super.write(compound, clientPacket);

        compound.putInt("TotalSails", totalSails);

        if (mirrorParent != null)
            compound.put("MirrorParent", NbtUtils.writeBlockPos(mirrorParent));
        if (mirrorChildren != null)
            compound.put("MirrorChildren", NBTHelper.writeCompoundList(mirrorChildren, NbtUtils::writeBlockPos));
    }

    public void startMirroringOther(BlockPos parent) {
        if (parent.equals(worldPosition))
            return;
        if (!(level.getBlockEntity(parent) instanceof SailBlockEntity pbe))
            return;
        if (pbe.getType() != getType())
            return;
        if (pbe.mirrorChildren == null)
            pbe.mirrorChildren = new ArrayList<>();
        pbe.mirrorChildren.add(worldPosition);
        pbe.notifyUpdate();

        mirrorParent = parent;
        try {
            assemble();
        } catch (AssemblyException e) {
        }
        notifyUpdate();
    }

    public void notifyMirrorsOfDisassembly() {
        if (mirrorChildren == null)
            return;
        for (BlockPos blockPos : mirrorChildren) {
            if (!(level.getBlockEntity(blockPos) instanceof SailBlockEntity pbe))
                continue;
            pbe.offset = offset;
            this.totalSails = (int) offset;
            pbe.disassemble();
            pbe.mirrorParent = null;
            pbe.notifyUpdate();
        }
        mirrorChildren.clear();
        notifyUpdate();
    }

    @Override
    protected int getExtensionRange() {
        return Math.max(0, Math.min(AllConfigs.server().kinetics.maxRopeLength.get(),
                (worldPosition.getY() - 1) - level.getMinBuildHeight()));
    }

    @Override
    protected int getInitialOffset() {
        return initialOffset;
    }

    @Override
    protected Vec3 toMotionVector(float speed) {
        return new Vec3(0, -speed, 0);
    }

    @Override
    protected ValueBoxTransform getMovementModeSlot() {
        return new CenteredSideValueBoxTransform((state, d) -> d == Direction.UP);
    }

    @Override
    public float getInterpolatedOffset(float partialTicks) {
        if (isVirtual())
            return Mth.lerp(partialTicks, prevAnimatedOffset, offset);
        boolean moving = running && (movedContraption == null || !movedContraption.isStalled());
        return super.getInterpolatedOffset(moving ? partialTicks : 0.5f);
    }


    public BlockPos getMirrorParent() {
        return mirrorParent;
    }

    // Threshold switch

    @Override
    public int getCurrentValue() {
        return worldPosition.getY() - (int) getInterpolatedOffset(.5f);
    }

    @Override
    public int getMinValue() {
        return level.getMinBuildHeight();
    }

    @Override
    public int getMaxValue() {
        return worldPosition.getY();
    }

    @Override
    public MutableComponent format(int value) {
        return CreateLang.translateDirect("gui.threshold_switch.pulley_y_level", value);
    }
}
