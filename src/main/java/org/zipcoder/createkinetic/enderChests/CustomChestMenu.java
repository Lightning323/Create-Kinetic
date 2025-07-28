package org.zipcoder.createkinetic.enderChests;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.zipcoder.createkinetic.mixinInterfaces.AbstractContainerMenu_I;

import java.util.function.Supplier;

public class CustomChestMenu extends AbstractContainerMenu {

    private final Container container;
    private int currentPage = 0;
    private final int slotsPerPage; // e.g. 3 rows visible = 27
    private final int containerRows;
    private final int containerSlots;
    public final int MAX_ROWS = 6;
    final ChestType chestType;
    final Inventory inventory;
    public int inventoryTextY;
    final int buttonSize = 18;

    public CustomChestMenu(ChestType type, int i, Inventory inventory, Supplier<MenuType<CustomChestMenu>> menuType) {
        this(type, i, inventory, new SimpleContainer(9 * (type.getExtraSlots() + 3)), type.getExtraSlots() + 3, menuType);
    }


    public void setPage(int page) {
        this.currentPage = page;
        AbstractContainerMenu_I actm = ((AbstractContainerMenu_I) this);
        actm.clearSlots();
        layoutSlots(); // Add new visible page
    }

    public int getPage() {
        return this.currentPage;
    }

    public int getTotalPages() {
        return container.getContainerSize() / slotsPerPage;
    }


    public CustomChestMenu(ChestType type, int containerId, Inventory inventory, Container container, int rows, Supplier<MenuType<CustomChestMenu>> menuType) {
        super(menuType.get(), containerId);
        this.container = container;
        if (rows > MAX_ROWS) {
            this.containerRows = MAX_ROWS;
        } else {
            this.containerRows = rows;
        }
        checkContainerSize(container, this.containerRows * 9);

        this.slotsPerPage = 9 * containerRows; // e.g. 3 rows visible = 27
        this.containerSlots = rows * 9;
        this.inventory = inventory;
        this.chestType = type;
        container.startOpen(inventory.player);
        layoutSlots();
    }

    private void layoutSlots() {
        int yStart = 16;//Title
        if (this.getTotalPages() > 1) {
            yStart += 15;
        }


        //Iterate over all slots and only add the visible ones
        int pageStart = currentPage * slotsPerPage;
        int pageEnd = Math.min(pageStart + slotsPerPage, container.getContainerSize());

        for (int index = 0; index < container.getContainerSize(); index++) {
            Slot slot;

            if (index >= pageStart && index < pageEnd) {
                int visibleIndex = index - pageStart; // index on the current page
                int col = visibleIndex % 9;
                int row = visibleIndex / 9;

                int x = 8 + col * buttonSize;
                int y = yStart + row * buttonSize;

                slot = new Slot(container, index, x, y);
            } else {
                // Off-screen if not in current page
                slot = new Slot(container, index, -9999, -9999);
            }

            this.addSlot(slot);
        }


        yStart += 14 + (containerRows) * buttonSize;//Container
        inventoryTextY = yStart - 12;

        for (int l = 0; l < 3; ++l) {
            for (int m = 0; m < 9; ++m) {
                this.addSlot(new Slot(inventory, m + l * 9 + 9,
                        8 + m * buttonSize, yStart + (l * buttonSize)));
            }
        }

        yStart += (3 * buttonSize) + 4;//Inventory 3 rows

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(inventory, l,
                    8 + l * buttonSize, yStart));
        }
    }


    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
//        Slot slot = this.slots.get(i);
//        if (slot != null && slot.hasItem()) {
//            ItemStack itemStack2 = slot.getItem();
//            itemStack = itemStack2.copy();
//
//
//            if (i < this.containerRows * 9) {
//                if (!this.moveItemStackTo(itemStack2, this.containerRows * 9, this.slots.size(), true)) {
//                    return ItemStack.EMPTY;
//                }
//            } else if (!this.moveItemStackTo(itemStack2, 0, this.containerRows * 9, false)) {
//                return ItemStack.EMPTY;
//            }
//
//
//            if (itemStack2.isEmpty()) {
//                slot.setByPlayer(ItemStack.EMPTY);
//            } else {
//                slot.setChanged();
//            }
//        }

        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public Container getContainer() {
        return this.container;
    }

    public int getRowCount() {
        return this.containerRows;
    }
}
