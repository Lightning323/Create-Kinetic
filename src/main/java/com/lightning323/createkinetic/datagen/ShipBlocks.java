package com.lightning323.createkinetic.datagen;

import com.lightning323.createkinetic.blocks.rudder.RudderBlock;
import com.simibubi.create.foundation.data.SharedProperties;

import java.util.List;

import static com.lightning323.createkinetic.CreateKinetic.REGISTRATE;

public class ShipBlocks {

    // Define the wood variants you want to generate
    private static final List<String> WOOD_TYPES = List.of(
        "oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "bamboo"
    );

    public static void register() {
        for (String wood : WOOD_TYPES) {
            String name = wood + "_ship_helm";

            REGISTRATE.block(name, RudderBlock::new)
                    .initialProperties(SharedProperties::wooden)
                    .blockstate((c, p) -> {
                        // This creates the new JSON (e.g., bamboo_ship_helm.json)
                        // BUT it sets its parent to your STATIC base model (ship_helm.json)
                        var woodModel = p.models().withExistingParent(name, p.modLoc("block/helm/ship_helm"))
                                .texture("wood", p.mcLoc("block/" + wood + "_planks"))
                                .texture("particle", p.mcLoc("block/" + wood + "_planks"));

                        // Now apply that specific model to the blockstate
                        p.horizontalBlock(c.get(), woodModel);
                    })
                    .item()
                    .build()
                    .register();
        }
    }
}