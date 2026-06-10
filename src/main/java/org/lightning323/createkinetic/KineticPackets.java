package org.lightning323.createkinetic;

import org.lightning323.createkinetic.content.joystick.C2SExitJoystickControl;
import org.lightning323.createkinetic.content.joystick.C2SJoystickButton;
import org.lightning323.createkinetic.content.joystick.C2SJoystickClearFrequencies;
import org.lightning323.createkinetic.content.joystick.C2SJoystickSetBind;
import org.lightning323.createkinetic.content.joystick.C2SJoystickShowHud;
import org.lightning323.createkinetic.content.joystick.C2SJoystickSpringBack;
import org.lightning323.createkinetic.content.joystick.C2SJoystickTilt;
import org.lightning323.createkinetic.content.joystick.C2SJoystickUseMouseInput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class KineticPackets {
   private KineticPackets() {
   }

   @SubscribeEvent
   public static void onRegister(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar r = event.registrar(CreateKinetic.MOD_ID).versioned("1");
      r.playToServer(C2SExitJoystickControl.TYPE, C2SExitJoystickControl.STREAM_CODEC, C2SExitJoystickControl::handleOnServer);
      r.playToServer(C2SJoystickTilt.TYPE, C2SJoystickTilt.STREAM_CODEC, C2SJoystickTilt::handleOnServer);
      r.playToServer(C2SJoystickShowHud.TYPE, C2SJoystickShowHud.STREAM_CODEC, C2SJoystickShowHud::handleOnServer);
      r.playToServer(C2SJoystickButton.TYPE, C2SJoystickButton.STREAM_CODEC, C2SJoystickButton::handleOnServer);
      r.playToServer(C2SJoystickClearFrequencies.TYPE, C2SJoystickClearFrequencies.STREAM_CODEC, C2SJoystickClearFrequencies::handleOnServer);
      r.playToServer(C2SJoystickSetBind.TYPE, C2SJoystickSetBind.STREAM_CODEC, C2SJoystickSetBind::handleOnServer);
      r.playToServer(C2SJoystickUseMouseInput.TYPE, C2SJoystickUseMouseInput.STREAM_CODEC, C2SJoystickUseMouseInput::handleOnServer);
      r.playToServer(C2SJoystickSpringBack.TYPE, C2SJoystickSpringBack.STREAM_CODEC, C2SJoystickSpringBack::handleOnServer);
   }
}
