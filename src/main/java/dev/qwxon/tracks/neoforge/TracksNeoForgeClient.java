/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  dev.ryanhcode.offroad.index.OffroadBlockEntityTypes
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderers
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.bus.api.EventPriority
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.common.Mod
 *  net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
 *  net.neoforged.neoforge.client.event.ClientTickEvent$Post
 *  net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.network.PacketDistributor
 */
package dev.qwxon.tracks.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import dev.qwxon.tracks.TracksClient;
import dev.qwxon.tracks.client.TrackRenderTuning;
import dev.qwxon.tracks.client.TrackTuningScreen;
import dev.qwxon.tracks.content.blocks.sable_track.SableTrackRenderer;
import dev.qwxon.tracks.content.blocks.wheel_mount.AdjustableWheelMountRenderer;
import dev.qwxon.tracks.content.items.SuspensionKeyItem;
import dev.qwxon.tracks.index.TracksBlockEntityTypes;
import dev.qwxon.tracks.index.TracksItems;
import dev.qwxon.tracks.network.RequestOpenTuningPayload;
import dev.ryanhcode.offroad.index.OffroadBlockEntityTypes;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod(value="tracks", dist={Dist.CLIENT})
public class TracksNeoForgeClient {
    private static final KeyMapping OPEN_TUNING = new KeyMapping("key.tracks.open_tuning", InputConstants.Type.KEYSYM, 74, "key.categories.tracks");

    public TracksNeoForgeClient(IEventBus modBus) {
        modBus.addListener(TracksNeoForgeClient::clientSetup);
        modBus.addListener(TracksNeoForgeClient::registerKeys);
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, TracksNeoForgeClient::clientTick);
        TracksClient.init();
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            TrackRenderTuning.load(Minecraft.getInstance().gameDirectory.toPath().resolve("config/tracks-render-tuning.txt"));
            BlockEntityRenderers.register((BlockEntityType)((BlockEntityType)OffroadBlockEntityTypes.WHEEL_MOUNT.get()), AdjustableWheelMountRenderer::new);
            BlockEntityRenderers.register((BlockEntityType)((BlockEntityType)TracksBlockEntityTypes.SABLE_TRACK.get()), SableTrackRenderer::new);
        });
    }

    private static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_TUNING);
    }

    private static void clientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean bl = TracksClient.holdingSuspensionKey = minecraft.player != null && (minecraft.player.getMainHandItem().is(TracksItems.SUSPENSION_KEY.asItem()) || minecraft.player.getOffhandItem().is(TracksItems.SUSPENSION_KEY.asItem()));
        if (minecraft.player != null && TracksClient.holdingSuspensionKey) {
            ItemStack key = minecraft.player.getMainHandItem().is(TracksItems.SUSPENSION_KEY.asItem()) ? minecraft.player.getMainHandItem() : minecraft.player.getOffhandItem();
            SuspensionKeyItem.TuningMode mode = SuspensionKeyItem.getMode(key);
            TracksClient.holdingSuspensionKeyInPositionMode = mode == SuspensionKeyItem.TuningMode.POSITION;
            TracksClient.holdingSuspensionKeyInAllPositionMode = mode == SuspensionKeyItem.TuningMode.ALL_POSITION;
            TracksClient.holdingSuspensionKeyInResetMode = mode == SuspensionKeyItem.TuningMode.RESET;
        } else {
            TracksClient.holdingSuspensionKeyInPositionMode = false;
            TracksClient.holdingSuspensionKeyInAllPositionMode = false;
            TracksClient.holdingSuspensionKeyInResetMode = false;
        }
        while (OPEN_TUNING.consumeClick()) {
            if (minecraft.player == null) continue;
            PacketDistributor.sendToServer((CustomPacketPayload)new RequestOpenTuningPayload(), (CustomPacketPayload[])new CustomPacketPayload[0]);
        }
    }

    public static void openTuningScreen() {
        Minecraft.getInstance().setScreen((Screen)new TrackTuningScreen());
    }
}

