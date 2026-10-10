package org.lightning323.createkinetic.client.gui;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;

public class HoverTintIconButton extends IconButton {
   private final float hoverR;
   private final float hoverG;
   private final float hoverB;

   public HoverTintIconButton(int x, int y, ScreenElement icon, int hoverRgb) {
      super(x, y, icon);
      this.hoverR = (float)(hoverRgb >> 16 & 255) / 255.0F;
      this.hoverG = (float)(hoverRgb >> 8 & 255) / 255.0F;
      this.hoverB = (float)(hoverRgb & 255) / 255.0F;
   }

   protected void drawBg(GuiGraphics graphics, AllGuiTextures button) {
      if (button == AllGuiTextures.BUTTON_HOVER) {
         graphics.setColor(this.hoverR, this.hoverG, this.hoverB, 1.0F);
         super.drawBg(graphics, button);
         graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         super.drawBg(graphics, button);
      }
   }
}
