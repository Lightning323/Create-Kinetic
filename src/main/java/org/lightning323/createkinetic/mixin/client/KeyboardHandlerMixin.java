package org.lightning323.createkinetic.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import org.lightning323.createkinetic.content.blocks.joystick.JoystickControlClient;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({KeyboardHandler.class})
public class KeyboardHandlerMixin {
   @Inject(
      method = {"keyPress(JIIII)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void createkinetic$keyPress(long window, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
      if (JoystickControlClient.isActive()) {
         if (Minecraft.getInstance().screen == null) {
            if (key == 256 && action == 1) {
               JoystickControlClient.requestExit();
               ci.cancel();
            } else {
               InputConstants.Key boundKey = InputConstants.getKey(key, scanCode);
               int slot = JoystickControlClient.findBoundSlot(boundKey);
               if (slot >= 0) {
                  if (action == 1) {
                     JoystickControlClient.onBoundKey(slot, true);
                  } else if (action == 0) {
                     JoystickControlClient.onBoundKey(slot, false);
                  }

                  ci.cancel();
               }
            }
         }
      }
   }
}
