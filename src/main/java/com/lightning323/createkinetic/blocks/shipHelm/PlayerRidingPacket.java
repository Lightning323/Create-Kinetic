package com.lightning323.createkinetic.blocks.shipHelm;

import com.lightning323.createkinetic.client.KineticClientData;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class PlayerRidingPacket extends SimplePacketBase {
    private final BlockPos entityPos;

    public PlayerRidingPacket(BlockPos pos) {
        this.entityPos = pos;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(entityPos);
    }

    public static PlayerRidingPacket decode(FriendlyByteBuf buffer) {
        return new PlayerRidingPacket(buffer.readBlockPos());
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            // DistExecutor ensures this client-only code doesn't crash a dedicated server
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
                    KineticClientData.setControllingBlockEntity(Minecraft.getInstance().level, entityPos);
                }
            });
        });
        context.setPacketHandled(true);
        return true;
    }
}