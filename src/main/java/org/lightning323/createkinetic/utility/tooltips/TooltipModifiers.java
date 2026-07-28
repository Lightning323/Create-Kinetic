package org.lightning323.createkinetic.utility.tooltips;

import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.lang.FontHelper.Palette;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.config.PropulsionConfig;
import org.lightning323.createkinetic.registries.KineticBlocks;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class TooltipModifiers {
    private static final HashMap<Item, Function<SummaryPayload, String>> tooltipModificationLookup = new HashMap<Item, Function<SummaryPayload, String>>();
    private static final HashMap<Item, String> summaryKeyLookup = new HashMap<>();
    private static final HashMap<Item, String> conditionKeyLookup = new HashMap<>();

    static {
        summaryKeyLookup.put(KineticBlocks.THRUSTER_BLOCK.get().asItem(), "createkinetic.tooltip.shared.thruster_summary");
        summaryKeyLookup.put(KineticBlocks.CREATIVE_THRUSTER_BLOCK.get().asItem(), "createkinetic.tooltip.shared.thruster_summary");

        //Thruster
        tooltipModificationLookup.put(KineticBlocks.THRUSTER_BLOCK.get().asItem(), (payload) -> {
            int thrusterStrength = (int) Math.round(PropulsionConfig.BASE_THRUST.get());
            return Component.translatable(getSummaryKey(payload.item(), payload.path() + ".tooltip.summary")).getString().replace("{}", String.valueOf(thrusterStrength));
        });
        //Creative thruster
        tooltipModificationLookup.put(KineticBlocks.CREATIVE_THRUSTER_BLOCK.get().asItem(), (payload) -> {
            int thrusterStrength = (int) Math.round(PropulsionConfig.CREATIVE_THRUSTER_BASE_THRUST.get());
            return Component.translatable(getSummaryKey(payload.item(), payload.path() + ".tooltip.summary")).getString().replace("{}", String.valueOf(thrusterStrength));
        });
    }

    public static String getSummaryKey(Item item, String defaultKey) {
        return summaryKeyLookup.getOrDefault(item, defaultKey);
    }

    public static String getCondition1Key(Item item, String defaultKey) {
        return conditionKeyLookup.getOrDefault(item, defaultKey);
    }

    public static boolean apply(Item item, List<Component> tooltipList) {
        Function<SummaryPayload, String> summarySupplier = tooltipModificationLookup.get(item);
        if (summarySupplier != null) {
            String path = CreateKinetic.ID + "." + BuiltInRegistries.ITEM.getKey(item).getPath();
            String summary = summarySupplier.apply(new SummaryPayload(item, path));
            tooltipList.addAll(TooltipHelper.cutStringTextComponent(summary, Palette.STANDARD_CREATE));
            return true;
        }
        return false;
    }

    private record SummaryPayload (Item item, String path) {};
}
