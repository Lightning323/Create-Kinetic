package org.lightning323.createkinetic.content.blocks.joystick;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lightning323.createkinetic.CreateKinetic;

import java.util.Objects;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lightning323.createkinetic.config.KineticConfig;

@OnlyIn(Dist.CLIENT)
public final class JoystickHudOverlay implements LayeredDraw.Layer {
    private static final ResourceLocation CROSSHAIR_SPRITE = CreateKinetic.path("textures/gui/joystick_crosshair.png");
    private static final int CROSSHAIR_SIZE = 15;

    private static final ResourceLocation HUD_SPRITE = CreateKinetic.path("textures/gui/joystick_hud.png");
    private static final ResourceLocation HUD_MINIMAL_SPRITE = CreateKinetic.path("textures/gui/joystick_hud_minimal.png");
    private static final int HUD_SIZE = 65;


    private static void drawPixelLine(GuiGraphics g,
                                      int x0, int y0,
                                      int x1, int y1,
                                      int color) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            g.fill(x0, y0, x0 + 1, y0 + 1, color);

            if (x0 == x1 && y0 == y1)
                break;

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    public void render(GuiGraphics g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (JoystickControlClient.isActive() && !mc.options.hideGui) {
            if (mc.level != null) {
                BlockEntity var5 = mc.level.getBlockEntity(JoystickControlClient.activePos());
                if (var5 instanceof JoystickBlockEntity) {
                    JoystickBlockEntity be = (JoystickBlockEntity) var5;
                    if (!be.isShowHud()) {
                        return;
                    }
                }
            }

            int screenW = g.guiWidth();
            int screenH = g.guiHeight();
            int cx = screenW / 2;
            int cy = screenH / 2;
            int squareHeight = HUD_SIZE;
            int squareBottom = cy + squareHeight / 2;
            int squareTop = squareBottom - squareHeight;
            int squareLeft = cx - squareHeight / 2;
            int squareRight = cx + squareHeight / 2;
            int midY = squareTop + squareHeight / 2;



            int tiltX = JoystickControlClient.tiltX();
            int tiltY = JoystickControlClient.tiltY();

            // Draw crosshair
            int dotX = Math.round((float) cx + (float) tiltX * 2.0F) - CROSSHAIR_SIZE / 2;
            int dotY = Math.round((float) midY + (float) tiltY * 2.0F) - CROSSHAIR_SIZE / 2;

            if(KineticConfig.joystickShowHudLine()) {
                drawPixelLine(g, cx, cy - 1, dotX + 7, dotY + 7, 0xFFFFFFFF);
            }

            if (KineticConfig.joystickShowHudBox()) {
//                int fill = KineticKeys.isFreeCameraHeld() ? 671088640 : 1073741824;
//                g.fill(squareLeft, squareTop, squareRight, squareBottom, fill);
                g.blit(HUD_SPRITE, squareLeft, squareTop, 0, 0, HUD_SIZE, HUD_SIZE, HUD_SIZE, HUD_SIZE);
            } else {
                g.blit(HUD_MINIMAL_SPRITE, squareLeft, squareTop, 0, 0, HUD_SIZE, HUD_SIZE, HUD_SIZE, HUD_SIZE);
            }

            drawDot(g, dotX, dotY, CROSSHAIR_SIZE);


            if (KineticConfig.joystickShowReadout()) {
                Font font = mc.font;
                float readoutScale = 1.0F;
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
    }

    private void drawDot(GuiGraphics g, int dotX, int dotY, int crosshairHeight) {
//      g.fill(dotX, dotY, dotX + 5, dotY + 5, dotColor);

        // 1. Texture location
        // 2. x (screen position)
        // 3. y (screen position)
        // 4. u (texture source x coordinate)
        // 5. v (texture source y coordinate)
        // 6. width (on screen)
        // 7. height (on screen)
        g.blit(CROSSHAIR_SPRITE, dotX, dotY, 0, 0, crosshairHeight, crosshairHeight, crosshairHeight, crosshairHeight);
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
            default -> throw new MatchException((String) null, (Throwable) null);
        }

        int x = var10000;
        int rgb = dir.colorRgb;
        int r = (int) Math.min(255.0F, (float) (rgb >> 16 & 255) * brightnessScale);
        int gg = (int) Math.min(255.0F, (float) (rgb >> 8 & 255) * brightnessScale);
        int b = (int) Math.min(255.0F, (float) (rgb & 255) * brightnessScale);
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
