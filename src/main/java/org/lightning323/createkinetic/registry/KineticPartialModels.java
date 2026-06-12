package org.lightning323.createkinetic.registry;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;
import org.lightning323.createkinetic.CreateKinetic;

public final class KineticPartialModels {
   public static final PartialModel GYROSCOPE_FLYWHEEL = block("gyroscope/flywheel");
   public static final PartialModel GYROSCOPE_INDICATOR = block("gyroscope/indicator");
   public static final PartialModel JOYSTICK_HANDLE = block("joystick/handle");
   public static final PartialModel JOYSTICK_INDICATOR = block("joystick/indicator");
   public static final PartialModel JOYSTICK_BUTTON = block("joystick/button");
   public static final PartialModel JOYSTICK_PREVIEW = block("joystick/preview");

   private KineticPartialModels() {
   }

   public static void init() {
   }

   private static PartialModel block(String path) {
      return PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateKinetic.MOD_ID, "block/" + path));
   }
}
