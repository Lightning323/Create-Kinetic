package org.lightning323.createkinetic.content.blocks.joystick;

import net.minecraft.ChatFormatting;

public enum JoystickDirection {
   FORWARD(0, ChatFormatting.GREEN, 8),
   RIGHT(1, ChatFormatting.LIGHT_PURPLE, 95),
   BACK(2, ChatFormatting.BLUE, 37),
   LEFT(3, ChatFormatting.RED, 66);

   public static final JoystickDirection[] VALUES = values();
   public final int index;
   public final ChatFormatting color;
   public final int colorRgb;
   public final int slotFrameX;

   private JoystickDirection(final int index, final ChatFormatting color, final int slotFrameX) {
      this.index = index;
      this.color = color;
      Integer rgb = color.getColor();
      this.colorRgb = rgb == null ? 16777215 : rgb;
      this.slotFrameX = slotFrameX;
   }

   public int firstSlot() {
      return this.index * 2;
   }

   public int secondSlot() {
      return this.index * 2 + 1;
   }

   public int strengthFor(int tiltX, int tiltY) {
      int var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = Math.max(0, -tiltY);
         case 1 -> var10000 = Math.max(0, tiltX);
         case 2 -> var10000 = Math.max(0, tiltY);
         case 3 -> var10000 = Math.max(0, -tiltX);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static JoystickDirection[] $values() {
      return new JoystickDirection[]{FORWARD, RIGHT, BACK, LEFT};
   }
}
