package org.lightning323.createkinetic.client.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.lightning323.createkinetic.CreateKinetic;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = CreateKinetic.ID, value = Dist.CLIENT)
public final class TooltipHandler {
    private static final List<ITooltipProvider> TOP_PROVIDERS = new ArrayList<>();

    static {
        TOP_PROVIDERS.add(new GenericSummaryTooltipProvider());
        TOP_PROVIDERS.add(new FuelTooltipProvider());
    }

    private TooltipHandler() {
    }

    @SubscribeEvent
    public static void addToItemTooltip(final ItemTooltipEvent event) {
        if (event.getItemStack().isEmpty()) {
            return;
        }

        final List<Component> currentTooltip = event.getToolTip();
        final List<Component> topList = new ArrayList<>();
        for (final ITooltipProvider provider : TOP_PROVIDERS) {
            provider.addText(event, topList);
        }
        if (topList.isEmpty()) {
            return;
        }

        final int insertIndex = currentTooltip.isEmpty() ? 0 : 1;
        currentTooltip.addAll(insertIndex, topList);
    }

    @SubscribeEvent
    public static void addToTooltipComponents(final RenderTooltipEvent.GatherComponents event) {
        // Visual tooltip components are intentionally disabled; keep text-only tooltip flow.
    }

    public static void wrapShiftHoldText(final List<Component> tooltipList, final String langKey, final Runnable addDetailedContent) {
        final boolean isShiftDown = Screen.hasShiftDown();
        final Component keyComponent = Component.translatable("create.tooltip.keyShift")
                .withStyle(isShiftDown ? ChatFormatting.WHITE : ChatFormatting.GRAY);
        tooltipList.add(Component.translatable(langKey, keyComponent).withStyle(ChatFormatting.DARK_GRAY));

        if (isShiftDown) {
            tooltipList.add(Component.empty());
            addDetailedContent.run();
        }
    }
}
