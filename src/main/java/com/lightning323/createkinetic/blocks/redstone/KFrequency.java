package com.lightning323.createkinetic.blocks.redstone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.Map;

public class KFrequency {
    public static final KFrequency EMPTY = new KFrequency(ItemStack.EMPTY);
    private static final Map<Item, KFrequency> simpleFrequencies = new IdentityHashMap<>();
    private ItemStack stack;
    private Item item;
    private int color;

    public static KFrequency of(ItemStack stack) {
        if (stack.isEmpty())
            return EMPTY;
        if (!stack.hasTag())
            return simpleFrequencies.computeIfAbsent(stack.getItem(), $ -> new KFrequency(stack));
        return new KFrequency(stack);
    }

    private KFrequency(ItemStack stack) {
        this.stack = stack;
        item = stack.getItem();
        CompoundTag displayTag = stack.getTagElement("display");
        color = displayTag != null && displayTag.contains("color") ? displayTag.getInt("color") : -1;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public int hashCode() {
        return (item.hashCode() * 31) ^ color;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        return obj instanceof KFrequency ? ((KFrequency) obj).item == item && ((KFrequency) obj).color == color
                : false;
    }

}