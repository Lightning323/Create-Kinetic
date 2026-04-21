//package org.lightning323.createkinetic.registries;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.level.Level;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.neoforge.network.PacketDistributor;
//import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
//import net.neoforged.neoforge.network.registration.PayloadRegistrar;
//import org.lightning323.createkinetic.CreateKinetic;
//import org.lightning323.createkinetic.items.frequencyFilter.KFilterScreenPacket;
//import org.lightning323.createkinetic.items.shipTotem.CustomTotemPacket;
//
//import java.util.function.BiConsumer;
//import java.util.function.Function;
//import java.util.function.Supplier;
//
//@EventBusSubscriber(modid = CreateKinetic.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
//public class KineticPackets {
//
//    @SubscribeEvent
//    public static void register(final RegisterPayloadHandlersEvent event) {
//        final PayloadRegistrar registrar = event.registrar("1"); // Version string
//
//        // Register Client -> Server
//        registrar.playToServer(
//                KFilterScreenPacket.TYPE,
//                KFilterScreenPacket.STREAM_CODEC, // Use StreamCodecs for 1.21.1
//                KFilterScreenPacket::handle
//        );
//
//        // Register Server -> Client
//        registrar.playToClient(
//                CustomTotemPacket.TYPE,
//                CustomTotemPacket.STREAM_CODEC,
//                CustomTotemPacket::handle
//        );
//    }
//
//    public static void sendToServer(CustomPacketPayload packet) {
//        PacketDistributor.SERVER.noArg().send(packet);
//    }
//
//    public static void sendToClient(CustomPacketPayload packet, ServerPlayer player) {
//        PacketDistributor.PLAYER.with(player).send(packet);
//    }
//
//    public static void sendToNear(Level world, BlockPos pos, int range, CustomPacketPayload packet) {
//        PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(pos.getCenter(), range, world.dimension()))
//                .send(packet);
//    }
//}