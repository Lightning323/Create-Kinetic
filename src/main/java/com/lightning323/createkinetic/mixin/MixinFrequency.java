package com.lightning323.createkinetic.mixin;

import com.google.common.hash.Hashing;
import com.lightning323.createkinetic.items.frequencyFilter.FrequencyFilterItem;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.charset.StandardCharsets;

@Mixin(targets = "com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler$Frequency")
public abstract class MixinFrequency {

    @Shadow
    private Item item;
    @Shadow
    private int color;

    @Unique
    private long hash;

    /**
     * @author YourName
     * @reason Overriding constructor logic to change how frequencies are initialized
     */
    @Inject(method = "<init>(Lnet/minecraft/world/item/ItemStack;)V", at = @At("RETURN"))
    private void onInit(ItemStack stack, CallbackInfo ci) {
        // This runs at the end of the constructor. 
        // You can re-assign the shadow fields or add custom logic.
        CompoundTag tag = stack.getTag();
//        if (tag != null && tag.contains("CustomFreqData")) {
//            // this.color = tag.getInt("CustomFreqData");
//        }

        if (tag != null && tag.contains(FrequencyFilterItem.ADDRESS_TAG)) {
            hash = Hashing.murmur3_128()
                    .hashString(tag.getString(FrequencyFilterItem.ADDRESS_TAG), StandardCharsets.UTF_8)
                    .asLong();
        } else hash = 0;
    }

    /**
     * @author YourName
     * @reason Changing frequency matching logic
     */
    @Overwrite(remap = false)
    public int hashCode() {
        // This mixes the top 32 bits and bottom 32 bits together
        int foldedHash = (int) (hash ^ (hash >>> 32));

        return (item.hashCode() * 31) ^ color ^ foldedHash;
    }

    /**
     * @author YourName
     * @reason Changing frequency equality logic
     */
    @Overwrite(remap = false)
    public boolean equals(Object obj) {
        if ((Object) this == obj) return true;
        if (!(obj instanceof Frequency other)) return false;

        // Accessing fields via shadow or casting

        /**
         * Item is very fast, It uses Singleton Pattern.
         * In Minecraft, an Item (like Items.IRON_INGOT or Items.APPLE) is only created once when the game starts up. These are stored in a central Registry.
         */

        MixinFrequency access = (MixinFrequency) (Object) other;
        return access.item == this.item
                && access.color == this.color
                && access.hash == this.hash;
    }
}