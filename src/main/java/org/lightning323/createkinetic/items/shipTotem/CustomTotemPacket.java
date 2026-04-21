//package org.lightning323.createkinetic.items.shipTotem;
//
//import com.simibubi.create.foundation.networking.SimplePacketBase;
//import net.minecraft.client.Minecraft;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.world.item.ItemStack;
//import net.minecraftforge.network.NetworkEvent;
//
//public class CustomTotemPacket extends SimplePacketBase {
//    private final ItemStack item;
//
//    public CustomTotemPacket(ItemStack item) {
//        this.item = item;
//    }
//
//    @Override
//    public void write(FriendlyByteBuf buf) {
//        buf.writeItem(item);
//    }
//
//    public static CustomTotemPacket decode(FriendlyByteBuf buf) {
//        ItemStack item = buf.readItem();
//        return new CustomTotemPacket(item);
//    }
//
//    @Override
//    public boolean handle(NetworkEvent.Context ctx) {
//        ctx.enqueueWork(() -> {
//            Minecraft mc = Minecraft.getInstance();
//            if (mc.player != null && item != null) {
//                ItemStack stack = item;
//                stack.setCount(1);
//
//                //If we play the sound here, it will only sound for this player
////                Level world = Minecraft.getInstance().level;
////                world.playSound(mc.player, mc.player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.HOSTILE, 0.2f, 1f);
//
//                Minecraft.getInstance().gameRenderer.displayItemActivation(stack);
//            }
//        });
//        ctx.setPacketHandled(true);
//        return true;
//    }
//}
