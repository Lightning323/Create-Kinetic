package org.lightning323.createkinetic.mixin.compat.drivebywire;//package org.lightning323.createkinetic.mixin.compat.drivebywire;
//
//import org.lightning323.createkinetic.compat.drivebywire.DriveByWireBridge;
//import org.lightning323.createkinetic.compat.drivebywire.JoystickWireChannels;
//import org.lightning323.createkinetic.content.blocks.joystick.JoystickBlockEntity;
//import org.lightning323.createkinetic.content.blocks.joystick.JoystickDirection;
//import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntityType;
//import net.minecraft.world.level.block.state.BlockState;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin({JoystickBlockEntity.class})
//public abstract class JoystickBlockEntityWireMixin extends SmartBlockEntity {
//   @Unique
//   private static final int BUTTON_SLOT;
//   @Shadow
//   private byte tiltX;
//   @Shadow
//   private byte tiltY;
//   @Shadow
//   private boolean buttonPressed;
//   @Unique
//   private final int[] createkinetic$lastPublishedStrength;
//   @Unique
//   private boolean createkinetic$primed;
//
//   private JoystickBlockEntityWireMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
//      super(type, pos, state);
//      this.createkinetic$lastPublishedStrength = new int[BUTTON_SLOT + 1];
//   }
//
//   @Inject(
//      method = {"pushNetworkIfChanged"},
//      at = {@At("HEAD")}
//   )
//   private void createkinetic$publishToWire(CallbackInfo ci) {
//      Level level = this.getLevel();
//      if (level != null && !level.isClientSide) {
//         if (!this.createkinetic$primed) {
//            this.createkinetic$primed = true;
//
//            for(JoystickDirection direction : JoystickDirection.VALUES) {
//               this.createkinetic$lastPublishedStrength[direction.index] = direction.strengthFor(this.tiltX, this.tiltY);
//            }
//
//            this.createkinetic$lastPublishedStrength[BUTTON_SLOT] = this.buttonPressed ? 15 : 0;
//         } else {
//            for(JoystickDirection direction : JoystickDirection.VALUES) {
//               this.createkinetic$publishIfChanged(direction.index, JoystickWireChannels.channelFor(direction), direction.strengthFor(this.tiltX, this.tiltY));
//            }
//
//            this.createkinetic$publishIfChanged(BUTTON_SLOT, "createkinetic.wire.channel.button", this.buttonPressed ? 15 : 0);
//         }
//      }
//   }
//
//   @Inject(
//      method = {"invalidate"},
//      at = {@At("HEAD")}
//   )
//   private void createkinetic$clearWire(CallbackInfo ci) {
//      if (!this.isChunkUnloaded()) {
//         Level level = this.getLevel();
//         if (level != null && !level.isClientSide) {
//            DriveByWireBridge.clear(level, this.getBlockPos());
//         }
//      }
//   }
//
//   @Unique
//   private void createkinetic$publishIfChanged(int slot, String channel, int strength) {
//      if (strength != this.createkinetic$lastPublishedStrength[slot]) {
//         this.createkinetic$lastPublishedStrength[slot] = strength;
//         DriveByWireBridge.publish(this.getLevel(), this.getBlockPos(), channel, strength);
//      }
//   }
//
//   static {
//      BUTTON_SLOT = JoystickDirection.VALUES.length;
//   }
//}
