package org.lightning323.createkinetic.registry;

import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.content.blocks.joystick.JoystickMenu;
import org.lightning323.createkinetic.content.blocks.joystick.JoystickScreen;
import com.tterrag.registrate.util.entry.MenuEntry;

public final class KineticMenuTypes {
   public static final MenuEntry<JoystickMenu> JOYSTICK = CreateKinetic.getRegistrate().menu("joystick", JoystickMenu::new, () -> JoystickScreen::new).register();

   private KineticMenuTypes() {
   }

   public static void register() {
   }
}
