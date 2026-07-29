package org.lightning323.createkinetic.registries;

import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.content.creative_tools.item.ModItems;

import java.util.function.Supplier;

public class KineticCreativeTabs {


    public static final ResourceLocation SIMULATED_CREATIVE_SECTION = ResourceLocation.fromNamespaceAndPath("simulated", "simulated");
    public static final ResourceLocation AERONAUTICS_CREATIVE_SECTION = ResourceLocation.fromNamespaceAndPath("aeronautics", "aeronautics");
    public static final ResourceLocation OFFROAD_CREATIVE_SECTION = ResourceLocation.fromNamespaceAndPath("offroad", "offroad");


    //    private static final ResourceLocation MAIN_SECTION = ResourceLocation.fromNamespaceAndPath(CreateKinetic.ID, "kinetic_main");
    private static final ResourceLocation TOOLS_SECTION = ResourceLocation.fromNamespaceAndPath(CreateKinetic.ID, "kinetic_tools");
    private static boolean sectionsInitialized = false;

    public static synchronized void registerAeronauticsSections() {
        if (sectionsInitialized) {
            return;
        }

        registerSectionItem(OFFROAD_CREATIVE_SECTION, "small_suspension_track", KineticItems.SUSPENSION_TRACK::get);
        registerSectionItem(OFFROAD_CREATIVE_SECTION, "small_track_drive_wheel", KineticItems.TRACK_DRIVE_WHEEL::get);
        registerSectionItem(OFFROAD_CREATIVE_SECTION, "track_mount", KineticBlocks.TRACK_MOUNT::asItem);
        registerSectionItem(SIMULATED_CREATIVE_SECTION, "reaction_wheel", () -> KineticBlocks.REACTION_WHEEL.asItem());
        registerSectionItem(SIMULATED_CREATIVE_SECTION, "joystick", () -> KineticBlocks.JOYSTICK.asItem());

        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "thruster", () -> KineticBlocks.THRUSTER_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "creative_thruster", () -> KineticBlocks.CREATIVE_THRUSTER_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "ion_thruster", () -> KineticBlocks.ION_THRUSTER_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "vector_thruster", () -> KineticBlocks.VECTOR_THRUSTER_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "creative_vector_thruster", () -> KineticBlocks.CREATIVE_VECTOR_THRUSTER_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "liquid_vector_thruster", () -> KineticBlocks.LIQUID_VECTOR_THRUSTER_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "redstone_converter", () -> KineticBlocks.REDSTONE_CONVERTER_BLOCK.get().asItem());

        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "wing", () -> KineticBlocks.WING_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "tempered_wing", () -> KineticBlocks.TEMPERED_WING_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "copycat_wing", () -> KineticBlocks.COPYCAT_WING.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "solid_burner", () -> KineticBlocks.SOLID_BURNER.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "liquid_burner", () -> KineticBlocks.LIQUID_BURNER.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "stirling_engine", () -> KineticBlocks.STIRLING_ENGINE_BLOCK.get().asItem());

        //TODO: We could use these for something in the future
//        registerSectionItem(MAIN_SECTION, "platinum_fluid_tank", () -> PropulsionBlocks.PLATINUM_FLUID_TANK.get().asItem());
//        registerSectionItem(MAIN_SECTION, "platinum_fluid_vessel", () -> PropulsionBlocks.PLATINUM_FLUID_VESSEL.get().asItem());

        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "turpentine_bucket", () -> KineticItems.TURPENTINE_BUCKET.get());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "pine_resin", () -> KineticItems.PINE_RESIN.get());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "oxidizer_bucket", () -> KineticItems.OXIDIZER_BUCKET.get());

        registerSectionItem(TOOLS_SECTION, "assembler_stick", () -> ModItems.ASSEMBLER_STICK.get());
        registerSectionItem(TOOLS_SECTION, "auto_glue", () -> ModItems.AUTO_GLUE.get());
        registerSectionItem(TOOLS_SECTION, "glued_contraption_mover", () -> ModItems.GLUED_CONTRAPTION_MOVER.get());
        registerSectionItem(TOOLS_SECTION, "glued_contraption_cloner", () -> ModItems.GLUED_CONTRAPTION_CLONER.get());
        registerSectionItem(TOOLS_SECTION, "contraption_remover", () -> ModItems.CONTRAPTION_REMOVER.get());

        sectionsInitialized = true;
    }

    private static void registerSectionItem(ResourceLocation sectionId, String itemPath, Supplier<Item> itemSupplier) {
        SimulatedRegistrate.TAB_ITEMS.add(itemSupplier);
        SimulatedRegistrate.ITEM_TO_SECTION.put(ResourceLocation.fromNamespaceAndPath(CreateKinetic.ID, itemPath), sectionId);
    }
}
