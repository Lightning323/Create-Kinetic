package com.lightning323.createkinetic.blocks.redstone;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.Map;

public class Frequency {
    public static final Frequency EMPTY = new Frequency(ItemStack.EMPTY);
    private static final Map<Item, Frequency> simpleFrequencies = new IdentityHashMap<>();
    private ItemStack stack;
    private Item item;
    private int color;

    public static Frequency of(ItemStack stack) {
        if (stack.isEmpty())
            return EMPTY;
        if (!stack.hasTag())
            return simpleFrequencies.computeIfAbsent(stack.getItem(), $ -> new Frequency(stack));
        return new Frequency(stack);
    }

    private Frequency(ItemStack stack) {
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
        return obj instanceof Frequency ? ((Frequency) obj).item == item && ((Frequency) obj).color == color
                : false;
    }

}