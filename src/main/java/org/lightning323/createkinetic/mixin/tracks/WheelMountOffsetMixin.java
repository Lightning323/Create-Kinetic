/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyExpressionValue
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlock
 *  dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Constant
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.ModifyConstant
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package org.lightning323.createkinetic.mixin.tracks;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import org.lightning323.createkinetic.mixin_interface.WheelMountOffsetAccess;
import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlock;
import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={WheelMountBlockEntity.class})
public abstract class WheelMountOffsetMixin
extends KineticBlockEntity
implements WheelMountOffsetAccess {
    @Unique
    private static final double Tracks_MAX_LATERAL_OFFSET = 1.5;
    @Unique
    private static final double Tracks_MIN_LATERAL_OFFSET = -1.0;
    @Unique
    private static final double Tracks_LATERAL_OFFSET_STEP = 0.125;
    @Unique
    private static final double Tracks_MAX_LONGITUDINAL_OFFSET = 1.5;
    @Unique
    private static final double Tracks_MIN_LONGITUDINAL_OFFSET = -1.0;
    @Unique
    private static final double Tracks_LONGITUDINAL_OFFSET_STEP = 0.125;
    @Unique
    private static final double Tracks_MAX_HEIGHT_OFFSET = 0.75;
    @Unique
    private static final double Tracks_HEIGHT_OFFSET_STEP = 0.125;
    @Shadow
    protected int clientSteeringSignalLeft;
    @Shadow
    protected int clientSteeringSignalRight;
    @Unique
    private double kinetic$lateralOffset = 0.0;
    @Unique
    private double kinetic$lastLateralOffset = 0.0;
    @Unique
    private double kinetic$longitudinalOffset = 0.0;
    @Unique
    private double kinetic$lastLongitudinalOffset = 0.0;
    @Unique
    private double kinetic$heightOffset = 0.0;
    @Unique
    private double kinetic$lastHeightOffset = 0.0;
    @Unique
    private double kinetic$wheelSpringMultiplier = 1.0;
    @Unique
    private double kinetic$wheelDampingMultiplier = 1.0;
    @Unique
    private double kinetic$wheelDriveMultiplier = 1.0;
    @Unique
    private double kinetic$wheelGripMultiplier = 1.0;
    @Unique
    private boolean kinetic$visualSuspensionHidden = false;

    @Shadow
    protected abstract double getLerpedYaw(double var1);

    public WheelMountOffsetMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public double kinetic$adjustLateralOffset(int direction) {
        double previous = this.kinetic$lateralOffset;
        this.kinetic$lateralOffset = Mth.clamp((double)((double)Math.round((this.kinetic$lateralOffset + (double)direction * 0.125) / 0.125) * 0.125), (double)-1.0, (double)1.5);
        if (Math.abs(previous - this.kinetic$lateralOffset) > 1.0E-6) {
            this.setChanged();
            if (this.level != null && !this.level.isClientSide) {
                this.sendData();
            }
        }
        return this.kinetic$lateralOffset;
    }

    @Override
    public double kinetic$adjustLongitudinalOffset(int direction) {
        double previous = this.kinetic$longitudinalOffset;
        this.kinetic$longitudinalOffset = Mth.clamp((double)((double)Math.round((this.kinetic$longitudinalOffset + (double)direction * 0.125) / 0.125) * 0.125), (double)-1.0, (double)1.5);
        if (Math.abs(previous - this.kinetic$longitudinalOffset) > 1.0E-6) {
            this.setChanged();
            if (this.level != null && !this.level.isClientSide) {
                this.sendData();
            }
        }
        return this.kinetic$longitudinalOffset;
    }

    @Override
    public double kinetic$adjustHeightOffset(int direction, boolean sideInteraction) {
        double previous = this.kinetic$heightOffset;
        this.kinetic$heightOffset = Mth.clamp((double)((double)Math.round((this.kinetic$heightOffset + (double)direction * 0.125) / 0.125) * 0.125), (double)-0.75, (double)0.75);
        if (Math.abs(previous - this.kinetic$heightOffset) > 1.0E-6) {
            this.setChanged();
            if (this.level != null && !this.level.isClientSide) {
                this.sendData();
            }
        }
        return this.kinetic$heightOffset;
    }

    @Override
    public double kinetic$adjustTuning(String key, int direction) {
        double step = key.equals("drive") ? 0.1 : 0.05;
        double previous = this.kinetic$getTuning(key);
        double next = Mth.clamp((double)((double)Math.round((previous + (double)direction * step) / step) * step), (double)0.1, (double)4.0);
        switch (key) {
            case "spring": {
                this.kinetic$wheelSpringMultiplier = next;
                break;
            }
            case "damping": {
                this.kinetic$wheelDampingMultiplier = next;
                break;
            }
            case "drive": {
                this.kinetic$wheelDriveMultiplier = next;
                break;
            }
            case "grip": {
                this.kinetic$wheelGripMultiplier = next;
                break;
            }
            default: {
                return previous;
            }
        }
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.sendData();
        }
        return next;
    }

    @Override
    public double kinetic$getTuning(String key) {
        return switch (key) {
            case "spring" -> this.kinetic$wheelSpringMultiplier;
            case "damping" -> this.kinetic$wheelDampingMultiplier;
            case "drive" -> this.kinetic$wheelDriveMultiplier;
            case "grip" -> this.kinetic$wheelGripMultiplier;
            default -> 1.0;
        };
    }

    @Override
    public void kinetic$resetTuning() {
        this.kinetic$wheelSpringMultiplier = 1.0;
        this.kinetic$wheelDampingMultiplier = 1.0;
        this.kinetic$wheelDriveMultiplier = 1.0;
        this.kinetic$wheelGripMultiplier = 1.0;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.sendData();
        }
    }

    @Override
    public double kinetic$getLerpedLateralOffset(float partialTicks) {
        return Mth.lerp((double)partialTicks, (double)this.kinetic$lastLateralOffset, (double)this.kinetic$lateralOffset);
    }

    @Override
    public double kinetic$getLerpedLongitudinalOffset(float partialTicks) {
        return Mth.lerp((double)partialTicks, (double)this.kinetic$lastLongitudinalOffset, (double)this.kinetic$longitudinalOffset);
    }

    @Override
    public double kinetic$getLerpedHeightOffset(float partialTicks) {
        return Mth.lerp((double)partialTicks, (double)this.kinetic$lastHeightOffset, (double)this.kinetic$heightOffset);
    }

    @Override
    public double kinetic$getLerpedYaw(float partialTicks) {
        return this.getLerpedYaw(partialTicks);
    }

    @Override
    public int kinetic$getClientSteeringSignalLeft() {
        return this.clientSteeringSignalLeft;
    }

    @Override
    public boolean kinetic$isVisualSuspensionHidden() {
        return this.kinetic$visualSuspensionHidden;
    }

    @Override
    public void kinetic$toggleVisualSuspensionHidden() {
        this.kinetic$visualSuspensionHidden = !this.kinetic$visualSuspensionHidden;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.sendData();
        }
    }

    @Override
    public int kinetic$getClientSteeringSignalRight() {
        return this.clientSteeringSignalRight;
    }

    @Inject(method={"tick"}, at={@At(value="HEAD")})
    private void kinetic$tickOffset(CallbackInfo ci) {
        this.kinetic$lastLateralOffset = this.kinetic$lateralOffset;
        this.kinetic$lastLongitudinalOffset = this.kinetic$longitudinalOffset;
        this.kinetic$lastHeightOffset = this.kinetic$heightOffset;
    }

    @Inject(method={"write"}, at={@At(value="TAIL")})
    private void kinetic$writeOffset(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        tag.putDouble("TracksLateralOffset", this.kinetic$lateralOffset);
        tag.putDouble("TracksLongitudinalOffset", this.kinetic$longitudinalOffset);
        tag.putDouble("TracksHeightOffset", this.kinetic$heightOffset);
        tag.putDouble("TracksWheelSpringMultiplier", this.kinetic$wheelSpringMultiplier);
        tag.putDouble("TracksWheelDampingMultiplier", this.kinetic$wheelDampingMultiplier);
        tag.putDouble("TracksWheelDriveMultiplier", this.kinetic$wheelDriveMultiplier);
        tag.putDouble("TracksWheelGripMultiplier", this.kinetic$wheelGripMultiplier);
        tag.putBoolean("TracksVisualSuspensionHidden", this.kinetic$visualSuspensionHidden);
    }

    @Inject(method={"read"}, at={@At(value="TAIL")})
    private void kinetic$readOffset(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (tag.contains("TracksLateralOffset")) {
            this.kinetic$lateralOffset = tag.getDouble("TracksLateralOffset");
        }
        if (tag.contains("TracksLongitudinalOffset")) {
            this.kinetic$longitudinalOffset = tag.getDouble("TracksLongitudinalOffset");
        }
        if (tag.contains("TracksHeightOffset")) {
            this.kinetic$heightOffset = tag.getDouble("TracksHeightOffset");
        }
        if (tag.contains("TracksWheelSpringMultiplier")) {
            this.kinetic$wheelSpringMultiplier = tag.getDouble("TracksWheelSpringMultiplier");
        }
        if (tag.contains("TracksWheelDampingMultiplier")) {
            this.kinetic$wheelDampingMultiplier = tag.getDouble("TracksWheelDampingMultiplier");
        }
        if (tag.contains("TracksWheelDriveMultiplier")) {
            this.kinetic$wheelDriveMultiplier = tag.getDouble("TracksWheelDriveMultiplier");
        }
        if (tag.contains("TracksWheelGripMultiplier")) {
            this.kinetic$wheelGripMultiplier = tag.getDouble("TracksWheelGripMultiplier");
        }
        if (tag.contains("TracksVisualSuspensionHidden")) {
            this.kinetic$visualSuspensionHidden = tag.getBoolean("TracksVisualSuspensionHidden");
        }
        if (clientPacket) {
            this.kinetic$lastLateralOffset = this.kinetic$lateralOffset;
            this.kinetic$lastLongitudinalOffset = this.kinetic$longitudinalOffset;
            this.kinetic$lastHeightOffset = this.kinetic$heightOffset;
        }
    }

    @ModifyConstant(method={"sable$physicsTick"}, constant={@Constant(doubleValue=40.0)})
    private double kinetic$tuneWheelSpring(double original) {
        return original * this.kinetic$wheelSpringMultiplier;
    }

    @ModifyConstant(method={"sable$physicsTick"}, constant={@Constant(doubleValue=10.0)})
    private double kinetic$tuneWheelDamping(double original) {
        return original * this.kinetic$wheelDampingMultiplier;
    }

    @ModifyConstant(method={"sable$physicsTick"}, constant={@Constant(doubleValue=1.75)})
    private double kinetic$tuneWheelDrive(double original) {
        return original * this.kinetic$wheelDriveMultiplier;
    }

    @ModifyConstant(method={"sable$physicsTick"}, constant={@Constant(doubleValue=-0.6)})
    private double kinetic$tuneWheelGrip(double original) {
        return original * this.kinetic$wheelGripMultiplier;
    }

    @ModifyExpressionValue(method={"sable$physicsTick", "computeMaxExtensionToTerrain"}, at={@At(value="INVOKE", target="Lnet/minecraft/core/BlockPos;getCenter()Lnet/minecraft/world/phys/Vec3;")})
    private Vec3 kinetic$offsetWheelCenter(Vec3 original) {
        return this.kinetic$applyWheelOffset(original);
    }

    @Inject(method={"createRenderBoundingBox"}, at={@At(value="RETURN")}, cancellable=true)
    private void kinetic$inflateRenderBounds(CallbackInfoReturnable<AABB> cir) {
        cir.setReturnValue(((AABB)cir.getReturnValue()).inflate(Math.abs(this.kinetic$lateralOffset) + Math.abs(this.kinetic$longitudinalOffset)));
    }

    @Unique
    private Vec3 kinetic$applyWheelOffset(Vec3 original) {
        Direction facing = (Direction)this.getBlockState().getValue(WheelMountBlock.HORIZONTAL_FACING);
        return original.add(Vec3.atLowerCornerOf((Vec3i)facing.getClockWise().getNormal()).scale(this.kinetic$lateralOffset)).add(Vec3.atLowerCornerOf((Vec3i)facing.getNormal()).scale(this.kinetic$longitudinalOffset)).add(0.0, this.kinetic$heightOffset, 0.0);
    }
}

