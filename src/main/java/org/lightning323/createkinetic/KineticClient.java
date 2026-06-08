package org.lightning323.createkinetic;

import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.lightning323.createkinetic.content.gyroscope.GyroscopeBlockEntity;
import org.lightning323.createkinetic.content.gyroscope.GyroscopeItemRenderer;
import org.lightning323.createkinetic.content.gyroscope.GyroscopeVisual;
import org.lightning323.createkinetic.content.joystick.JoystickBlockEntity;
import org.lightning323.createkinetic.content.joystick.JoystickDirection;
import org.lightning323.createkinetic.content.joystick.JoystickHudOverlay;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.lightning323.createkinetic.content.joystick.JoystickVisual;

import static org.lightning323.createkinetic.CreateKinetic.MODID;
import static org.lightning323.createkinetic.CreateKinetic.TAB_SECTION;

public final class KineticClient {
    private KineticClient() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        //Fixed
        SimpleBlockEntityVisualizer.builder(KineticBlockEntityTypes.GYROSCOPE.get())
                .factory((ctx, be, partialTick) ->
                        new GyroscopeVisual(ctx, (GyroscopeBlockEntity) be, partialTick))
                .neverSkipVanillaRender()
                .apply();

        SimpleBlockEntityVisualizer.builder(KineticBlockEntityTypes.JOYSTICK.get())
                .factory((ctx, be, partialTick) ->
                        new JoystickVisual(ctx, (JoystickBlockEntity) be, partialTick))
                .neverSkipVanillaRender()
                .apply();

        BaseConfigScreen.setDefaultActionFor(MODID, (base) -> base.withButtonLabels("Client Settings", "Common Settings", "Common Settings").withSpecs(Config.CLIENT_SPEC, Config.SPEC, Config.SPEC));
    }

    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        ModContainer container = (ModContainer) ModList.get().getModContainerById(MODID).orElseThrow(() -> new IllegalStateException("Aeroworks mod container missing on LoadComplete"));
        Supplier<IConfigScreenFactory> factory = () -> (mc, previousScreen) -> new BaseConfigScreen(previousScreen, MODID);
        container.registerExtensionPoint(IConfigScreenFactory.class, factory);
    }

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, ResourceLocation.fromNamespaceAndPath(MODID, "joystick_hud"), new JoystickHudOverlay());
    }

    @SubscribeEvent
    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> tintIndex >= 0 && tintIndex < JoystickDirection.VALUES.length ? -16777216 | JoystickDirection.VALUES[tintIndex].colorRgb : -1, new ItemLike[]{KineticBlocks.JOYSTICK.asItem()});
    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        final GyroscopeItemRenderer renderer = new GyroscopeItemRenderer();
        event.registerItem(new IClientItemExtensions() {
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        }, new Item[]{KineticBlocks.GYROSCOPE.asItem()});
    }

    @SubscribeEvent
    public static void onModelBakingComplete(ModelEvent.ModifyBakingResult event) {
        ResourceLocation itemRl = ResourceLocation.fromNamespaceAndPath(MODID, "gyroscope");
        ModelResourceLocation key = new ModelResourceLocation(itemRl, "inventory");
        Map<ModelResourceLocation, BakedModel> registry = event.getModels();
        BakedModel original = (BakedModel) registry.get(key);
        if (original != null) {
            registry.put(key, new CustomRenderedItemModel(original));
        }

    }

    private static AtomicBoolean built = new AtomicBoolean(false);

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (!built.get()) {
            registerSectionItem(TAB_SECTION, "gyroscope", () -> KineticBlocks.GYROSCOPE.asItem());
            registerSectionItem(TAB_SECTION, "joystick", () -> KineticBlocks.JOYSTICK.asItem());
            built.set(true);
        }
    }

    private static void registerSectionItem(ResourceLocation sectionId, String itemPath, Supplier<Item> itemSupplier) {
        SimulatedRegistrate.TAB_ITEMS.add(itemSupplier);
        SimulatedRegistrate.ITEM_TO_SECTION.put(ResourceLocation.fromNamespaceAndPath(MODID, itemPath), sectionId);
    }

}
