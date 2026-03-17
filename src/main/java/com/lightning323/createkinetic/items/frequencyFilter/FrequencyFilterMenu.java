package com.lightning323.createkinetic.items.frequencyFilter;

import com.lightning323.createkinetic.blocks.redstone.KFrequency;
import com.lightning323.createkinetic.registries.KineticMenus;

import com.lightning323.createkinetic.registries.KineticPackets;
import com.simibubi.create.content.logistics.filter.AbstractFilterMenu;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;

public class FrequencyFilterMenu extends AbstractFilterMenu {

    String address;

    public FrequencyFilterMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public FrequencyFilterMenu(MenuType<?> type, int id, Inventory inv, ItemStack stack) {
        super(type, id, inv, stack);
    }

    public static FrequencyFilterMenu create(int id, Inventory inv, ItemStack stack) {
        return new FrequencyFilterMenu(KineticMenus.FREQUENCY_FILTER.get(), id, inv, stack);
    }

    @Override
    protected int getPlayerInventoryXOffset() {
        return 40;
    }

	@Override
	protected int getPlayerInventoryYOffset() {
		return 101;
	}

	@Override
	protected void addFilterSlots() {}

    @Override
    protected ItemStackHandler createGhostInventory() {
        return new ItemStackHandler();
    }

    @Override
    public void clearContents() {
        address = "";
    }

    @Override
    protected void initAndReadInventory(ItemStack filterItem) {
        super.initAndReadInventory(filterItem);
        address = filterItem.getOrCreateTag()
                .getString("Address");
    }

    @Override
    protected void saveData(ItemStack filterItem) {
        super.saveData(filterItem);
        filterItem.getOrCreateTag()
                .putString("Address", address);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        return ItemStack.EMPTY;
    }
}