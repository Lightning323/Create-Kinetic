package org.lightning323.createkinetic.content.blocks.gyroscope;

import org.lightning323.createkinetic.config.Config;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class GyroscopeBlockEntity extends KineticBlockEntity implements BlockEntitySubLevelActor {
   private static final float CHASE_RATE = 0.015625F;
   private static final float INDICATOR_CHASE_RATE = 0.15F;
   private static final int UNSTABLE_THRESHOLD = 50;
   private static final int LEVEL_THRESHOLD = 95;
   private static final int BAR_LENGTH = 18;
   private final LerpedFloat effectiveSpeed = LerpedFloat.linear();
   private final LerpedFloat indicatorX = LerpedFloat.linear();
   private final LerpedFloat indicatorZ = LerpedFloat.linear();
   private float angle;
   private boolean effectiveSpeedInitialized = false;
   private byte stabilizedPercent;
   private byte ownSignalX;
   private byte ownSignalZ;

   public float effectiveSpeedAt(float partialTicks) {
      return this.effectiveSpeed.getValue(partialTicks);
   }

   public float getAngle() {
      return this.angle;
   }

   public double currentRpmScale() {
      return Math.min((double)Math.abs(this.effectiveSpeed.getValue()) / Config.gyroscopeReferenceRpm(), (double)1.0F);
   }

   public int getStabilizedPercent() {
      return Byte.toUnsignedInt(this.stabilizedPercent);
   }

   public int getOwnSignalX() {
      return Byte.toUnsignedInt(this.ownSignalX);
   }

   public int getOwnSignalZ() {
      return Byte.toUnsignedInt(this.ownSignalZ);
   }

   public float indicatorXAt(float partialTicks) {
      return this.indicatorX.getValue(partialTicks);
   }

   public float indicatorZAt(float partialTicks) {
      return this.indicatorZ.getValue(partialTicks);
   }

   public void setStabilizedPercent(int percent) {
      byte clamped = (byte)Math.max(0, Math.min(100, percent));
      if (clamped != this.stabilizedPercent) {
         this.stabilizedPercent = clamped;
         this.sendData();
      }
   }

   public GyroscopeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   protected AABB createRenderBoundingBox() {
      return new AABB((double)(this.worldPosition.getX() - 1), (double)this.worldPosition.getY(), (double)(this.worldPosition.getZ() - 1), (double)(this.worldPosition.getX() + 2), (double)(this.worldPosition.getY() + 1), (double)(this.worldPosition.getZ() + 2));
   }

   public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
   }

   protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
      super.read(compound, registries, clientPacket);
      if (clientPacket) {
         this.effectiveSpeed.chase((double)this.getSpeed(), (double)0.015625F, Chaser.EXP);
         this.stabilizedPercent = compound.getByte("StabilizedPercent");
         this.ownSignalX = compound.getByte("OwnSignalX");
         this.ownSignalZ = compound.getByte("OwnSignalZ");
      }

   }

   public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
      super.write(compound, registries, clientPacket);
      if (clientPacket) {
         compound.putByte("StabilizedPercent", this.stabilizedPercent);
         compound.putByte("OwnSignalX", this.ownSignalX);
         compound.putByte("OwnSignalZ", this.ownSignalZ);
      }

   }

   public void tick() {
      super.tick();
      float target = this.getSpeed();
      if (!this.effectiveSpeedInitialized) {
         this.effectiveSpeed.startWithValue((double)target);
         this.indicatorX.startWithValue((double)((float)this.getOwnSignalX() / 15.0F));
         this.indicatorZ.startWithValue((double)((float)this.getOwnSignalZ() / 15.0F));
         this.effectiveSpeedInitialized = true;
      }

      this.effectiveSpeed.chase((double)target, (double)0.015625F, Chaser.EXP);
      this.effectiveSpeed.tickChaser();
      if (this.level != null) {
         if (this.level.isClientSide) {
            this.angle += this.effectiveSpeed.getValue() * 3.0F / 10.0F;
            this.angle %= 360.0F;
            this.indicatorX.chase((double)((float)this.getOwnSignalX() / 15.0F), (double)0.15F, Chaser.EXP);
            this.indicatorZ.chase((double)((float)this.getOwnSignalZ() / 15.0F), (double)0.15F, Chaser.EXP);
            this.indicatorX.tickChaser();
            this.indicatorZ.tickChaser();
         } else {
            byte newX = (byte)Math.max(this.level.getSignal(this.worldPosition.east(), Direction.EAST), this.level.getSignal(this.worldPosition.west(), Direction.WEST));
            byte newZ = (byte)Math.max(this.level.getSignal(this.worldPosition.north(), Direction.NORTH), this.level.getSignal(this.worldPosition.south(), Direction.SOUTH));
            if (newX != this.ownSignalX || newZ != this.ownSignalZ) {
               this.ownSignalX = newX;
               this.ownSignalZ = newZ;
               this.sendData();
            }

         }
      }
   }

   public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
      double partialPhysicsTick = SubLevelPhysicsSystem.getCurrentlySteppingSystem().getPartialPhysicsTick();
      GyroscopeController.of(subLevel).tick(partialPhysicsTick, handle, timeStep);
   }

   public void remove() {
      super.remove();
      if (this.level != null && !this.level.isClientSide) {
         SubLevel sub = Sable.HELPER.getContaining(this);
         if (sub instanceof ServerSubLevel) {
            ServerSubLevel server = (ServerSubLevel)sub;

            for(BlockEntitySubLevelActor actor : server.getPlot().getBlockEntityActors()) {
               if (actor != this && actor instanceof GyroscopeBlockEntity) {
                  return;
               }
            }

            GyroscopeController.detach(server);
         }
      }
   }

   public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
      int stability = this.getStabilizedPercent();
      StatusTier tier = StatusTier.of(stability, this.effectiveSpeed.getValue() == 0.0F);
      Component stateWord = Component.translatable(tier.langKey).withStyle(tier.color);
      tooltip.add(Component.literal("    ").append(Component.translatable("tooltip.createkinetic.gyroscope.header", new Object[]{stateWord}).withStyle(ChatFormatting.WHITE)));
      int redBreak = (int)Math.round((double)9.0F);
      int sigX = this.getOwnSignalX();
      int sigZ = this.getOwnSignalZ();
      boolean showStiffness = sigX > 0 || sigZ > 0;
      Component stabilitySpacer = showStiffness ? Component.translatable("tooltip.createkinetic.gyroscope.stability_dots").withStyle(ChatFormatting.DARK_GRAY) : Component.literal(" ");
      tooltip.add(Component.literal("     ").append(Component.translatable("tooltip.createkinetic.gyroscope.stability").withStyle(ChatFormatting.GRAY)).append(stabilitySpacer).append(thresholdBar((float)stability / 100.0F, redBreak, 17, ChatFormatting.RED, ChatFormatting.YELLOW, ChatFormatting.GREEN)));
      if (showStiffness) {
         tooltip.add(axisLine("tooltip.createkinetic.gyroscope.stiffness.x", sigX, ChatFormatting.GREEN));
         tooltip.add(axisLine("tooltip.createkinetic.gyroscope.stiffness.z", sigZ, ChatFormatting.BLUE));
      }

      tooltip.add(Component.empty());
      super.addToGoggleTooltip(tooltip, isPlayerSneaking);
      return true;
   }

   private static Component axisLine(String labelKey, int signal, ChatFormatting color) {
      float stiffness = 1.0F - (float)signal / 15.0F;
      return Component.literal("     ").append(Component.translatable("tooltip.createkinetic.gyroscope.stiffness_indent").withStyle(ChatFormatting.DARK_GRAY)).append(Component.translatable(labelKey).withStyle(color)).append(Component.translatable(labelKey + "_dots").withStyle(ChatFormatting.DARK_GRAY)).append(stiffnessBar(stiffness, 18, color, ChatFormatting.DARK_GRAY));
   }

   private static MutableComponent stiffnessBar(float fraction, int total, ChatFormatting filledColor, ChatFormatting emptyColor) {
      int filled = Math.max(0, Math.min(total, Math.round(fraction * (float)total)));
      return Component.empty().append(repeatBar(filled, filledColor)).append(repeatBar(total - filled, emptyColor));
   }

   private static MutableComponent thresholdBar(float fraction, int firstBreak, int secondBreak, ChatFormatting first, ChatFormatting second, ChatFormatting third) {
      int filled = Math.max(0, Math.min(18, Math.round(fraction * 18.0F)));
      int firstFilled = Math.min(filled, firstBreak);
      int secondFilled = Math.max(0, Math.min(filled, secondBreak) - firstBreak);
      int thirdFilled = Math.max(0, filled - secondBreak);
      int empty = 18 - filled;
      return Component.empty().append(repeatBar(firstFilled, first)).append(repeatBar(secondFilled, second)).append(repeatBar(thirdFilled, third)).append(repeatBar(empty, ChatFormatting.DARK_GRAY));
   }

   private static MutableComponent repeatBar(int count, ChatFormatting color) {
      return count <= 0 ? Component.empty() : Component.literal("|".repeat(count)).withStyle(color);
   }

   private static enum StatusTier {
      OFF(ChatFormatting.GRAY, "tooltip.createkinetic.gyroscope.status.off"),
      UNSTABLE(ChatFormatting.RED, "tooltip.createkinetic.gyroscope.status.unstable"),
      STEADY(ChatFormatting.YELLOW, "tooltip.createkinetic.gyroscope.status.steady"),
      LEVEL(ChatFormatting.GREEN, "tooltip.createkinetic.gyroscope.status.level");

      final ChatFormatting color;
      final String langKey;

      private StatusTier(final ChatFormatting color, final String langKey) {
         this.color = color;
         this.langKey = langKey;
      }

      static StatusTier of(int stability, boolean off) {
         if (off) {
            return OFF;
         } else if (stability >= 95) {
            return LEVEL;
         } else {
            return stability >= 50 ? STEADY : UNSTABLE;
         }
      }

      // $FF: synthetic method
      private static StatusTier[] $values() {
         return new StatusTier[]{OFF, UNSTABLE, STEADY, LEVEL};
      }
   }
}
