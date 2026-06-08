package org.lightning323.createkinetic;

import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.lightning323.createkinetic.content.gyroscope.GyroscopeController;
import org.lightning323.createkinetic.content.joystick.JoystickControlClient;
import org.lightning323.createkinetic.content.joystick.JoystickSessions;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.createmod.catnip.lang.FontHelper.Palette;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


/**
 * Tracks forked from https://github.com/ChiyahaRe/Create-Tracks-Plus
 */
@Mod(CreateKinetic.MODID)
public class CreateKinetic {
    public static final String MODID = "createkinetic";
    private static final NonNullSupplier<KineticRegistrate> REGISTRATE = NonNullSupplier.lazy(() -> (KineticRegistrate) ((CreateRegistrate) KineticRegistrate.create(CreateKinetic.MODID).defaultCreativeTab((ResourceKey) null)).setTooltipModifierFactory((item) -> (new ItemDescription.Modifier(item, Palette.STANDARD_CREATE)).andThen(TooltipModifier.mapNull(KineticStats.create(item)))));

    public static final ResourceLocation TAB_SECTION = ResourceLocation.fromNamespaceAndPath("simulated", "simulated");

    static KineticRegistrate getRegistrate() {
        return (KineticRegistrate) REGISTRATE.get();
    }

    public CreateKinetic(IEventBus modEventBus, ModContainer modContainer) {
        getRegistrate().registerEventListeners(modEventBus);
        KineticBlocks.register();
        KineticBlockEntityTypes.register();
        KineticMenuTypes.register();
        modContainer.registerConfig(Type.COMMON, Config.SPEC);
        modContainer.registerConfig(Type.CLIENT, Config.CLIENT_SPEC);
        modEventBus.register(KineticPackets.class);
        modEventBus.register(Config.class);
        NeoForge.EVENT_BUS.register(JoystickSessions.class);
        NeoForge.EVENT_BUS.register(GyroscopeController.class);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            registerClientHandlers(modEventBus);
            KineticPartialModels.init();
        }

    }

    private static void registerClientHandlers(IEventBus modEventBus) {
        modEventBus.register(KineticClient.class);
        modEventBus.register(KineticKeys.class);
        NeoForge.EVENT_BUS.register(JoystickControlClient.class);
    }
}
