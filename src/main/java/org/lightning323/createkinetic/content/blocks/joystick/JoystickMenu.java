package org.lightning323.createkinetic.content.blocks.joystick;

import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class JoystickMenu extends GhostItemMenu<JoystickBlockEntity> {


   public JoystickMenu(MenuType<?> type, int id, Inventory inv, RegistryFriendlyByteBuf buf) {
      super(type, id, inv, buf);
   }

   public JoystickMenu(MenuType<?> type, int id, Inventory inv, JoystickBlockEntity be) {
      super(type, id, inv, be);
   }

   protected JoystickBlockEntity createOnClient(RegistryFriendlyByteBuf buf) {
      BlockPos pos = buf.readBlockPos();
      ClientLevel level = Minecraft.getInstance().level;
      if (level == null) {
         return null;
      } else {
         BlockEntity be = level.getBlockEntity(pos);
         JoystickBlockEntity var10000;
         if (be instanceof JoystickBlockEntity) {
            JoystickBlockEntity joystick = (JoystickBlockEntity)be;
            var10000 = joystick;
         } else {
            var10000 = null;
         }

         return var10000;
      }
   }

   protected ItemStackHandler createGhostInventory() {
      return ((JoystickBlockEntity)this.contentHolder).getFrequencyItems();
   }

   protected boolean allowRepeats() {
      return true;
   }

   protected void addSlots() {
      this.addPlayerSlots(8, 122);

      for(JoystickDirection dir : JoystickDirection.VALUES) {
         int x = 10 + dir.slotFrameX;
         this.addSlot(new SlotItemHandler(this.ghostInventory, dir.firstSlot(), x, 32));
         this.addSlot(new SlotItemHandler(this.ghostInventory, dir.secondSlot(), x, 50));
      }

      int buttonX = 134;
      this.addSlot(new SlotItemHandler(this.ghostInventory, JoystickBlockEntity.BUTTON_SLOT_1, 134, 32));
      this.addSlot(new SlotItemHandler(this.ghostInventory, JoystickBlockEntity.BUTTON_SLOT_2, 134, 50));
   }

   protected void saveData(JoystickBlockEntity be) {
   }
}
