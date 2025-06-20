package org.zipcoder.createkinetic.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.zipcoder.createkinetic.ModItems;

import java.util.function.Supplier;

public class CustomTotemPacket {
    public CustomTotemPacket() {}

    public static void encode(CustomTotemPacket msg, FriendlyByteBuf buf) {}

    public static CustomTotemPacket decode(FriendlyByteBuf buf) {
        return new CustomTotemPacket();
    }

    public static void handle(CustomTotemPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Only run on client
            Minecraft mc = Minecraft.getInstance();
            ItemStack fakeTotem = new ItemStack(ModItems.SHIP_TOTEM.get());
            mc.gameRenderer.displayItemActivation(fakeTotem);
        });
        ctx.get().setPacketHandled(true);
    }
}
