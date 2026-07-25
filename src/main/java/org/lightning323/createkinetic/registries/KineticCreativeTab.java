package org.lightning323.createkinetic.registries;

import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.content.creative_tools.item.ModItems;

import java.util.function.Supplier;

public class KineticCreativeTab {


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

        registerSectionItem(OFFROAD_CREATIVE_SECTION, "small_suspension_track", KineticItems.SMALL_SUSPENSION_TRACK::get);
        registerSectionItem(OFFROAD_CREATIVE_SECTION, "small_track_drive_wheel", KineticItems.SMALL_TRACK_DRIVE_WHEEL::get);
        registerSectionItem(OFFROAD_CREATIVE_SECTION, "track_mount", KineticBlocks.TRACK_MOUNT::asItem);
        registerSectionItem(SIMULATED_CREATIVE_SECTION, "gyroscope", () -> KineticBlocks.GYROSCOPE.asItem());
        registerSectionItem(SIMULATED_CREATIVE_SECTION, "joystick", () -> KineticBlocks.JOYSTICK.asItem());

        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "thruster", () -> PropulsionBlocks.THRUSTER_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "creative_thruster", () -> PropulsionBlocks.CREATIVE_THRUSTER_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "ion_thruster", () -> PropulsionBlocks.ION_THRUSTER_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "vector_thruster", () -> PropulsionBlocks.VECTOR_THRUSTER_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "creative_vector_thruster", () -> PropulsionBlocks.CREATIVE_VECTOR_THRUSTER_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "liquid_vector_thruster", () -> PropulsionBlocks.LIQUID_VECTOR_THRUSTER_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "redstone_converter", () -> PropulsionBlocks.REDSTONE_CONVERTER_BLOCK.get().asItem());

        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "wing", () -> PropulsionBlocks.WING_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "tempered_wing", () -> PropulsionBlocks.TEMPERED_WING_BLOCK.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "copycat_wing", () -> PropulsionBlocks.COPYCAT_WING.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "solid_burner", () -> PropulsionBlocks.SOLID_BURNER.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "liquid_burner", () -> PropulsionBlocks.LIQUID_BURNER.get().asItem());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "stirling_engine", () -> PropulsionBlocks.STIRLING_ENGINE_BLOCK.get().asItem());

        //TODO: We could use these for something in the future
//        registerSectionItem(MAIN_SECTION, "platinum_fluid_tank", () -> PropulsionBlocks.PLATINUM_FLUID_TANK.get().asItem());
//        registerSectionItem(MAIN_SECTION, "platinum_fluid_vessel", () -> PropulsionBlocks.PLATINUM_FLUID_VESSEL.get().asItem());

        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "turpentine_bucket", () -> PropulsionItems.TURPENTINE_BUCKET.get());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "pine_resin", () -> PropulsionItems.PINE_RESIN.get());
        registerSectionItem(AERONAUTICS_CREATIVE_SECTION, "oxidizer_bucket", () -> PropulsionItems.OXIDIZER_BUCKET.get());

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
