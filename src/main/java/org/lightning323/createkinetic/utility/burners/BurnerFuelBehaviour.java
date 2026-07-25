package org.lightning323.createkinetic.utility.burners;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.items.IItemHandler;

public class BurnerFuelBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<BurnerFuelBehaviour> TYPE = new BehaviourType<>();

    private BurnerFuelItemHandler itemHandler;
    public ItemStack fuelStack = ItemStack.EMPTY;
    private final Runnable onInsertion;

    public BurnerFuelBehaviour(SmartBlockEntity be, Runnable onInsertion) {
        super(be);
        this.itemHandler = new BurnerFuelItemHandler(this);
        this.onInsertion = onInsertion;
    }

    public boolean tryConsumeFuel() {
        if (fuelStack.isEmpty()) return false;
        
        int burnTime = fuelStack.getBurnTime(RecipeType.SMELTING);
        if (burnTime > 0 && blockEntity instanceof IBurner burner) {
            burner.setBurnTime(burnTime);
            fuelStack.shrink(1);
            if (fuelStack.isEmpty()) {
                fuelStack = ItemStack.EMPTY;
            }
            return true;
        }
        return false;
    }


    public boolean handlePlayerInteraction(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(blockEntity instanceof IBurner)) return false;

        if (stack.isEmpty() && !fuelStack.isEmpty()) {
            player.getInventory().placeItemBackInInventory(itemHandler.extractItem(0, 64, false));
            blockEntity.notifyUpdate();
            return true;
        }

        if (!stack.isEmpty() && itemHandler.isItemValid(0, stack)) {
            ItemStack remainder = itemHandler.insertItem(0, stack, false);
            player.setItemInHand(hand, remainder);
            if (remainder.getCount() != stack.getCount()) {
                blockEntity.notifyUpdate();
                if (onInsertion != null) onInsertion.run();
                return true;
            }
        }

        return false;
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    //NBT

    @Override
    public void write(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
        if (fuelStack.isEmpty()) {
            nbt.remove("Fuel");
        } else {
            nbt.put("Fuel", fuelStack.save(registries));
        }
        super.write(nbt, registries, clientPacket);
    }

    @Override
    public void read(CompoundTag nbt, net.minecraft.core.HolderLookup.Provider registries, boolean clientPacket) {
        if (nbt.contains("Fuel")) {
            fuelStack = ItemStack.parse(registries, nbt.getCompound("Fuel")).orElse(ItemStack.EMPTY);
        } else {
            fuelStack = ItemStack.EMPTY;
        }
        super.read(nbt, registries, clientPacket);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
