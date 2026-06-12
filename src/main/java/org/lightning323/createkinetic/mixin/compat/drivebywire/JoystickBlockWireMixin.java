package org.lightning323.createkinetic.mixin.compat.drivebywire;//package org.lightning323.createkinetic.mixin.compat.drivebywire;
//
//import org.lightning323.createkinetic.compat.drivebywire.JoystickWireChannels;
//import org.lightning323.createkinetic.content.blocks.joystick.JoystickBlock;
//import edn.stratodonut.drivebywire.wire.MultiChannelWireSource;
//import java.util.List;
//import org.spongepowered.asm.mixin.Mixin;
//
//@Mixin({JoystickBlock.class})
//public abstract class JoystickBlockWireMixin implements MultiChannelWireSource {
//   public List<String> wire$getChannels() {
//      return JoystickWireChannels.ALL;
//   }
//
//   public String wire$nextChannel(String current, boolean forward) {
//      return JoystickWireChannels.nextChannel(current, forward);
//   }
//}
