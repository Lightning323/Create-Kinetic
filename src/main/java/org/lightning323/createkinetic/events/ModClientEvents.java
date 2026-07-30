package org.lightning323.createkinetic.events;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.simibubi.create.content.decoration.encasing.EncasedCTBehaviour;
import com.simibubi.create.content.fluids.tank.FluidTankRenderer;
import com.simibubi.create.foundation.model.ModelSwapper;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.content.thruster.ion_thruster.IonThrusterRenderer;
import org.lightning323.createkinetic.content.thruster.thruster.ThrusterRenderer;
import org.lightning323.createkinetic.content.thruster.thruster.creative_thruster.CreativeThrusterRenderer;
import org.lightning323.createkinetic.content.thruster.vector_thruster.VectorRedstoneLinkRenderer;
import org.lightning323.createkinetic.content.thruster.vector_thruster.liquid_vector_thruster.LiquidVectorThrusterRenderer;
import org.lightning323.createkinetic.ponder.DeltaPonderPlugin;
import org.lightning323.createkinetic.registries.*;
import org.lightning323.createkinetic.utility.value_boxes.DualRowValueRenderer;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = CreateKinetic.ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.InteractionKeyMappingTriggered event) {
        // Removed assembly gauge click handling.
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        DualRowValueRenderer.tick();
        VectorRedstoneLinkRenderer.tick();
    }

    @SubscribeEvent
    public static void onClientCommandsRegister(RegisterClientCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> propulsionCommand = Commands.literal("propulsion");
        event.getDispatcher().register(propulsionCommand
                .then(Commands.literal("config")
                        .executes((ctx) -> {
                            openConfig();
                            return 1;
                        })));
    }

    private static void openConfig() {
        Screen parent = Minecraft.getInstance().screen;
        ScreenOpener.open(new BaseConfigScreen(parent, CreateKinetic.ID));
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        // Removed colorized optical lens handling.
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        // Removed assembly gauge overlay.
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.parse("minecraft:block/water_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.parse("minecraft:block/water_flow");
            }

            @Override
            public int getTintColor() {
                return 0xFFD69E49;
            }

            @Override
            public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                return 0xFFD69E49;
            }

            @Override
            public int getTintColor(FluidStack stack) {
                return 0xFFD69E49;
            }
        }, KineticFluids.TURPENTINE_TYPE);


        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.parse("minecraft:block/water_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.parse("minecraft:block/water_flow");
            }

            @Override
            public int getTintColor() {
                return 0xFF88CCFF;
            }

            @Override
            public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                return 0xFF88CCFF;
            }

            @Override
            public int getTintColor(FluidStack stack) {
                return 0xFF88CCFF;
            }
        }, KineticFluids.OXIDIZER_TYPE);
    }

    @SubscribeEvent
    public static void clientInit(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(KineticFluids.TURPENTINE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(KineticFluids.FLOWING_TURPENTINE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(KineticFluids.OXIDIZER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(KineticFluids.FLOWING_OXIDIZER.get(), RenderType.translucent());
        });

        PonderIndex.addPlugin(new DeltaPonderPlugin());
        PropulsionInstanceTypes.register();

    }

//    @SubscribeEvent
//    private static void buildContents(BuildCreativeModeTabContentsEvent event) {
////       System.out.println("BuildCreativeModeTabContentsEvent "+event.getTabKey());
//    }


    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(KineticBlockEntities.CREATIVE_THRUSTER_BLOCK_ENTITY.get(), CreativeThrusterRenderer::new);
        event.registerBlockEntityRenderer(KineticBlockEntities.THRUSTER_BLOCK_ENTITY.get(), ThrusterRenderer::new);
        event.registerBlockEntityRenderer(KineticBlockEntities.ION_THRUSTER_BLOCK_ENTITY.get(), IonThrusterRenderer::new);
        event.registerBlockEntityRenderer(KineticBlockEntities.CREATIVE_VECTOR_THRUSTER_BLOCK_ENTITY.get(), IonThrusterRenderer::new);
        event.registerBlockEntityRenderer(KineticBlockEntities.LIQUID_VECTOR_THRUSTER_BLOCK_ENTITY.get(), LiquidVectorThrusterRenderer::new);
    }
}
