package org.lightning323.createkinetic.foundation.gui.widget;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;

public class ToggleButton extends IconButton {
   private boolean selected;
   private boolean locked;

   public ToggleButton(int x, int y, ScreenElement icon) {
      super(x, y, icon);
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public boolean isSelected() {
      return this.selected;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
   }

   public boolean isLocked() {
      return this.locked;
   }

   protected void drawBg(GuiGraphics graphics, AllGuiTextures button) {
      if (this.locked) {
         graphics.setColor(1.0F, 0.55F, 0.55F, 1.0F);
         super.drawBg(graphics, AllGuiTextures.BUTTON);
         graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         AllGuiTextures effective = button;
         if (this.selected && (button == AllGuiTextures.BUTTON || button == AllGuiTextures.BUTTON_HOVER)) {
            effective = AllGuiTextures.BUTTON_DOWN;
         }

         super.drawBg(graphics, effective);
      }
   }
}
