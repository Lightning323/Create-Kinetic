package com.lightning323.createkinetic.registries;

import com.tterrag.registrate.providers.RegistrateLangProvider;

import static com.lightning323.createkinetic.CreateKinetic.MOD_ID;

public class KineticEnglishTranslationProvider {
    public static void addTranslations(RegistrateLangProvider provider) {
        provider.add("info." + MOD_ID + ".needs_ship", "You need a ship to use this helm");
        provider.add("itemGroup." + MOD_ID + ".tab", "Create Kinetic");
        provider.add("tooltip.createkinetic.ballast_tank", "Fill with fluid to increase weight, Can be used in submarines to change elevation");
        provider.add("tooltip.createkinetic.shiptotem", "A totem, useful for recovering lost or runaway ships");
        provider.add("tooltip.createkinetic.enchantedballast", "Keeps your ship upright");
        provider.add("tooltip.createkinetic.anchor", "Use redstone power to anchor (freeze) your ship");
        provider.add("tooltip.createkinetic.shiptotem.freeze", "Freezes the ship after recovery");
        provider.add("tooltip.createkinetic.frequency_filter","Matches redstone signals against their corresponding address (frequency). Used in Smart Redstone Links");
        provider.add("tooltip.createkinetic.redstone_link","Like a redstone link but uses frequency filters instead of items to send/receive signals");
    }
}
