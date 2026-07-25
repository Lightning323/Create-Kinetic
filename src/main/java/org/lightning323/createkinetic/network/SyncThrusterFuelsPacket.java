package org.lightning323.createkinetic.network;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.content.thruster.FluidThrusterProperties;
import org.lightning323.createkinetic.content.thruster.ThrusterFuelManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SyncThrusterFuelsPacket implements CustomPacketPayload {
    public static final Type<SyncThrusterFuelsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CreateKinetic.ID, "sync_thruster_fuels"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncThrusterFuelsPacket> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> payload.encode(buf),
        buf -> SyncThrusterFuelsPacket.decode(buf)
    );

    private final Map<ResourceLocation, FluidThrusterProperties> fuelMap;
    private final Set<ResourceLocation> removedFuelIds;

    public static SyncThrusterFuelsPacket create(Map<Fluid, FluidThrusterProperties> mapToSync, Set<ResourceLocation> removedFuelIds) {
        Map<ResourceLocation, FluidThrusterProperties> networkSafeMap = new HashMap<>();
        mapToSync.forEach((fluid, props) -> {
            ResourceLocation key = BuiltInRegistries.FLUID.getKey(fluid);
            if (key != null) {
                networkSafeMap.put(key, props);
            }
        });
        return new SyncThrusterFuelsPacket(networkSafeMap, removedFuelIds);
    }

    private SyncThrusterFuelsPacket(Map<ResourceLocation, FluidThrusterProperties> fuelMap, Set<ResourceLocation> removedFuelIds) {
        this.fuelMap = fuelMap;
        this.removedFuelIds = removedFuelIds;
    }

    public static SyncThrusterFuelsPacket decode(FriendlyByteBuf buf) {
        Map<ResourceLocation, FluidThrusterProperties> map = buf.readMap(FriendlyByteBuf::readResourceLocation, FluidThrusterProperties::decode);
        Set<ResourceLocation> removedFuelIds = buf.readCollection(java.util.HashSet::new, FriendlyByteBuf::readResourceLocation);
        return new SyncThrusterFuelsPacket(map, removedFuelIds);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeMap(this.fuelMap, FriendlyByteBuf::writeResourceLocation, (b, props) -> props.encode(b));
        buf.writeCollection(this.removedFuelIds, FriendlyByteBuf::writeResourceLocation);
    }

    public void handle() {
        ThrusterFuelManager.updateClient(this.fuelMap, this.removedFuelIds);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
