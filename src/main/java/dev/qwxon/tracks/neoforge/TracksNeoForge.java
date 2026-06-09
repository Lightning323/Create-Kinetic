/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.ModContainer
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.config.IConfigSpec
 *  net.neoforged.fml.config.ModConfig$Type
 *  net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.neoforged.fml.loading.FMLEnvironment
 *  net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
 *  net.neoforged.neoforge.network.registration.PayloadRegistrar
 */
package dev.qwxon.tracks.neoforge;

import dev.qwxon.tracks.Tracks;
import dev.qwxon.tracks.config.TracksServerConfig;
import dev.qwxon.tracks.network.OpenTuningScreenPayload;
import dev.qwxon.tracks.network.RequestOpenTuningPayload;
import dev.qwxon.tracks.network.SelectTrackTuningModePayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.lightning323.createkinetic.CreateKinetic;

@Mod(value="tracks")
public class TracksNeoForge {
    public TracksNeoForge(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(TracksNeoForge::init);
        modBus.addListener(TracksNeoForge::registerPayloads);
        modContainer.registerConfig(ModConfig.Type.SERVER, (IConfigSpec)TracksServerConfig.SPEC);
        Tracks.init();
        CreateKinetic.getRegistrate().registerEventListeners(modBus);
    }

    private static void init(FMLCommonSetupEvent event) {
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("tracks").versioned("1.0.0");
        registrar.playToServer(RequestOpenTuningPayload.TYPE, RequestOpenTuningPayload.STREAM_CODEC, RequestOpenTuningPayload::handle);
        registrar.playToServer(SelectTrackTuningModePayload.TYPE, SelectTrackTuningModePayload.STREAM_CODEC, SelectTrackTuningModePayload::handle);
        registrar.playToClient(OpenTuningScreenPayload.TYPE, OpenTuningScreenPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                TracksNeoForgeClient.openTuningScreen();
            }
        }));
    }
}

