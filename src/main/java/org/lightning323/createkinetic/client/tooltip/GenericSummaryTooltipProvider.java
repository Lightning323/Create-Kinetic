package org.lightning323.createkinetic.client.tooltip;

import com.simibubi.create.foundation.item.TooltipHelper;
import net.createmod.catnip.lang.FontHelper.Palette;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.lightning323.createkinetic.CreateKinetic;

import java.util.List;

public final class GenericSummaryTooltipProvider implements ITooltipProvider {
    @Override
    public void addText(final ItemTooltipEvent event, final List<Component> tooltipList) {
        final Item item = event.getItemStack().getItem();
        final ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        if (id == null || !CreateKinetic.ID.equals(id.getNamespace())) {
            return;
        }

        final String path = CreateKinetic.ID + "." + id.getPath();
        
        // Priority: Specific item tooltip -> fallback Create logic -> generic shared summary
        String summaryKey = "item." + CreateKinetic.ID + "." + id.getPath() + ".tooltip.summary";
        if (!I18n.exists(summaryKey)) {
            summaryKey = path + ".tooltip.summary";
            if (!I18n.exists(summaryKey)) {
                summaryKey = CreateKinetic.ID + ".tooltip.shared." + id.getPath() + "_summary";
                if (!I18n.exists(summaryKey)) {
                    return;
                }
            }
        }

        final String finalSummaryKey = summaryKey;
        TooltipHandler.wrapShiftHoldText(tooltipList, "create.tooltip.holdForDescription", () -> {
            tooltipList.addAll(TooltipHelper.cutStringTextComponent(
                    Component.translatable(finalSummaryKey).getString(),
                    Palette.STANDARD_CREATE
            ));
        });
    }
}
