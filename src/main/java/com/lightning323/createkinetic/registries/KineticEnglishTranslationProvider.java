package com.lightning323.createkinetic.registries;

import com.tterrag.registrate.providers.RegistrateLangProvider;

import static com.lightning323.createkinetic.CreateKinetic.MOD_ID;

public class KineticEnglishTranslationProvider {
    public static void addTranslations(RegistrateLangProvider provider) {
        provider.add("info." + MOD_ID + ".needs_ship", "You need a ship to use this helm");
        provider.add("itemGroup." + MOD_ID + ".tab", "Create Kinetic");
        provider.add("tooltip.createkinetic.shiptotem", "A totem, useful for recovering lost or runaway ships");
        provider.add("tooltip.createkinetic.enchantedballast", "Keeps your ship upright");
        provider.add("tooltip.createkinetic.anchor", "Use redstone power to anchor (freeze) your ship");
        provider.add("tooltip.createkinetic.shiptotem.freeze", "Freezes the ship after recovery");
    }
}
