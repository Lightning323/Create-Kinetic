/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 */
package org.lightning323.createkinetic.network;

import org.lightning323.createkinetic.CreateKinetic;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record OpenTuningScreenPayload() implements CustomPacketPayload
{
    public static final Type<OpenTuningScreenPayload> TYPE = new Type(CreateKinetic.path("open_tuning_screen"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenTuningScreenPayload> STREAM_CODEC = StreamCodec.unit(new OpenTuningScreenPayload());

    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

