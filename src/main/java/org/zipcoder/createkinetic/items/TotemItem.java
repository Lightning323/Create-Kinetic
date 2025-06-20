package org.zipcoder.createkinetic.items;


import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.zipcoder.createkinetic.ModItems;

import java.util.function.Consumer;

//EnderpearlItem
public class TotemItem extends Item {


    private Consumer<ServerPlayer> _onUse;
    private SoundEvent _sound;
    private String _particleID;
//    private RegistryEntry.Reference<SoundEvent> _regSound;

    public TotemItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
        _sound = SoundEvents.TOTEM_USE;
    }


    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        //Play the sound
        world.playSound(player, player, _sound, SoundSource.HOSTILE, 0.2f, 1f);

        if (!player.isCreative()) {
            itemStack.setCount(0);
        }

        if (world.isClientSide()) {
            System.out.println("Displaying item activation");
            Minecraft.getInstance().gameRenderer.displayItemActivation(new ItemStack(this));
        } else {
            //Trigger animation
            //player.level().broadcastEntityEvent(player, (byte) 35);
            if (_onUse != null) _onUse.accept((ServerPlayer) player);
            //ServerPlayNetworking.send((ServerPlayerEntity) user, new EffigyParticlePayload(_particleID));

            // If the sound is provided as a reg key we do this server side
//            if (_regSound != null) {
//                var serverPlayerEntity = (ServerPlayerEntity) user;
//                serverPlayerEntity.networkHandler.sendPacket(new PlaySoundS2CPacket(_regSound, SoundCategory.NEUTRAL,
//                        serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), 128.0F, 1.0F, 1l));
//            }
        }

        return InteractionResultHolder.consume(itemStack);
    }

}