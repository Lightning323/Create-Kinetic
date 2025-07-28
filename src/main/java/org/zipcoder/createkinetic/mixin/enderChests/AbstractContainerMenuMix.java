package org.zipcoder.createkinetic.mixin.enderChests;

import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.zipcoder.createkinetic.mixinInterfaces.AbstractContainerMenu_I;

import java.util.List;

@Mixin(value = AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMix implements AbstractContainerMenu_I {

    @Shadow
    private final NonNullList<ItemStack> lastSlots = NonNullList.create();
    @Shadow
    public final NonNullList<Slot> slots = NonNullList.create();
    @Shadow
    private final List<DataSlot> dataSlots = Lists.newArrayList();
    @Shadow
    private ItemStack carried = ItemStack.EMPTY;
    @Shadow
    private final NonNullList<ItemStack> remoteSlots = NonNullList.create();

    public void clearSlots() {
        this.slots.clear();
        this.lastSlots.clear();
        this.remoteSlots.clear();
    }
}
