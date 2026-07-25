package org.lightning323.createkinetic.content.creative_tools.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.lightning323.createkinetic.CreateKinetic;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateKinetic.ID);

    public static final DeferredHolder<Item, AssemblerStickItem> ASSEMBLER_STICK = ITEMS.register("assembler_stick",
            () -> new AssemblerStickItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, AutoGlueItem> AUTO_GLUE = ITEMS.register("auto_glue",
            () -> new AutoGlueItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, GluedContraptionMoverItem> GLUED_CONTRAPTION_MOVER = ITEMS.register("glued_contraption_mover",
            () -> new GluedContraptionMoverItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, GluedContraptionClonerItem> GLUED_CONTRAPTION_CLONER = ITEMS.register("glued_contraption_cloner",
            () -> new GluedContraptionClonerItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, ContraptionRemoverItem> CONTRAPTION_REMOVER = ITEMS.register("contraption_remover",
            () -> new ContraptionRemoverItem(new Item.Properties().stacksTo(1)));

    private ModItems() {
    }

    public static void register(final IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
