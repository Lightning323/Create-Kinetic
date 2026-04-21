package org.lightning323.createkinetic.registries;

import com.tterrag.registrate.providers.RegistrateLangProvider;

import static org.lightning323.createkinetic.CreateKinetic.MOD_ID;

public class KineticEnglishTranslationProvider {
    public static void addTranslations(RegistrateLangProvider provider) {
        provider.add("info." + MOD_ID + ".needs_ship", "You need a ship to use this helm");
        provider.add("itemGroup." + MOD_ID + ".tab", "Create Kinetic");
        provider.add("tooltip.createkinetic.ballast_tank", "Fill with fluid to increase weight, Can be used in submarines to change elevation");
        provider.add("tooltip.createkinetic.shiptotem", "A totem, useful for recovering lost or runaway ships");
        provider.add("tooltip.createkinetic.enchantedballast", "Keeps your ship upright");
        provider.add("tooltip.createkinetic.anchor", "Use redstone power to anchor (freeze) your ship");
        provider.add("tooltip.createkinetic.shiptotem.freeze", "Freezes the ship after recovery");
        provider.add("tooltip.createkinetic.frequency_filter", "Matches redstone signals against their corresponding address (frequency). Used in Smart Redstone Links");
        provider.add("tooltip.createkinetic.redstone_link", "Like a redstone link but uses frequency filters instead of items to send/receive signals");
        provider.add("createkinetic.rudder.not_enough_space", "The area must be clear in order to place a rudder blade!");
        provider.add("tooltip.createkinetic.helm", "Provides rotational power to rudders and other steering devices; Can be used alongside other controls");
        provider.add("tooltip.createkinetic.retractable_sail", "A retractable sail, useful for propelling a ship");
        provider.add("tooltip.createkinetic.sail_pulley", "A pulley with sails instead of rope, useful for propelling a ship");
        provider.add("tooltip.createkinetic.rudder", "Can be used to steer or dive a ship");
        provider.add("tooltip.createkinetic.forward_crank", "A crank that can be used to propel a ship");
        provider.add("tooltip.createkinetic.elevator_crank", "A crank that can be used to propel a ship");
        provider.add("key.createkinetic.control", "Kinetic Controls");
        provider.add("key.createkinetic.control.down", "Elevator (Down)");
        provider.add("key.createkinetic.control.up", "Elevator (Up)");
    }
}
