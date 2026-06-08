package org.lightning323.createkinetic;

import com.mojang.blaze3d.platform.InputConstants.Type;
import org.lightning323.createkinetic.content.joystick.JoystickControlClient;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;

public final class KineticKeys {
   private static final IKeyConflictContext JOYSTICK_ACTIVE_CONTEXT = new IKeyConflictContext() {
      public boolean isActive() {
         return JoystickControlClient.isActive();
      }

      public boolean conflicts(IKeyConflictContext other) {
         return true;
      }
   };
   public static final KeyMapping FREE_CAMERA;

   private KineticKeys() {
   }

   @SubscribeEvent
   public static void onRegister(RegisterKeyMappingsEvent event) {
      event.register(FREE_CAMERA);
   }

   public static boolean isFreeCameraHeld() {
      return FREE_CAMERA.isDown();
   }

   static {
      FREE_CAMERA = new KeyMapping("key.aeroworks.joystick.free_camera", JOYSTICK_ACTIVE_CONTEXT, Type.KEYSYM, 340, "key.categories.aeroworks");
   }
}
