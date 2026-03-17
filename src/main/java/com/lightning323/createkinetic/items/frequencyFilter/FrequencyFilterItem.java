package com.lightning323.createkinetic.items.frequencyFilter;

import java.util.Collections;
import java.util.List;

import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.filter.PackageFilterMenu;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;

import com.simibubi.create.foundation.recipe.ItemCopyingRecipe.SupportsItemCopying;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class FrequencyFilterItem extends FilterItem implements MenuProvider, SupportsItemCopying {

    public FrequencyFilterItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return FrequencyFilterMenu.create(id, inv, player.getMainHandItem());
    }


    @Override
    public List<Component> makeSummary(ItemStack filter) {
        if (!filter.hasTag()) return Collections.emptyList();

        String address = filter.getOrCreateTag()
                .getString("Address");
        if (address.isBlank()) return Collections.emptyList();

        return List.of(CreateLang.text("-> ")
                .style(ChatFormatting.GRAY)
                .add(CreateLang.text(address)
                        .style(ChatFormatting.GOLD))
                .component());
    }


    @Override
    public FilterItemStack makeStackWrapper(ItemStack filter) {
        return new FilterItemStack.PackageFilterItemStack(filter);
    }

    @Override
    public ItemStack[] getFilterItems(ItemStack itemStack) {
        return new ItemStack[0];
    }

}