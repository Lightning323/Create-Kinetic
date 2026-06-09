/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 */
package dev.qwxon.tracks.network;

import dev.qwxon.tracks.Tracks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record OpenTuningScreenPayload() implements CustomPacketPayload
{
    public static final Type<OpenTuningScreenPayload> TYPE = new Type(Tracks.path("open_tuning_screen"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenTuningScreenPayload> STREAM_CODEC = StreamCodec.unit(new OpenTuningScreenPayload());

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

