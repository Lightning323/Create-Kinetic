package com.lightning323.createkinetic.blocks;

import com.lightning323.createkinetic.ship.KineticShipControl;

public class EnchantedBallastBlock extends CountableBlock {
    public EnchantedBallastBlock(Properties settings) {
        super(settings);
    }

    @Override
    void addToShip(KineticShipControl controller) {
        controller.numEnchantedBallast++;
    }

    @Override
    void removeFromShip(KineticShipControl controller) {
        controller.numEnchantedBallast--;
    }
}
