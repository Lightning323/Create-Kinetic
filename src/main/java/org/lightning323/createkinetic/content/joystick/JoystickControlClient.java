package org.lightning323.createkinetic.content.joystick;

import com.mojang.blaze3d.platform.InputConstants;
import org.lightning323.createkinetic.KineticKeys;
import org.lightning323.createkinetic.Config;
import com.simibubi.create.foundation.utility.ControlsUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public final class JoystickControlClient {
   private static @Nullable BlockPos activePos;
   private static byte tiltX;
   private static byte tiltY;
   private static double accumX;
   private static double accumY;
   private static final boolean[] dirHeld;
   private static final long[] dirLastStepMs;
   private static long lastSpringStepXMs;
   private static long lastSpringStepYMs;
   private static long lastBoundActionMs;

   private static long stepIntervalMs() {
      return (long)Config.joystickKeyRepeatDelayMs();
   }

   private static long springBackDelayMs() {
      return (long)Config.joystickSpringBackDelayMs();
   }

   private JoystickControlClient() {
   }

   public static boolean isActive() {
      return activePos != null;
   }

   public static boolean isMouseCaptured() {
      if (activePos == null) {
         return false;
      } else {
         JoystickBlockEntity be = activeBE();
         return be == null || be.isUseMouseInput();
      }
   }

   public static @Nullable BlockPos activePos() {
      return activePos;
   }

   public static int tiltX() {
      return tiltX;
   }

   public static int tiltY() {
      return tiltY;
   }

   public static void enter(BlockPos pos) {
      activePos = pos;
      Minecraft mc = Minecraft.getInstance();
      byte seedX = 0;
      byte seedY = 0;
      boolean mouseMode = true;
      if (mc.level != null) {
         BlockEntity var6 = mc.level.getBlockEntity(pos);
         if (var6 instanceof JoystickBlockEntity) {
            JoystickBlockEntity be = (JoystickBlockEntity)var6;
            seedX = be.getTiltX();
            seedY = be.getTiltY();
            mouseMode = be.isUseMouseInput();
         }
      }

      tiltX = seedX;
      tiltY = seedY;
      accumX = (double)0.0F;
      accumY = (double)0.0F;
      LocalPlayer player = mc.player;
      if (player != null) {
         Component modeLabel = Component.translatable(mouseMode ? "message.createkinetic.joystick.mode_mouse" : "message.createkinetic.joystick.mode_keys");
         if (mouseMode) {
            Component keyLabel = Component.literal("[").append(KineticKeys.FREE_CAMERA.getTranslatedKeyMessage()).append("]");
            player.displayClientMessage(Component.translatable("message.createkinetic.joystick.entered_mouse", new Object[]{modeLabel, keyLabel}), true);
         } else {
            player.displayClientMessage(Component.translatable("message.createkinetic.joystick.entered_keys", new Object[]{modeLabel}), true);
         }
      }

   }

   public static void exit(String reasonKey) {
      boolean wasActive = activePos != null;
      activePos = null;
      tiltX = 0;
      tiltY = 0;
      accumX = (double)0.0F;
      accumY = (double)0.0F;

      for(int i = 0; i < dirHeld.length; ++i) {
         dirHeld[i] = false;
      }

      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         if (!reasonKey.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.createkinetic.joystick." + reasonKey), true);
         } else if (wasActive) {
            player.displayClientMessage(Component.translatable("message.createkinetic.joystick.exited"), true);
         }

      }
   }

   public static void requestExit() {
      if (activePos != null) {
         PacketDistributor.sendToServer(new C2SExitJoystickControl(activePos), new CustomPacketPayload[0]);
      }
   }

   public static void setButtonPressed(boolean pressed) {
      if (activePos != null) {
         PacketDistributor.sendToServer(new C2SJoystickButton(activePos, pressed), new CustomPacketPayload[0]);
      }
   }

   public static void feedMouseDelta(double dx, double dy) {
      if (activePos != null) {
         JoystickBlockEntity be = activeBE();
         if (be == null || be.isUseMouseInput()) {
            accumX += dx;
            accumY += dy;
            double threshold = Config.joystickPixelsPerStep();
            if (!(threshold <= (double)0.0F)) {
               boolean changed;
               for(changed = false; accumX >= threshold && tiltX < 15; changed = true) {
                  accumX -= threshold;
                  ++tiltX;
               }

               while(accumX <= -threshold && tiltX > -15) {
                  accumX += threshold;
                  --tiltX;
                  changed = true;
               }

               while(accumY >= threshold && tiltY < 15) {
                  accumY -= threshold;
                  ++tiltY;
                  changed = true;
               }

               while(accumY <= -threshold && tiltY > -15) {
                  accumY += threshold;
                  --tiltY;
                  changed = true;
               }

               if (tiltX == 15 && accumX > (double)0.0F) {
                  accumX = (double)0.0F;
               }

               if (tiltX == -15 && accumX < (double)0.0F) {
                  accumX = (double)0.0F;
               }

               if (tiltY == 15 && accumY > (double)0.0F) {
                  accumY = (double)0.0F;
               }

               if (tiltY == -15 && accumY < (double)0.0F) {
                  accumY = (double)0.0F;
               }

               if (changed) {
                  PacketDistributor.sendToServer(new C2SJoystickTilt(activePos, tiltX, tiltY), new CustomPacketPayload[0]);
               }

            }
         }
      }
   }

   @SubscribeEvent
   public static void onMovementInput(MovementInputUpdateEvent event) {
      if (activePos != null) {
         Input input = event.getInput();
         input.forwardImpulse = 0.0F;
         input.leftImpulse = 0.0F;
         input.up = false;
         input.down = false;
         input.left = false;
         input.right = false;
         input.jumping = false;
         input.shiftKeyDown = false;
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onClientTick(ClientTickEvent.Post event) {
      if (activePos != null) {
         for(KeyMapping mapping : ControlsUtil.getControls()) {
            drain(mapping);
         }

         Options o = Minecraft.getInstance().options;
         drain(o.keyAttack);
         drain(o.keyUse);
         drain(o.keyPickItem);
         drain(o.keyDrop);
         drain(o.keySwapOffhand);

         for(KeyMapping hotbar : o.keyHotbarSlots) {
            drain(hotbar);
         }

         tickBindLoop();
      }
   }

   private static void tickBindLoop() {
      if (!KineticKeys.isFreeCameraHeld()) {
         long now = System.currentTimeMillis();

         for(int i = 0; i < dirHeld.length; ++i) {
            if (dirHeld[i] && now - dirLastStepMs[i] >= stepIntervalMs()) {
               applyDirectionStep(i);
               dirLastStepMs[i] = now;
            }
         }

         JoystickBlockEntity be = activeBE();
         if (be != null && be.isSpringBack() && !be.isUseMouseInput() && !be.isPowered()) {
            if (now - lastBoundActionMs >= springBackDelayMs()) {
               boolean axisXBound = !be.getBinding(JoystickDirection.LEFT.index).isEmpty() || !be.getBinding(JoystickDirection.RIGHT.index).isEmpty();
               boolean axisYBound = !be.getBinding(JoystickDirection.FORWARD.index).isEmpty() || !be.getBinding(JoystickDirection.BACK.index).isEmpty();
               boolean keyHeldX = dirHeld[JoystickDirection.LEFT.index] || dirHeld[JoystickDirection.RIGHT.index];
               boolean keyHeldY = dirHeld[JoystickDirection.FORWARD.index] || dirHeld[JoystickDirection.BACK.index];
               boolean changed = false;
               if (axisXBound && !keyHeldX && tiltX != 0 && now - lastSpringStepXMs >= stepIntervalMs()) {
                  tiltX = (byte)(tiltX + (tiltX > 0 ? -1 : 1));
                  accumX = (double)0.0F;
                  lastSpringStepXMs = now;
                  changed = true;
               }

               if (axisYBound && !keyHeldY && tiltY != 0 && now - lastSpringStepYMs >= stepIntervalMs()) {
                  tiltY = (byte)(tiltY + (tiltY > 0 ? -1 : 1));
                  accumY = (double)0.0F;
                  lastSpringStepYMs = now;
                  changed = true;
               }

               if (changed) {
                  PacketDistributor.sendToServer(new C2SJoystickTilt(activePos, tiltX, tiltY), new CustomPacketPayload[0]);
               }

            }
         }
      }
   }

   private static void drain(KeyMapping mapping) {
      while(mapping.consumeClick()) {
      }

      mapping.setDown(false);
   }

   public static void exitIfControlling(BlockPos pos) {
      if (pos.equals(activePos)) {
         exit("");
      }

   }

   @SubscribeEvent
   public static void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
      if (activePos != null) {
         exit("");
      }

   }

   @SubscribeEvent
   public static void onClientTickPre(ClientTickEvent.Pre event) {
      JoystickClientRaycast.clearNearby();
   }

   @SubscribeEvent
   public static void onCrosshairRender(RenderGuiLayerEvent.Pre event) {
      if (VanillaGuiLayers.CROSSHAIR.equals(event.getName()) && isActive()) {
         event.setCanceled(true);
      }

   }

   public static int findBoundSlot(InputConstants.Key key) {
      JoystickBlockEntity be = activeBE();
      if (be == null) {
         return -1;
      } else {
         String name = key.getName();

         for(int i = 0; i < JoystickBlockEntity.BIND_COUNT; ++i) {
            String bind = be.getBinding(i);
            if (!bind.isEmpty() && bind.equals(name)) {
               return i;
            }
         }

         return -1;
      }
   }

   public static void onBoundKey(int slot, boolean pressed) {
      if (activePos != null) {
         if (!KineticKeys.isFreeCameraHeld()) {
            if (slot == JoystickBlockEntity.BIND_BUTTON_INDEX) {
               setButtonPressed(pressed);
            } else if (slot >= 0 && slot < dirHeld.length) {
               JoystickBlockEntity be = activeBE();
               if (be == null || !be.isUseMouseInput()) {
                  long now = System.currentTimeMillis();
                  lastBoundActionMs = now;
                  if (pressed) {
                     applyDirectionStep(slot);
                     dirHeld[slot] = true;
                     dirLastStepMs[slot] = now;
                  } else {
                     dirHeld[slot] = false;
                  }

               }
            }
         }
      }
   }

   private static void applyDirectionStep(int slot) {
      if (activePos != null) {
         JoystickDirection dir = JoystickDirection.VALUES[slot];
         boolean changed = false;
         switch (dir) {
            case FORWARD:
               if (tiltY > -15) {
                  --tiltY;
                  accumY = (double)0.0F;
                  changed = true;
               }
               break;
            case BACK:
               if (tiltY < 15) {
                  ++tiltY;
                  accumY = (double)0.0F;
                  changed = true;
               }
               break;
            case LEFT:
               if (tiltX > -15) {
                  --tiltX;
                  accumX = (double)0.0F;
                  changed = true;
               }
               break;
            case RIGHT:
               if (tiltX < 15) {
                  ++tiltX;
                  accumX = (double)0.0F;
                  changed = true;
               }
         }

         if (changed) {
            PacketDistributor.sendToServer(new C2SJoystickTilt(activePos, tiltX, tiltY), new CustomPacketPayload[0]);
         }

      }
   }

   private static @Nullable JoystickBlockEntity activeBE() {
      if (activePos == null) {
         return null;
      } else {
         ClientLevel level = Minecraft.getInstance().level;
         if (level == null) {
            return null;
         } else {
            BlockEntity var2 = level.getBlockEntity(activePos);
            JoystickBlockEntity var10000;
            if (var2 instanceof JoystickBlockEntity) {
               JoystickBlockEntity be = (JoystickBlockEntity)var2;
               var10000 = be;
            } else {
               var10000 = null;
            }

            return var10000;
         }
      }
   }

   static {
      dirHeld = new boolean[JoystickDirection.VALUES.length];
      dirLastStepMs = new long[JoystickDirection.VALUES.length];
   }
}
