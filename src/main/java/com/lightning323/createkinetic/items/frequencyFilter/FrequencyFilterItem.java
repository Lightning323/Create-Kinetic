package com.lightning323.createkinetic.items.frequencyFilter;

import java.util.Collections;
import java.util.List;

import com.lightning323.createkinetic.blocks.redstone.KFrequency;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.logistics.filter.PackageFilterMenu;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;

import com.simibubi.create.foundation.recipe.ItemCopyingRecipe.SupportsItemCopying;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class FrequencyFilterItem extends FilterItem implements MenuProvider, SupportsItemCopying {

    public FrequencyFilterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // Only run this on the server
        if (!level.isClientSide) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.contains("Address")) {
                tag.putString("Address", KFrequency.generateRandomString(20));
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {

        return FrequencyFilterMenu.create(id, inv, player.getMainHandItem());
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.createkinetic.frequency_filter")
                    .withStyle(ChatFormatting.GOLD));
        } else {
            tooltip.add(TooltipHelper.holdShift(FontHelper.Palette.STANDARD_CREATE, false));
        }
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
    public Component getName(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Address")) {
            String address = tag.getString("Address");
            // Returns "Frequency Filter (AddressName)"
            return Component.translatable(this.getDescriptionId())
                    .append(" (")
                    .append(Component.literal(address).withStyle(ChatFormatting.GOLD))
                    .append(")");
        }
        return super.getName(stack);
    }

    @Override
    public ItemStack[] getFilterItems(ItemStack itemStack) {
        return new ItemStack[0];
    }

}