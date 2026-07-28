/*
 * Decompiled with CFR 0.152.
 */
package org.lightning323.createkinetic.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import dev.ryanhcode.offroad.index.OffroadBlockEntityTypes;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lightning323.createkinetic.config.Config;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.content.blocks.sable_track.SableTrackRenderer;
import org.lightning323.createkinetic.content.blocks.wheel_mount.AdjustableWheelMountRenderer;
import org.lightning323.createkinetic.content.blocks.reaction_wheel.ReactionWheelBlockEntity;
import org.lightning323.createkinetic.content.blocks.reaction_wheel.ReactionWheelItemRenderer;
import org.lightning323.createkinetic.content.blocks.reaction_wheel.ReactionWheelVisual;
import org.lightning323.createkinetic.content.blocks.joystick.*;
import org.lightning323.createkinetic.registries.*;
import org.lightning323.createkinetic.network.RequestOpenTuningPayload;

import java.util.Map;
import java.util.function.Supplier;

import static org.lightning323.createkinetic.CreateKinetic.ID;

@Mod(value = CreateKinetic.ID, dist = {Dist.CLIENT})
public class KineticClient {
    private static final KeyMapping OPEN_TUNING = new KeyMapping("key." + CreateKinetic.ID + ".open_tuning", InputConstants.Type.KEYSYM, 74, "key.categories." + CreateKinetic.ID);


    private static void registerClientHandlers(IEventBus modEventBus) {
        modEventBus.register(KineticClient.class);
        modEventBus.register(KineticKeys.class);
        NeoForge.EVENT_BUS.register(JoystickControlClient.class);
    }


    public static void init(IEventBus modBus) {
        registerClientHandlers(modBus);
        KineticPartialModels.init();
        modBus.addListener(KineticClient::clientSetup);
        modBus.addListener(KineticClient::registerKeys);
//        modBus.addListener(KineticClient::buildContents);
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, KineticClient::clientTick);
        TracksPartialModels.init();
        TracksSpriteShifts.init();
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            TrackRenderTuning.load(Minecraft.getInstance().gameDirectory.toPath().resolve("config/" + CreateKinetic.ID + "-render-tuning.txt"));
            BlockEntityRenderers.register((BlockEntityType) ((BlockEntityType) OffroadBlockEntityTypes.WHEEL_MOUNT.get()), AdjustableWheelMountRenderer::new);
            BlockEntityRenderers.register((BlockEntityType) ((BlockEntityType) KineticBlockEntityTypes.SABLE_TRACK.get()), SableTrackRenderer::new);
        });

        //Fixed
        SimpleBlockEntityVisualizer.builder(KineticBlockEntityTypes.GYROSCOPE.get())
                .factory((ctx, be, partialTick) ->
                        new ReactionWheelVisual(ctx, (ReactionWheelBlockEntity) be, partialTick))
                .neverSkipVanillaRender()
                .apply();

        SimpleBlockEntityVisualizer.builder(KineticBlockEntityTypes.JOYSTICK.get())
                .factory((ctx, be, partialTick) ->
                        new JoystickVisual(ctx, (JoystickBlockEntity) be, partialTick))
                .neverSkipVanillaRender()
                .apply();

        BaseConfigScreen.setDefaultActionFor(ID, (base) -> base.withButtonLabels("Client Settings", "Common Settings", "Common Settings").withSpecs(Config.CLIENT_SPEC, Config.SPEC, Config.SPEC));
    }

    private static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_TUNING);
    }

    private static void clientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();

        while (OPEN_TUNING.consumeClick()) {
            if (minecraft.player == null) continue;
            PacketDistributor.sendToServer((CustomPacketPayload) new RequestOpenTuningPayload(), (CustomPacketPayload[]) new CustomPacketPayload[0]);
        }
    }

    public static void openTuningScreen() {
        Minecraft.getInstance().setScreen((Screen) new TrackTuningScreen());
    }


    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        ModContainer container = (ModContainer) ModList.get().getModContainerById(ID).orElseThrow(() -> new IllegalStateException("Aeroworks mod container missing on LoadComplete"));
        Supplier<IConfigScreenFactory> factory = () -> (mc, previousScreen) -> new BaseConfigScreen(previousScreen, ID);
        container.registerExtensionPoint(IConfigScreenFactory.class, factory);
    }

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, ResourceLocation.fromNamespaceAndPath(ID, "joystick_hud"), new JoystickHudOverlay());
    }

    @SubscribeEvent
    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> tintIndex >= 0 && tintIndex < JoystickDirection.VALUES.length ? -16777216 | JoystickDirection.VALUES[tintIndex].colorRgb : -1, new ItemLike[]{KineticBlocks.JOYSTICK.asItem()});
    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        final ReactionWheelItemRenderer renderer = new ReactionWheelItemRenderer();
        event.registerItem(new IClientItemExtensions() {
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        }, new Item[]{KineticBlocks.REACTION_WHEEL.asItem()});
    }

    @SubscribeEvent
    public static void onModelBakingComplete(ModelEvent.ModifyBakingResult event) {
        ResourceLocation itemRl = ResourceLocation.fromNamespaceAndPath(ID, "gyroscope");
        ModelResourceLocation key = new ModelResourceLocation(itemRl, "inventory");
        Map<ModelResourceLocation, BakedModel> registry = event.getModels();
        BakedModel original = (BakedModel) registry.get(key);
        if (original != null) {
            registry.put(key, new CustomRenderedItemModel(original));
        }

    }


//    /**
//     * Register items in the existing tabs
//     */
//    private static AtomicBoolean built = new AtomicBoolean(false);

//    private static void registerSectionItem(ResourceLocation sectionId, String itemPath, Supplier<Item> itemSupplier) {
//        SimulatedRegistrate.TAB_ITEMS.add(itemSupplier);
//        SimulatedRegistrate.ITEM_TO_SECTION.put(ResourceLocation.fromNamespaceAndPath(ID, itemPath), sectionId);
//    }
//
//    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
//        if (!built.get()) {
//            registerSectionItem(OFFROAD_CREATIVE_SECTION, "small_suspension_track", KineticItems.SMALL_SUSPENSION_TRACK::get);
//            registerSectionItem(OFFROAD_CREATIVE_SECTION, "small_track_drive_wheel", KineticItems.SMALL_TRACK_DRIVE_WHEEL::get);
//            registerSectionItem(OFFROAD_CREATIVE_SECTION, "track_mount", KineticBlocks.TRACK_MOUNT::asItem);
//            registerSectionItem(SIMULATED_CREATIVE_SECTION, "gyroscope", () -> KineticBlocks.GYROSCOPE.asItem());
//            registerSectionItem(SIMULATED_CREATIVE_SECTION, "joystick", () -> KineticBlocks.JOYSTICK.asItem());
//            built.set(true);
//        }
//    }

}

