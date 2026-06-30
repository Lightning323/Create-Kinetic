package org.lightning323.createkinetic.content.blocks.joystick;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.client.KineticKeys;
import java.util.Objects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.level.block.entity.BlockEntity;

@OnlyIn(Dist.CLIENT)
public final class JoystickHudOverlay implements LayeredDraw.Layer {
   private static final ResourceLocation DOT_SPRITE = CreateKinetic.path("textures/gui/joystick_crosshair.png");
   private static final ResourceLocation HUD_SPRITE = CreateKinetic.path("textures/gui/joystick_hud.png");

   public void render(GuiGraphics g, DeltaTracker delta) {
      Minecraft mc = Minecraft.getInstance();
      if (JoystickControlClient.isActive() && !mc.options.hideGui) {
         if (mc.level != null) {
            BlockEntity var5 = mc.level.getBlockEntity(JoystickControlClient.activePos());
            if (var5 instanceof JoystickBlockEntity) {
               JoystickBlockEntity be = (JoystickBlockEntity)var5;
               if (!be.isShowHud()) {
                  return;
               }
            }
         }

         int screenW = g.guiWidth();
         int screenH = g.guiHeight();
         int cx = screenW / 2;
         int squareBottom = screenH - 80;
         int squareTop = squareBottom - 60;
         int squareLeft = cx - 30;
         int squareRight = cx + 30;
         int midY = squareTop + 30;
         boolean paused = KineticKeys.isFreeCameraHeld();
         int frame = paused ? 1344282656 : -2145378272;
         int fill = paused ? 671088640 : 1073741824;
         int axis = paused ? 813727872 : 1350598784;
         int dotColor = paused ? 1627389951 : -1056964609;
         g.fill(squareLeft - 1, squareTop - 1, squareRight + 1, squareBottom + 1, frame);
         g.fill(squareLeft, squareTop, squareRight, squareBottom, fill);
         g.fill(cx, squareTop, cx + 1, squareBottom, axis);
         g.fill(squareLeft, midY, squareRight, midY + 1, axis);

         g.blit(HUD_SPRITE, squareLeft, squareTop, 0, 0, 32, 32);

         int tiltX = JoystickControlClient.tiltX();
         int tiltY = JoystickControlClient.tiltY();
         float pxPerStep = 2.0F;
         int dotX = Math.round((float)cx + (float)tiltX * 2.0F) - 2;
         int dotY = Math.round((float)midY + (float)tiltY * 2.0F) - 2;


         drawDot(g, dotX, dotY, dotColor);


         Font font = mc.font;
         float readoutScale = paused ? 0.45F : 1.0F;
         JoystickDirection var10002 = JoystickDirection.FORWARD;
         Objects.requireNonNull(font);
         drawReadout(g, font, var10002, cx, squareTop - 9 - 8 + 4, tiltX, tiltY, Anchor.H_CENTER, readoutScale);
         drawReadout(g, font, JoystickDirection.BACK, cx, squareBottom + 8 - 4, tiltX, tiltY, Anchor.H_CENTER, readoutScale);
         var10002 = JoystickDirection.RIGHT;
         int var10003 = squareRight + 8;
         Objects.requireNonNull(font);
         drawReadout(g, font, var10002, var10003, midY - 9 / 2, tiltX, tiltY, Anchor.H_LEFT, readoutScale);
         var10002 = JoystickDirection.LEFT;
         var10003 = squareLeft - 8;
         Objects.requireNonNull(font);
         drawReadout(g, font, var10002, var10003, midY - 9 / 2, tiltX, tiltY, Anchor.H_RIGHT, readoutScale);
      }
   }

   private void drawDot(GuiGraphics g, int dotX, int dotY, int dotColor) {
//      g.fill(dotX, dotY, dotX + 5, dotY + 5, dotColor);

      // 1. Texture location
      // 2. x (screen position)
      // 3. y (screen position)
      // 4. u (texture source x coordinate)
      // 5. v (texture source y coordinate)
      // 6. width (on screen)
      // 7. height (on screen)
      g.blit(DOT_SPRITE, dotX, dotY, 0, 0, 16, 16);
   }

   private static void drawReadout(GuiGraphics g, Font font, JoystickDirection dir, int anchorX, int y, int tiltX, int tiltY, Anchor anchor, float brightnessScale) {
      int strength = dir.strengthFor(tiltX, tiltY);
      String text = Integer.toString(strength);
      int w = font.width(text);
      int var10000;
      switch (anchor.ordinal()) {
         case 0 -> var10000 = anchorX - w / 2;
         case 1 -> var10000 = anchorX;
         case 2 -> var10000 = anchorX - w;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      int x = var10000;
      int rgb = dir.colorRgb;
      int r = (int)Math.min(255.0F, (float)(rgb >> 16 & 255) * brightnessScale);
      int gg = (int)Math.min(255.0F, (float)(rgb >> 8 & 255) * brightnessScale);
      int b = (int)Math.min(255.0F, (float)(rgb & 255) * brightnessScale);
      g.drawString(font, text, x, y, -16777216 | r << 16 | gg << 8 | b, true);
   }

   private static enum Anchor {
      H_CENTER,
      H_LEFT,
      H_RIGHT;

      // $FF: synthetic method
      private static Anchor[] $values() {
         return new Anchor[]{H_CENTER, H_LEFT, H_RIGHT};
      }
   }
}
