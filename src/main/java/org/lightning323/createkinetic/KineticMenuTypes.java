package org.lightning323.createkinetic;

import org.lightning323.createkinetic.content.joystick.JoystickMenu;
import org.lightning323.createkinetic.content.joystick.JoystickScreen;
import com.tterrag.registrate.util.entry.MenuEntry;

public final class KineticMenuTypes {
   public static final MenuEntry<JoystickMenu> JOYSTICK = CreateKinetic.getRegistrate().menu("joystick", JoystickMenu::new, () -> JoystickScreen::new).register();

   private KineticMenuTypes() {
   }

   public static void register() {
   }
}
