/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package org.lightning323.createkinetic.network;

import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.content.blocks.sable_track.SableTrackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SelectTrackTuningModePayload(BlockPos pos, String key) implements CustomPacketPayload
{
    public static final Type<SelectTrackTuningModePayload> TYPE = new Type(CreateKinetic.path("select_track_tuning_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SelectTrackTuningModePayload> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, SelectTrackTuningModePayload>(){

        public SelectTrackTuningModePayload decode(RegistryFriendlyByteBuf buffer) {
            return new SelectTrackTuningModePayload(buffer.readBlockPos(), buffer.readUtf(64));
        }

        public void encode(RegistryFriendlyByteBuf buffer, SelectTrackTuningModePayload payload) {
            buffer.writeBlockPos(payload.pos());
            buffer.writeUtf(payload.key(), 64);
        }
    };

    public static void handle(SelectTrackTuningModePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            BlockEntity patt0$temp = context.player().level().getBlockEntity(payload.pos());
            if (patt0$temp instanceof SableTrackBlockEntity) {
                SableTrackBlockEntity track = (SableTrackBlockEntity)patt0$temp;
                if (context.player().distanceToSqr(payload.pos().getCenter()) < 64.0) {
                    track.selectScrollTuningMode(payload.key());
                }
            }
        });
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

