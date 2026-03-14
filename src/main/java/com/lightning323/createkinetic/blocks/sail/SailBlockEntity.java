package com.lightning323.createkinetic.blocks.sail;

import com.lightning323.createkinetic.registries.KineticBlocks;
import com.lightning323.createkinetic.ship.KineticShipControl;
import com.lightning323.createkinetic.ship.ShipUtils;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.content.contraptions.*;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SailBlockEntity extends KineticBlockEntity implements IDisplayAssemblyExceptions, ThresholdSwitchObservable {

    public float offset;
    public boolean running;
    public boolean assembleNextTick;
    public boolean needsContraption;
    protected boolean forceMove;
    protected ScrollOptionBehaviour<IControlContraption.MovementMode> movementMode;
    protected boolean waitingForSpeedChange;
    protected AssemblyException lastException;
    protected double sequencedOffsetLimit;

    // Custom position sync
    protected float clientOffsetDiff;


    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        movementMode = new ScrollOptionBehaviour<>(IControlContraption.MovementMode.class, CreateLang.translateDirect("contraptions.movement_mode"),
                this, getMovementModeSlot());
        movementMode.withCallback(t -> waitingForSpeedChange = false);
        behaviours.add(movementMode);
        registerAwardables(behaviours, AllAdvancements.CONTRAPTION_ACTORS);
    }

    @Override
    protected boolean syncSequenceContext() {
        return true;
    }

    @Override
    public void tick() {
        float prevOffset = offset;
        super.tick();

        if (level.isClientSide() && mirrorParent != null)
            if (sharedMirrorContraption == null || sharedMirrorContraption.get() == null
                    || !sharedMirrorContraption.get()
                    .isAlive()) {
                sharedMirrorContraption = null;
//                if (level.getBlockEntity(mirrorParent) instanceof SailBlockEntity pte && pte.movedContraption != null)
//                    sharedMirrorContraption = new WeakReference<>(pte.movedContraption);
            }

        if (isVirtual())
            prevAnimatedOffset = offset;
        invalidateRenderBoundingBox();

        if (isPassive())
            return;

        if (level.isClientSide)
            clientOffsetDiff *= .75f;

        if (waitingForSpeedChange) {
            return;
        }

        if (!level.isClientSide && assembleNextTick) {
            assembleNextTick = false;
            if (running) {
                if (getSpeed() == 0)
                    tryDisassemble();
                else
                    sendData();
                return;
            } else {
                if (getSpeed() != 0)
                    try {
                        assemble();
                        lastException = null;
                    } catch (AssemblyException e) {
                        lastException = e;
                    }
                sendData();
            }
            return;
        }

        if (!running)
            return;

        boolean contraptionPresent = false;
        if (needsContraption && !contraptionPresent)
            return;

        float movementSpeed = getMovementSpeed();
        boolean locked = false;
        if (sequencedOffsetLimit > 0) {
            sequencedOffsetLimit = Math.max(0, sequencedOffsetLimit - Math.abs(movementSpeed));
            locked = sequencedOffsetLimit == 0;
        }
        float newOffset = offset + movementSpeed;
        if ((int) newOffset != (int) offset)
            visitNewPosition();

        if (locked) {
            forceMove = true;
            sendData();
        }


        if (!contraptionPresent)
            offset = newOffset;

        int extensionRange = getExtensionRange();
        if (offset <= 0 || offset >= extensionRange) {
            offset = offset <= 0 ? 0 : extensionRange;
            if (!level.isClientSide) {
                moveAndCollideContraption();
                tryDisassemble();
                if (waitingForSpeedChange) {
                    forceMove = true;
                    sendData();
                }
            }
            return;
        }
    }


    @Override
    public void lazyTick() {
        super.lazyTick();
    }

    protected int getGridOffset(float offset) {
        return Mth.clamp((int) (offset + .5f), 0, getExtensionRange());
    }


    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        sequencedOffsetLimit = -1;

        if (isPassive())
            return;

        assembleNextTick = true;
        waitingForSpeedChange = false;


        if (sequenceContext != null && sequenceContext.instruction() == SequencerInstructions.TURN_DISTANCE)
            sequencedOffsetLimit = sequenceContext.getEffectiveValue(getTheoreticalSpeed());
    }


    @Override
    public AssemblyException getLastAssemblyException() {
        return lastException;
    }


    protected void tryDisassemble() {
        if (remove) {
            disassemble();
            return;
        }
        if (getMovementMode() == IControlContraption.MovementMode.MOVE_NEVER_PLACE) {
            waitingForSpeedChange = true;
            return;
        }
        int initial = getInitialOffset();
        if ((int) (offset + .5f) != initial && getMovementMode() == IControlContraption.MovementMode.MOVE_PLACE_RETURNED) {
            waitingForSpeedChange = true;
            return;
        }
        disassemble();
    }

    protected IControlContraption.MovementMode getMovementMode() {
        return movementMode.get();
    }

    protected boolean moveAndCollideContraption() {
        return false;
    }

    protected void collided() {
        if (level.isClientSide) {
            waitingForSpeedChange = true;
            return;
        }
        offset = getGridOffset(offset - getMovementSpeed());

        tryDisassemble();
    }

    public float getMovementSpeed() {
        float movementSpeed = Mth.clamp(convertToLinear(getSpeed()), -.49f, .49f) + clientOffsetDiff / 2f;
        if (level.isClientSide)
            movementSpeed *= ServerSpeedProvider.get();
        if (sequencedOffsetLimit >= 0)
            movementSpeed = (float) Mth.clamp(movementSpeed, -sequencedOffsetLimit, sequencedOffsetLimit);
        return movementSpeed;
    }


    public void onLengthBroken() {
        offset = 0;
        sendData();
    }

//    @Override
//    public void onLoad() {
//        super.onLoad();
//        ShipUtils.addSail(getLevel(), getBlockPos(), this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_AXIS));
//    }
//
//    @Override
//    public void remove() {
//        super.remove();
//        ShipUtils.removeSail(getLevel(), getBlockPos(), this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_AXIS));
//    }


    protected int initialOffset;
    private float prevAnimatedOffset;

    protected BlockPos mirrorParent;
    protected List<BlockPos> mirrorChildren;
    public WeakReference<AbstractContraptionEntity> sharedMirrorContraption;

    public SailBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(3);
        forceMove = true;
        needsContraption = true;
        sequencedOffsetLimit = -1;
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


    protected boolean isPassive() {
        return mirrorParent != null;
    }


    protected void assemble() throws AssemblyException {
        if (!(level.getBlockState(worldPosition)
                .getBlock() instanceof SailBlock))
            return;
        if (speed == 0 && mirrorParent == null)
            return;
        int maxLength = AllConfigs.server().kinetics.maxRopeLength.get();
        int i = 1;
        while (i <= maxLength) {
            BlockPos ropePos = worldPosition.below(i);
            BlockState ropeState = level.getBlockState(ropePos);
            if (!KineticBlocks.SAIL_CLOTH.has(ropeState) && !KineticBlocks.PULLEY_SAIL_WEIGHT.has(ropeState)) {
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
            boolean canAssembleStructure = true;

//            if (canAssembleStructure) {
//                Direction movementDirection = getSpeed() > 0 ? Direction.DOWN : Direction.UP;
//                if (ContraptionCollider.isCollidingWithWorld(level, contraption, anchor.relative(movementDirection),movementDirection))
//                    canAssembleStructure = false;
//            }

            if (!canAssembleStructure && getSpeed() > 0)
                return;

            removeRopes();

//            if (!contraption.getBlocks().isEmpty()) {
////                contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
////                movedContraption = ControlledContraptionEntity.create(level, this, contraption);
////                movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
////                level.addFreshEntity(movedContraption);
//                forceMove = true;
//                needsContraption = true;
//
//                if (contraption.containsBlockBreakers())
//                    award(AllAdvancements.CONTRAPTION_ACTORS);
//
//                for (BlockPos pos : contraption.createColliders(level, Direction.UP)) {
//                    if (pos.getY() != 0)
//                        continue;
//                    pos = pos.offset(anchor);
//                    if (level.getBlockEntity(
//                            new BlockPos(pos.getX(), worldPosition.getY(), pos.getZ())) instanceof SailBlockEntity pbe)
//                        pbe.startMirroringOther(worldPosition);
//                }
//            }
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
            level.setBlock(offset, oldState.getFluidState().createLegacyBlock(), 66);
        }
    }


    public int getTotalSails() {
        return totalSails;
    }


    public void disassemble() {
        if (!running && mirrorParent == null)
            return;
        offset = getGridOffset(offset);


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
                        boolean success = level.setBlock(magnetPos, KineticBlocks.PULLEY_SAIL_WEIGHT.getDefaultState()
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
                        boolean success = level.setBlock(sailPos, KineticBlocks.SAIL_CLOTH.getDefaultState()
                                        .setValue(BlockStateProperties.WATERLOGGED, waterlog[i]) //Waterlogged property
                                        .setValue(BlockStateProperties.HORIZONTAL_AXIS, //Horizontal axis property
                                                this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_AXIS))
                                , 66);
                        if (success) actuallyPlacedSails++;
                    }
                }
            }

            this.totalSails = (int) actuallyPlacedSails;
            notifyMirrorsOfDisassembly();
            updateSailCount();
        }

//        if (movedContraption != null)
//            movedContraption.discard();

//        movedContraption = null;
        initialOffset = 0;
        running = false;
        sendData();
    }

    protected boolean shouldCreateRopes() {
        return !remove;
    }

    private void updateSailCount() {
        if (getLevel().isClientSide) return;
        KineticShipControl shipController = ShipUtils.getOrCreateShipController(getLevel(), getBlockPos());
        if (shipController != null) {
            shipController.updateSailCount((ServerLevel) level);
        }
    }

    protected void visitNewPosition() {

        if (level.isClientSide)
            return;
//        if (movedContraption != null)
//            return;
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


        boolean forceMovement = compound.contains("ForceMovement");
        float offsetBefore = offset;

        running = compound.getBoolean("Running");
        waitingForSpeedChange = compound.getBoolean("Waiting");
        offset = compound.getFloat("Offset");
        sequencedOffsetLimit =
                compound.contains("SequencedOffsetLimit") ? compound.getDouble("SequencedOffsetLimit") : -1;
        lastException = AssemblyException.read(compound);
        super.read(compound, clientPacket);

        if (!clientPacket)
            return;
        else if (running) {
            clientOffsetDiff = offset - offsetBefore;
            offset = offsetBefore;
        }

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

    public void write(CompoundTag compound, boolean clientPacket) {
        compound.putInt("InitialOffset", initialOffset);
        compound.putBoolean("Running", running);
        compound.putBoolean("Waiting", waitingForSpeedChange);
        compound.putFloat("Offset", offset);
        if (sequencedOffsetLimit >= 0)
            compound.putDouble("SequencedOffsetLimit", sequencedOffsetLimit);
        AssemblyException.write(compound, lastException);
        super.write(compound, clientPacket);

        if (clientPacket && forceMove) {
            compound.putBoolean("ForceMovement", forceMove);
            forceMove = false;
        }

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

    protected int getExtensionRange() {
        return Math.max(0, Math.min(AllConfigs.server().kinetics.maxRopeLength.get(),
                (worldPosition.getY() - 1) - level.getMinBuildHeight()));
    }

    protected int getInitialOffset() {
        return initialOffset;
    }

    protected ValueBoxTransform getMovementModeSlot() {
        return new CenteredSideValueBoxTransform((state, d) -> d == Direction.UP);
    }

    public float getInterpolatedOffset(float partialTicks) {
        if (isVirtual())
            return Mth.lerp(partialTicks, prevAnimatedOffset, offset);
        boolean moving = running;

        float interpolatedOffset = Mth.clamp(offset + (moving ? partialTicks : 0.5f - .5f) * getMovementSpeed(), 0, getExtensionRange());
        return interpolatedOffset;
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
