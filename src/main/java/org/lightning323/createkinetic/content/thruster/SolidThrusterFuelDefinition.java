package org.lightning323.createkinetic.content.thruster;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Optional;

public record SolidThrusterFuelDefinition(
    Optional<ResourceLocation> itemId,
    Optional<ResourceLocation> itemTagId,
    float thrustMultiplier,
    float consumptionMultiplier,
    Optional<Integer> burnTicks,
    ThrusterParticleType particle,
    List<ResourceLocation> overrideTextures,
    Optional<Integer> overrideColor,
    boolean useItemColor,
    Optional<String> requiredMod
) {
    public static final Codec<SolidThrusterFuelDefinition> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ResourceLocation.CODEC.optionalFieldOf("item").forGetter(SolidThrusterFuelDefinition::itemId),
            ResourceLocation.CODEC.optionalFieldOf("item_tag").forGetter(SolidThrusterFuelDefinition::itemTagId),
            Codec.FLOAT.fieldOf("thrust_multiplier").forGetter(SolidThrusterFuelDefinition::thrustMultiplier),
            Codec.FLOAT.fieldOf("consumption_multiplier").forGetter(SolidThrusterFuelDefinition::consumptionMultiplier),
            Codec.INT.optionalFieldOf("burn_ticks").forGetter(SolidThrusterFuelDefinition::burnTicks),
            ThrusterParticleType.CODEC.optionalFieldOf("particle", ThrusterParticleType.PLUME).forGetter(SolidThrusterFuelDefinition::particle),
            ResourceLocation.CODEC.listOf().optionalFieldOf("override_textures", List.of()).forGetter(SolidThrusterFuelDefinition::overrideTextures),
            Codec.INT.optionalFieldOf("override_color").forGetter(SolidThrusterFuelDefinition::overrideColor),
            Codec.BOOL.optionalFieldOf("use_item_color", false).forGetter(SolidThrusterFuelDefinition::useItemColor),
            Codec.STRING.optionalFieldOf("required_mod").forGetter(SolidThrusterFuelDefinition::requiredMod)
        ).apply(instance, SolidThrusterFuelDefinition::new));

    public boolean isItemEntry() {
        return itemId.isPresent();
    }

    public boolean isTagEntry() {
        return itemTagId.isPresent();
    }

    public Item getItem() {
        if (itemId.isEmpty()) {
            return Items.AIR;
        }
        Item item = BuiltInRegistries.ITEM.get(itemId.get());
        return item == null ? Items.AIR : item;
    }

    public TagKey<Item> getItemTag() {
        return TagKey.create(Registries.ITEM, itemTagId.orElse(ResourceLocation.withDefaultNamespace("air")));
    }

    public int resolveBurnTicks(ItemStack stack) {
        if (burnTicks.isPresent() && burnTicks.get() > 0) {
            return burnTicks.get();
        }
        int smelting = stack.getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING);
        if (smelting <= 0) {
            smelting = 1600;
        }
        float multiplier = consumptionMultiplier;
        if (multiplier <= 0) {
            multiplier = 1.0f;
        }
        return Math.max(1, Math.round(smelting / multiplier));
    }
}
