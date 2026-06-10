package org.lightning323.createkinetic;

import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import dev.simulated_team.simulated.util.SimColors;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.lightning323.createkinetic.content.gyroscope.GyroscopeController;
import org.lightning323.createkinetic.content.joystick.JoystickControlClient;
import org.lightning323.createkinetic.content.joystick.JoystickSessions;


/**
 * Tracks forked from https://github.com/ChiyahaRe/Create-Tracks-Plus
 */
@Mod(CreateKinetic.MOD_ID)
public class CreateKinetic {
    public static final String MOD_ID = "createkinetic";


    private static final NonNullSupplier<SimulatedRegistrate> REGISTRATE = NonNullSupplier.lazy(() ->
            (SimulatedRegistrate)new SimulatedRegistrate(path(MOD_ID), MOD_ID).defaultCreativeTab((ResourceKey)null));

    static SimulatedRegistrate getRegistrate() {
        return REGISTRATE.get();
    }
    public static final ResourceLocation TAB_SECTION = ResourceLocation.fromNamespaceAndPath("simulated", "simulated");

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

        //Init tracks
        setTooltips();
    }


    private static void setTooltips() {
        getRegistrate().setTooltipModifierFactory(item -> {
            Rarity rarity = item.getDefaultInstance().getRarity();
            FontHelper.Palette color = FontHelper.Palette.STANDARD_CREATE;
            if (rarity == Rarity.EPIC) {
                color = new FontHelper.Palette(TooltipHelper.styleFromColor((int) SimColors.EPIC_OURPLE), TooltipHelper.styleFromColor((ChatFormatting)rarity.color()));
            }
            return new ItemDescription.Modifier(item, color).andThen(TooltipModifier.mapNull((TooltipModifier)KineticStats.create((Item)item)));
        });
    }


    public static ResourceLocation path(String path) {
        return ResourceLocation.tryBuild((String) MOD_ID, (String) path);
    }

    private static void registerClientHandlers(IEventBus modEventBus) {
        modEventBus.register(KineticClient.class);
        modEventBus.register(KineticKeys.class);
        NeoForge.EVENT_BUS.register(JoystickControlClient.class);
    }
}
