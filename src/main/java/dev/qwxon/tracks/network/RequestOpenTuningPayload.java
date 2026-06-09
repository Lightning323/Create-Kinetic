/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.neoforged.neoforge.network.PacketDistributor
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package dev.qwxon.tracks.network;

import dev.qwxon.tracks.Tracks;
import dev.qwxon.tracks.config.TracksServerConfig;
import dev.qwxon.tracks.network.OpenTuningScreenPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestOpenTuningPayload() implements CustomPacketPayload
{
    public static final Type<RequestOpenTuningPayload> TYPE = new Type(Tracks.path("request_open_tuning"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestOpenTuningPayload> STREAM_CODEC = StreamCodec.unit(new RequestOpenTuningPayload());

    public static void handle(RequestOpenTuningPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player patt0$temp = context.player();
            if (patt0$temp instanceof ServerPlayer) {
                ServerPlayer player = (ServerPlayer)patt0$temp;
                if (TracksServerConfig.renderTuningCheatsEnabled() && player.hasPermissions(2)) {
                    PacketDistributor.sendToPlayer((ServerPlayer)player, (CustomPacketPayload)new OpenTuningScreenPayload(), (CustomPacketPayload[])new CustomPacketPayload[0]);
                }
            }
        });
    }

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

