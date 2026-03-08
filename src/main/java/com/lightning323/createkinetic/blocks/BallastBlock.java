package com.lightning323.createkinetic.blocks;

import com.lightning323.createkinetic.ship.SailsShipControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

public class BallastBlock extends Block {

    public BallastBlock(Properties settings) {
        super(settings);
    }
//
//    @Override
//    void addToShip(SailsShipControl controller) {
//        controller.numBallast++;
//    }
//
//    @Override
//    void removeFromShip(SailsShipControl controller) {
//        controller.numBallast--;
//    }
//
//    @Override
//    void sendMessage(Player player, SailsShipControl controller) {
//        player.sendSystemMessage(Component.literal("Ballast: "+ (controller.numBallast)));
//    }


}
