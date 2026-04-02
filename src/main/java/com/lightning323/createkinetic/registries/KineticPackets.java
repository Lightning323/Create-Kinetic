package com.lightning323.createkinetic.registries;

import com.lightning323.createkinetic.CreateKinetic;
import com.lightning323.createkinetic.blocks.shipHelm.HelmControlPacket;
import com.lightning323.createkinetic.blocks.shipHelm.PlayerRidingPacket;
import com.lightning323.createkinetic.items.frequencyFilter.KFilterScreenPacket;
import com.lightning323.createkinetic.items.shipTotem.CustomTotemPacket;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

public enum KineticPackets {
    CONFIGURE_FILTER(KFilterScreenPacket.class, KFilterScreenPacket::new, PLAY_TO_SERVER),
    HELM_CONTROL(HelmControlPacket.class, HelmControlPacket::decode, PLAY_TO_SERVER),
    PLAYER_RIDING(PlayerRidingPacket.class, PlayerRidingPacket::decode, PLAY_TO_CLIENT),
    CUSTOM_TOTEM(CustomTotemPacket.class, CustomTotemPacket::decode, PLAY_TO_CLIENT);
//    FILTER_NAME(KFilterNamePacket.class, KFilterNamePacket::new, PLAY_TO_CLIENT);

    public static final ResourceLocation CHANNEL_NAME = CreateKinetic.resource("main");
    public static final int NETWORK_VERSION = 3;
    public static final String NETWORK_VERSION_STR = String.valueOf(NETWORK_VERSION);
    private static SimpleChannel channel;

    private PacketType<?> packetType;

    <T extends SimplePacketBase> KineticPackets(Class<T> type, Function<FriendlyByteBuf, T> factory,
                                                NetworkDirection direction) {
        packetType = new PacketType<>(type, factory, direction);
    }

    public static void registerPackets() {
        channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .serverAcceptedVersions(NETWORK_VERSION_STR::equals)
                .clientAcceptedVersions(NETWORK_VERSION_STR::equals)
                .networkProtocolVersion(() -> NETWORK_VERSION_STR)
                .simpleChannel();

        for (KineticPackets packet : values())
            packet.packetType.register();
    }

    public static SimpleChannel getChannel() {
        return channel;
    }

    public static void sendToNear(Level world, BlockPos pos, int range, Object message) {
        getChannel().send(
                PacketDistributor.NEAR.with(TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), range, world.dimension())),
                message);
    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        getChannel().sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        getChannel().sendToServer(packet);
    }

    private static class PacketType<T extends SimplePacketBase> {
        private static int index = 0;

        private BiConsumer<T, FriendlyByteBuf> encoder;
        private Function<FriendlyByteBuf, T> decoder;
        private BiConsumer<T, Supplier<Context>> handler;
        private Class<T> type;
        private NetworkDirection direction;

        private PacketType(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
            encoder = T::write;
            decoder = factory;
            handler = (packet, contextSupplier) -> {
                Context context = contextSupplier.get();
                if (packet.handle(context)) {
                    context.setPacketHandled(true);
                }
            };
            this.type = type;
            this.direction = direction;
        }

        private void register() {
            getChannel().messageBuilder(type, index++, direction)
                    .encoder(encoder)
                    .decoder(decoder)
                    .consumerNetworkThread(handler)
                    .add();
        }
    }

}
