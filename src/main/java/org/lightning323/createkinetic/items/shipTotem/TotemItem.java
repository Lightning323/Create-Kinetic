package org.lightning323.createkinetic.items.shipTotem;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

import java.util.function.BiPredicate;

//EnderpearlItem
public class TotemItem extends Item {
    private BiPredicate<ServerPlayer, ItemStack> _onUse;

    public TotemItem() {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    }

    public TotemItem(BiPredicate<ServerPlayer, ItemStack> _onUse) {
        super(new Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
        this._onUse = _onUse;
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
//        if (!world.isClientSide()) {
//            if (_onUse != null && _onUse.test((ServerPlayer) player, itemStack)){//Trigger the animation and sound
//                KineticPackets.sendToClient(new CustomTotemPacket(itemStack), (ServerPlayer) player);
//
//                //Set player to null to play for everyone
//                world.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.2f, 1f);
//
//                if (!player.isCreative()) {
//                    itemStack.setCount(0);
//                }
//            }
//        }

        return InteractionResultHolder.consume(itemStack);
    }

}