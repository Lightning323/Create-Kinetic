package org.zipcoder.createkinetic.enderChests;

public enum ChestType {
    IRON(1),
    COPPER(1),
    GOLD(2),
    LAPIS(2),
    REDSTONE(2),

    DIAMOND(3),
    EMERALD(3+6),
    NETHERITE(3+6+6+6);

    private final int extraSlots;

    ChestType(int extraSlots) {
        this.extraSlots = extraSlots;
    }

    public int getExtraSlots() {
        return extraSlots;
    }
}
