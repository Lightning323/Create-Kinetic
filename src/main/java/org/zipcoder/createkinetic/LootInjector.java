package org.zipcoder.createkinetic;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LootInjector {

    static final int shipTotemChance = 20;
    static final int freezeTotemChance = 10;
    static final int emptyChance = Math.max(1, 100 - shipTotemChance - freezeTotemChance);

    /**
     * LootJS.modifiers((event) => {
     *   event.addLootTypeModifier(LootType.CHEST).randomChance(0.15).addLoot("createkinetic:freeze_ship_totem");
     *   event.addLootTypeModifier(LootType.CHEST).randomChance(0.2).addLoot("createkinetic:ship_totem");
     * });
     * @param event
     */

    @SubscribeEvent
    public static void onLootLoad(LootTableLoadEvent event) {
        // Only affect vanilla chests
        if (event.getName().getNamespace().equals("minecraft")
                && event.getName().getPath().startsWith("chests/")) {
//            System.out.println("Adding totem loot to " + event.getName().getPath());
            // Build a loot pool
            LootPool pool = LootPool.lootPool()
                    .add(LootItem.lootTableItem(ModItems.SHIP_TOTEM.get()).setWeight(shipTotemChance))//X% change of ship totem
                    .add(LootItem.lootTableItem(ModItems.FREEZE_SHIP_TOTEM.get()).setWeight(freezeTotemChance))//Y% change of freeze totem
                    .add(EmptyLootItem.emptyItem().setWeight(emptyChance)) // chance for nothing
                            .setRolls(ConstantValue.exactly(1)) // roll once per chest
                            .build();

            // Add the pool to the chest
            event.getTable().addPool(pool);
        }
    }
}
