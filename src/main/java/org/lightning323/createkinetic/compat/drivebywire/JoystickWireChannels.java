package org.lightning323.createkinetic.compat.drivebywire;//package org.lightning323.createkinetic.compat.drivebywire;
//
//import org.lightning323.createkinetic.content.blocks.joystick.JoystickDirection;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Locale;
//import java.util.stream.Stream;
//
//public final class JoystickWireChannels {
//   private static final String CHANNEL_PREFIX = "createkinetic.wire.channel.";
//   public static final String FORWARD;
//   public static final String RIGHT;
//   public static final String BACK;
//   public static final String LEFT;
//   public static final String BUTTON = "createkinetic.wire.channel.button";
//   public static final List<String> ALL;
//
//   private JoystickWireChannels() {
//   }
//
//   public static String channelFor(JoystickDirection direction) {
//      String var10000 = direction.name();
//      return "createkinetic.wire.channel." + var10000.toLowerCase(Locale.ROOT);
//   }
//
//   public static String nextChannel(String current, boolean forward) {
//      int index = ALL.indexOf(current);
//      return index == -1 ? (String)ALL.getFirst() : (String)ALL.get(Math.floorMod(index + (forward ? 1 : -1), ALL.size()));
//   }
//
//   static {
//      FORWARD = channelFor(JoystickDirection.FORWARD);
//      RIGHT = channelFor(JoystickDirection.RIGHT);
//      BACK = channelFor(JoystickDirection.BACK);
//      LEFT = channelFor(JoystickDirection.LEFT);
//      ALL = Stream.concat(Arrays.stream(JoystickDirection.VALUES).map(JoystickWireChannels::channelFor), Stream.of("createkinetic.wire.channel.button")).toList();
//   }
//}
