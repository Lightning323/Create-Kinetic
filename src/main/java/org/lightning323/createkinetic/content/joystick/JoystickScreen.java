package org.lightning323.createkinetic.content.joystick;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import com.mojang.blaze3d.vertex.PoseStack;
import org.lightning323.createkinetic.KineticPartialModels;
import org.lightning323.createkinetic.foundation.gui.widget.HoverTintIconButton;
import org.lightning323.createkinetic.foundation.gui.widget.ToggleButton;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;

public class JoystickScreen extends AbstractSimiContainerScreen<JoystickMenu> {
   private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath("createkinetic", "textures/gui/joystick.png");
   private static final ScreenElement KEYBOARD_ICON = iconBlit("keyboard");
   private static final ScreenElement MOUSE_ICON = iconBlit("mouse");
   private static final int TITLE_COLOR = 5841956;
   private static final int PREVIEW_AREA_WIDTH = 55;
   private static final int PREVIEW_AREA_HEIGHT = 58;
   private static final int HEADER_Y = 19;
   private static final int HEADER_WIDTH = 16;
   private static final int HEADER_HEIGHT = 11;
   private static final int HEADER_TINT_RESTING = 419430399;
   private static final int HEADER_TINT_HOVER = 1090519039;
   private static final int HEADER_TINT_CAPTURE = -2130706560;
   private ToggleButton hudButton;
   private ToggleButton mouseInputToggle;
   private ToggleButton springBackToggle;
   private IconButton clearButton;
   private IconButton confirmButton;
   private List<Rect2i> extraAreas = Collections.emptyList();
   private final int[] headerSlotX;
   private int capturingSlot;

   private static ScreenElement iconBlit(String name) {
      ResourceLocation tex = ResourceLocation.fromNamespaceAndPath("createkinetic", "textures/gui/icons/" + name + ".png");
      return (graphics, x, y) -> graphics.blit(tex, x, y, 0.0F, 0.0F, 16, 16, 16, 16);
   }

   public JoystickScreen(JoystickMenu menu, Inventory inv, Component title) {
      super(menu, inv, title);
      this.headerSlotX = new int[JoystickBlockEntity.BIND_COUNT];
      this.capturingSlot = -1;
   }

   protected void init() {
      this.setWindowSize(176, 212);
      super.init();
      this.titleLabelX = 14;
      this.titleLabelY = 4;
      this.inventoryLabelX = 8;
      this.inventoryLabelY = 110;
      int frameLeft = this.leftPos + 10;
      int frameTop = this.topPos;
      this.hudButton = new ToggleButton(frameLeft + 8, frameTop + 76, AllIcons.I_VIEW_SCHEDULE);
      boolean initiallyShowing = ((JoystickMenu)this.menu).contentHolder != null && ((JoystickBlockEntity)((JoystickMenu)this.menu).contentHolder).isShowHud();
      this.hudButton.setSelected(initiallyShowing);
      this.applyHudTooltip();
      this.hudButton.withCallback(() -> {
         if (((JoystickMenu)this.menu).contentHolder != null) {
            boolean next = !this.hudButton.isSelected();
            this.hudButton.setSelected(next);
            this.applyHudTooltip();
            PacketDistributor.sendToServer(new C2SJoystickShowHud(((JoystickBlockEntity)((JoystickMenu)this.menu).contentHolder).getBlockPos(), next), new CustomPacketPayload[0]);
         }
      });
      this.addRenderableWidget(this.hudButton);
      this.mouseInputToggle = new ToggleButton(frameLeft + 29, frameTop + 76, MOUSE_ICON);
      boolean initialMouse = ((JoystickMenu)this.menu).contentHolder != null && ((JoystickBlockEntity)((JoystickMenu)this.menu).contentHolder).isUseMouseInput();
      this.mouseInputToggle.setSelected(initialMouse);
      this.applyMouseToggleTooltip();
      this.mouseInputToggle.withCallback(() -> {
         if (((JoystickMenu)this.menu).contentHolder != null) {
            boolean next = !this.mouseInputToggle.isSelected();
            this.mouseInputToggle.setSelected(next);
            this.applyMouseToggleTooltip();
            PacketDistributor.sendToServer(new C2SJoystickUseMouseInput(((JoystickBlockEntity)((JoystickMenu)this.menu).contentHolder).getBlockPos(), next), new CustomPacketPayload[0]);
         }
      });
      this.addRenderableWidget(this.mouseInputToggle);
      this.springBackToggle = new ToggleButton(frameLeft + 50, frameTop + 76, AllIcons.I_REFRESH);
      boolean initialSpring = ((JoystickMenu)this.menu).contentHolder != null && ((JoystickBlockEntity)((JoystickMenu)this.menu).contentHolder).isSpringBack();
      this.springBackToggle.setSelected(initialSpring);
      this.refreshSpringBackState();
      this.springBackToggle.withCallback(() -> {
         if (((JoystickMenu)this.menu).contentHolder != null) {
            if (!this.springBackToggle.isLocked()) {
               boolean next = !this.springBackToggle.isSelected();
               this.springBackToggle.setSelected(next);
               this.refreshSpringBackState();
               PacketDistributor.sendToServer(new C2SJoystickSpringBack(((JoystickBlockEntity)((JoystickMenu)this.menu).contentHolder).getBlockPos(), next), new CustomPacketPayload[0]);
            }
         }
      });
      this.addRenderableWidget(this.springBackToggle);

      for(JoystickDirection dir : JoystickDirection.VALUES) {
         this.headerSlotX[dir.index] = dir.slotFrameX;
      }

      this.headerSlotX[JoystickBlockEntity.BIND_BUTTON_INDEX] = 124;
      this.clearButton = new HoverTintIconButton(frameLeft + 96, frameTop + 76, AllIcons.I_TRASH, 16744576);
      this.clearButton.withCallback(() -> {
         if (((JoystickMenu)this.menu).contentHolder != null) {
            PacketDistributor.sendToServer(new C2SJoystickClearFrequencies(((JoystickBlockEntity)((JoystickMenu)this.menu).contentHolder).getBlockPos()), new CustomPacketPayload[0]);
         }
      });
      this.clearButton.setToolTip(Component.translatable("gui.createkinetic.joystick.clear_frequencies"));
      this.addRenderableWidget(this.clearButton);
      this.confirmButton = new HoverTintIconButton(frameLeft + 123, frameTop + 76, AllIcons.I_CONFIRM, 8454016);
      this.confirmButton.withCallback(() -> {
         if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.closeContainer();
         }

      });
      this.confirmButton.setToolTip(Component.translatable("gui.createkinetic.joystick.save"));
      this.addRenderableWidget(this.confirmButton);
      int previewLeft = frameLeft + 156;
      int previewTop = frameTop + 100 - 58;
      this.extraAreas = ImmutableList.of(new Rect2i(previewLeft, previewTop, 55, 58));
   }

   public List<Rect2i> getExtraAreas() {
      return this.extraAreas;
   }

   protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
      this.refreshSpringBackState();
      int frameLeft = this.leftPos + 10;
      int frameTop = this.topPos;
      g.blit(BACKGROUND, frameLeft, frameTop, 0.0F, 0.0F, 156, 100, 256, 256);
      int invX = this.leftPos + this.getLeftOfCentered(AllGuiTextures.PLAYER_INVENTORY.getWidth()) - this.leftPos;
      this.renderPlayerInventory(g, invX, this.topPos + 100 + 4);
      this.renderBlockPreview(g, frameLeft, frameTop);
      this.renderHeaderOverlays(g, frameLeft, frameTop, mouseX, mouseY);
   }

   private void renderHeaderOverlays(GuiGraphics g, int frameLeft, int frameTop, int mouseX, int mouseY) {
      for(int slot = 0; slot < this.headerSlotX.length; ++slot) {
         if (this.isHeaderVisible(slot)) {
            int x = frameLeft + this.headerSlotX[slot];
            int y = frameTop + 19;
            boolean capturing = this.capturingSlot == slot;
            boolean hovered = mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 11;
            int tint = capturing ? -2130706560 : (hovered ? 1090519039 : 419430399);
            g.fill(x, y, x + 16, y + 11, tint);
         }
      }

   }

   private boolean isHeaderVisible(int slot) {
      if (slot == JoystickBlockEntity.BIND_BUTTON_INDEX) {
         return true;
      } else {
         return ((JoystickMenu)this.menu).contentHolder != null && !((JoystickBlockEntity)((JoystickMenu)this.menu).contentHolder).isUseMouseInput();
      }
   }

   private void renderBlockPreview(GuiGraphics g, int frameLeft, int frameTop) {
      if (((JoystickMenu)this.menu).contentHolder != null) {
         PoseStack ms = g.pose();
         ms.pushPose();
         ((PoseTransformStack)((PoseTransformStack)TransformStack.of(ms).pushPose().translate((float)(frameLeft + 156 + 39), (float)(frameTop + 100 + 4), 100.0F).scale(40.0F)).rotateXDegrees(-22.0F)).rotateYDegrees(153.0F);
         GuiGameElement.of(KineticPartialModels.JOYSTICK_PREVIEW).render(g);
         ms.popPose();
      }
   }

   protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
      g.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 5841956, false);
   }

   protected void renderTooltip(GuiGraphics g, int mouseX, int mouseY) {
      int headerSlot = this.headerSlotAt(mouseX, mouseY);
      if (headerSlot >= 0) {
         this.renderHeaderTooltip(g, mouseX, mouseY, headerSlot);
      } else {
         if (((JoystickMenu)this.menu).getCarried().isEmpty() && this.hoveredSlot != null) {
            Slot var6 = this.hoveredSlot;
            if (var6 instanceof SlotItemHandler) {
               SlotItemHandler ghost = (SlotItemHandler)var6;
               if (ghost.container != ((JoystickMenu)this.menu).playerInventory) {
                  int slotIdx = ghost.getSlotIndex();
                  List<Component> lines = new LinkedList();
                  if (this.hoveredSlot.hasItem()) {
                     lines.addAll(this.getTooltipFromContainerItem(this.hoveredSlot.getItem()));
                  }

                  appendFrequencyContext(lines, slotIdx);
                  if (lines.isEmpty()) {
                     return;
                  }

                  g.renderComponentTooltip(this.font, lines, mouseX, mouseY);
                  return;
               }
            }
         }

         super.renderTooltip(g, mouseX, mouseY);
      }
   }

   private void renderHeaderTooltip(GuiGraphics g, int mouseX, int mouseY, int slot) {
      List<Component> lines = new LinkedList();
      if (this.capturingSlot == slot) {
         lines.add(Component.translatable("gui.createkinetic.joystick.bind_capture_prompt").withStyle(ChatFormatting.YELLOW));
      } else {
         String bind = ((JoystickMenu)this.menu).contentHolder == null ? "" : ((JoystickBlockEntity)((JoystickMenu)this.menu).contentHolder).getBinding(slot);
         if (bind.isEmpty()) {
            lines.add(Component.translatable("gui.createkinetic.joystick.bind_unbound").withStyle(ChatFormatting.GRAY));
         } else {
            Component keyName = displayNameFor(bind);
            lines.add(Component.translatable("gui.createkinetic.joystick.bind_bound", new Object[]{keyName}).withStyle(ChatFormatting.GOLD));
         }
      }

      g.renderComponentTooltip(this.font, lines, mouseX, mouseY);
   }

   private static Component displayNameFor(String name) {
      try {
         return InputConstants.getKey(name).getDisplayName();
      } catch (IllegalArgumentException var2) {
         return Component.literal(name);
      }
   }

   private int headerSlotAt(int mouseX, int mouseY) {
      int frameLeft = this.leftPos + 10;
      int frameTop = this.topPos;

      for(int slot = 0; slot < this.headerSlotX.length; ++slot) {
         if (this.isHeaderVisible(slot)) {
            int x = frameLeft + this.headerSlotX[slot];
            int y = frameTop + 19;
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 11) {
               return slot;
            }
         }
      }

      return -1;
   }

   private static void appendFrequencyContext(List<Component> lines, int slotIdx) {
      if (slotIdx >= 0 && slotIdx < JoystickBlockEntity.FREQUENCY_SLOTS) {
         Component label;
         if (slotIdx >= JoystickBlockEntity.BUTTON_SLOT_1) {
            label = Component.translatable("gui.createkinetic.joystick.button");
         } else {
            JoystickDirection dir = JoystickDirection.VALUES[slotIdx / 2];
            label = Component.translatable("gui.createkinetic.joystick.dir_name." + dir.name().toLowerCase()).withStyle(dir.color);
         }

         int half = slotIdx % 2 + 1;
         lines.add(Component.translatable("gui.createkinetic.joystick.frequency_slot_" + half, new Object[]{label}).withStyle(ChatFormatting.GOLD));
      }
   }

   private void applyHudTooltip() {
      String key = this.hudButton.isSelected() ? "gui.createkinetic.joystick.hide_hud" : "gui.createkinetic.joystick.show_hud";
      this.hudButton.setToolTip(Component.translatable(key));
   }

   private void applyMouseToggleTooltip() {
      boolean mouseMode = this.mouseInputToggle.isSelected();
      this.mouseInputToggle.setIcon(mouseMode ? MOUSE_ICON : KEYBOARD_ICON);
      String key = mouseMode ? "gui.createkinetic.joystick.use_mouse_on" : "gui.createkinetic.joystick.use_mouse_off";
      this.mouseInputToggle.setToolTip(Component.translatable(key));
   }

   private void refreshSpringBackState() {
      if (((JoystickMenu)this.menu).contentHolder != null) {
         boolean powered = ((JoystickBlockEntity)((JoystickMenu)this.menu).contentHolder).isPowered();
         boolean mouseMode = ((JoystickBlockEntity)((JoystickMenu)this.menu).contentHolder).isUseMouseInput();
         this.springBackToggle.visible = !mouseMode;
         this.springBackToggle.setLocked(powered);
         String key;
         if (powered) {
            key = "gui.createkinetic.joystick.spring_back_powered_locked";
         } else {
            key = this.springBackToggle.isSelected() ? "gui.createkinetic.joystick.spring_back_on" : "gui.createkinetic.joystick.spring_back_off";
         }

         this.springBackToggle.setToolTip(Component.translatable(key));
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.capturingSlot >= 0) {
         this.commitBind(Type.MOUSE.getOrCreate(button));
         return true;
      } else {
         int slot = this.headerSlotAt((int)mouseX, (int)mouseY);
         if (slot >= 0) {
            if (button == 0) {
               this.capturingSlot = slot;
               return true;
            }

            if (button == 1) {
               this.sendBind(slot, "");
               return true;
            }
         }

         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.capturingSlot >= 0) {
         if (keyCode == 256) {
            this.capturingSlot = -1;
            return true;
         } else {
            this.commitBind(InputConstants.getKey(keyCode, scanCode));
            return true;
         }
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   private void commitBind(InputConstants.Key key) {
      if (this.capturingSlot >= 0) {
         this.sendBind(this.capturingSlot, key.getName());
         this.capturingSlot = -1;
      }
   }

   private void sendBind(int slot, String keyName) {
      if (((JoystickMenu)this.menu).contentHolder != null) {
         PacketDistributor.sendToServer(new C2SJoystickSetBind(((JoystickBlockEntity)((JoystickMenu)this.menu).contentHolder).getBlockPos(), slot, keyName), new CustomPacketPayload[0]);
      }
   }

   public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
      this.renderBackground(g, mouseX, mouseY, partialTick);
      super.render(g, mouseX, mouseY, partialTick);
      this.renderTooltip(g, mouseX, mouseY);
   }
}
