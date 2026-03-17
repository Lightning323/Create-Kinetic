package com.lightning323.createkinetic.blocks.redstone;

import com.lightning323.createkinetic.items.frequencyFilter.FrequencyFilterItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public class KFrequency {
    public static final KFrequency EMPTY = new KFrequency(ItemStack.EMPTY);
    private static final Map<Item, KFrequency> simpleFrequencies = new IdentityHashMap<>();

    private ItemStack stack;//Used for asthetics
    private final String address;//For comparison

    public static KFrequency of(ItemStack stack) {
        if (stack.isEmpty())
            return EMPTY;
        if (!stack.hasTag())
            return simpleFrequencies.computeIfAbsent(stack.getItem(), $ -> new KFrequency(stack));
        return new KFrequency(stack);
    }

    private KFrequency(ItemStack stack) {
        this.stack = stack;

        if (stack.getItem() instanceof FrequencyFilterItem && stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("Address")) {
                address = tag.getString("Address");
                return;
            }
        }
        address="";
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(address);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof KFrequency other)) return false;

        return this.address.equals(other.address);
    }
}


//This version respects item type and NBT data
/*
public class KFrequency {
    public static final KFrequency EMPTY = new KFrequency(ItemStack.EMPTY);
    private static final Map<Item, KFrequency> simpleFrequencies = new IdentityHashMap<>();
    private ItemStack stack;
    private Item item;
    private int color;
    private final CompoundTag tag; // Store the tag for comparison

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
        this.tag = stack.getTag();
        color = displayTag != null && displayTag.contains("color") ? displayTag.getInt("color") : -1;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public int hashCode() {
        // Use the Item's hash and the Tag's hash
        int result = item.hashCode();
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof KFrequency other)) return false;

        // Use standard NBT equality check
        if (this.item != other.item) return false;

        if (this.tag == null) return other.tag == null;
        return this.tag.equals(other.tag);
    }

}
* */