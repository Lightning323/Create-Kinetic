package com.lightning323.createkinetic.blocks.shipHelm;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class HelmControlPacket extends SimplePacketBase {
    private final BlockPos bePos; // The ID of the entity we are controlling
    private final HelmControlData data;

    public HelmControlPacket(BlockPos bePos, HelmControlData data) {
        this.bePos = bePos;
        this.data = data;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(bePos); // Write ID first
        buffer.writeFloat(data.forwardImpulse);
        buffer.writeFloat(data.leftImpulse);
        buffer.writeFloat(data.upImpulse);
        buffer.writeBoolean(data.sprintOn);
    }

    public static HelmControlPacket decode(FriendlyByteBuf buffer) {
        BlockPos bePos = buffer.readBlockPos();
        HelmControlData data = new HelmControlData();
        data.forwardImpulse = buffer.readFloat();
        data.leftImpulse = buffer.readFloat();
        data.upImpulse = buffer.readFloat();
        data.sprintOn = buffer.readBoolean();
        return new HelmControlPacket(bePos, data);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {

                BlockEntity target = player.level().getBlockEntity(bePos);
                if (target instanceof ShipHelmBlockEntity be) {
                    be.updateControl(data);
                }
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}