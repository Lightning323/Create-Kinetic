package org.lightning323.createkinetic.registries;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.SoundType;
import org.lightning323.createkinetic.propulsion.ThrusterBlock;

import java.util.List;

import static org.lightning323.createkinetic.CreateKinetic.REGISTRATE;

public class KineticBlocks {

    public static final BlockEntry<ThrusterBlock> THRUSTER = REGISTRATE
            .block("thruster", ThrusterBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .properties(p -> p
                    .noOcclusion()
                    .strength(0.2f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.COPPER))
            .register();

    public static void register() {
    }

}