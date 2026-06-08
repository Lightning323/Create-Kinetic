package org.lightning323.createkinetic.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import org.lightning323.createkinetic.KineticKeys;
import org.lightning323.createkinetic.content.joystick.JoystickControlClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MouseHandler.class})
public class MouseHandlerMixin {
   @Shadow
   private double accumulatedDX;
   @Shadow
   private double accumulatedDY;

   private static boolean createkinetic$shouldSuppressInput() {
      return JoystickControlClient.isActive() && Minecraft.getInstance().screen == null;
   }

   @Inject(
      method = {"turnPlayer(D)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void createkinetic$turnPlayer(double deltaTime, CallbackInfo ci) {
      if (createkinetic$shouldSuppressInput()) {
         if (JoystickControlClient.isMouseCaptured()) {
            if (!KineticKeys.isFreeCameraHeld()) {
               JoystickControlClient.feedMouseDelta(this.accumulatedDX, this.accumulatedDY);
               ci.cancel();
            }
         }
      }
   }

   @Inject(
      method = {"onPress(JIII)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void createkinetic$onPress(long window, int button, int action, int mods, CallbackInfo ci) {
      if (createkinetic$shouldSuppressInput()) {
         if (action != 2) {
            InputConstants.Key key = Type.MOUSE.getOrCreate(button);
            int slot = JoystickControlClient.findBoundSlot(key);
            if (slot >= 0) {
               JoystickControlClient.onBoundKey(slot, action == 1);
            }
         }

         ci.cancel();
      }
   }
}
