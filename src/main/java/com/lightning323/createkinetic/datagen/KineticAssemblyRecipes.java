package com.lightning323.createkinetic.datagen;

import com.lightning323.createkinetic.CreateKinetic;
import com.lightning323.createkinetic.blocks.helm.ShipHelmBlock;
import com.lightning323.createkinetic.registries.KineticBlocks;
import com.lightning323.createkinetic.registries.KineticItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeBuilder;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class KineticAssemblyRecipes extends RecipeProvider {

    public KineticAssemblyRecipes(DataGenerator generator) {
        super(generator.getPackOutput());
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        // Build your recipes here
        createMechanicalPart(consumer);
        shipHelmRecipe(consumer, Items.OAK_PLANKS, Items.OAK_FENCE, KineticBlocks.OAK_SHIP_HELM.get());
        shipHelmRecipe(consumer, Items.BIRCH_PLANKS, Items.BIRCH_FENCE, KineticBlocks.BIRCH_SHIP_HELM.get());
        shipHelmRecipe(consumer, Items.JUNGLE_PLANKS, Items.JUNGLE_FENCE, KineticBlocks.JUNGLE_SHIP_HELM.get());
        shipHelmRecipe(consumer, Items.ACACIA_PLANKS, Items.ACACIA_FENCE, KineticBlocks.ACACIA_SHIP_HELM.get());
        shipHelmRecipe(consumer, Items.DARK_OAK_PLANKS, Items.DARK_OAK_FENCE, KineticBlocks.DARK_OAK_SHIP_HELM.get());
        shipHelmRecipe(consumer, Items.WARPED_PLANKS, Items.WARPED_FENCE, KineticBlocks.WARPED_SHIP_HELM.get());
        shipHelmRecipe(consumer, Items.MANGROVE_PLANKS, Items.MANGROVE_FENCE, KineticBlocks.MANGROVE_SHIP_HELM.get());
        shipHelmRecipe(consumer, Items.BAMBOO_PLANKS, Items.BAMBOO_FENCE, KineticBlocks.BAMBOO_SHIP_HELM.get());
        shipHelmRecipe(consumer, Items.CRIMSON_PLANKS, Items.CRIMSON_FENCE, KineticBlocks.CRIMSON_SHIP_HELM.get());
        shipHelmRecipe(consumer, Items.SPRUCE_PLANKS, Items.SPRUCE_FENCE, KineticBlocks.SPRUCE_SHIP_HELM.get());
    }

    //    private void registerPressingRecipe(Consumer<FinishedRecipe> consumer, Item input, Item output) {
//        new ProcessingRecipeBuilder<>(PressingRecipe::new, CreateKinetic.resource("sail_cloth_sheet"))
//                .withItemIngredients(Ingredient.of(input))
//                .withSingleItemOutput(KineticItems.SAIL_CLOTH.asStack())
//                .build(consumer);
//    }
//private void registerMixingRecipe(Consumer<FinishedRecipe> consumer) {
//    new ProcessingRecipeBuilder<>(MixingRecipe::new, CreateKinetic.resource("treated_wood"))
//            .withItemIngredients(Ingredient.of(ItemTags.PLANKS))
//            .withFluidIngredients(FluidIngredient.fromFluid(Fluids.WATER, 250))
//            .withSingleItemOutput(new ItemStack(Items.OAK_PLANKS)) // Replace with your treated wood
//            .requiresHeat(HeatCondition.HEATED)
//            .build(consumer);
//}
    private void shipHelmRecipe(Consumer<FinishedRecipe> consumer, Item planks, Item fence, ShipHelmBlock output) {
        MechanicalCraftingRecipeBuilder.shapedRecipe(output)
                .key('S', KineticItems.STEERING_MECHANISM.get())
                .key('P', planks)
                .key('F', fence)
                .key('B', AllBlocks.BRASS_CASING)

                .patternLine(" F ")
                .patternLine("FSF")
                .patternLine("PFP")
                .patternLine("PBP")
                .build(consumer);
    }

    private void createMechanicalPart(Consumer<FinishedRecipe> consumer) {
        ResourceLocation id = CreateKinetic.resource("mechanical_sail_assembly");

        new SequencedAssemblyRecipeBuilder(id)
                .require(AllItems.ANDESITE_ALLOY) // <--- ADD THIS: The base item to start the process
                .transitionTo(KineticItems.INCOMPLETE_STEERING_MECHANISM.get())
                .addOutput(KineticItems.STEERING_MECHANISM.get(), 1)
                .loops(3)
                .addStep(DeployerApplicationRecipe::new, b -> b.require(Items.WHITE_WOOL))
                .addStep(DeployerApplicationRecipe::new, b -> b.require(Items.IRON_NUGGET))
                .addStep(PressingRecipe::new, b -> b)
                .build(consumer);
    }
}