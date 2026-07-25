package org.lightning323.createkinetic.client.tooltip;

import net.createmod.catnip.lang.FontHelper.Palette;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.lightning323.createkinetic.content.thruster.FluidThrusterProperties;
import org.lightning323.createkinetic.content.thruster.ThrusterFuelManager;

import java.util.List;
import java.util.Optional;

public final class FuelTooltipProvider implements ITooltipProvider {
    @Override
    public void addText(final ItemTooltipEvent event, final List<Component> tooltipList) {
        final ItemStack stack = event.getItemStack();
        final IFluidHandlerItem fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler == null) {
            return;
        }

        final FluidStack fluidStack = fluidHandler.getFluidInTank(0);
        if (fluidStack.isEmpty()) {
            return;
        }

        final FluidThrusterProperties properties = ThrusterFuelManager.getProperties(fluidStack.getFluid());
        if (properties == null) {
            return;
        }
        TooltipHandler.wrapShiftHoldText(tooltipList, "createkinetic.tooltip.holdForRocketFuelSummary", () -> {
            final int thrustPercent = Math.round(properties.thrustMultiplier() * 100.0f);
            final Component thrustLine = Component.translatable("createkinetic.tooltip.thrust")
                    .append(": ")
                    .withStyle(Palette.STANDARD_CREATE.primary())
                    .append(Component.literal(Integer.toString(thrustPercent)).withStyle(Palette.STANDARD_CREATE.highlight()))
                    .append(Component.literal("%").withStyle(Palette.STANDARD_CREATE.primary()));
            tooltipList.add(thrustLine);
            final int consumptionPercent = Math.round(properties.consumptionMultiplier() * 100.0f);
            final Component consumptionLine = Component.translatable("createkinetic.tooltip.consumption")
                    .append(": ")
                    .withStyle(Palette.STANDARD_CREATE.primary())
                    .append(Component.literal(Integer.toString(consumptionPercent)).withStyle(Palette.STANDARD_CREATE.highlight()))
                    .append(Component.literal("%").withStyle(Palette.STANDARD_CREATE.primary()));
            tooltipList.add(consumptionLine);
            tooltipList.add(Component.empty());
        });
    }

    @Override
    public Optional<TooltipComponent> getVisual(final ItemTooltipEvent event) {
        return Optional.empty();
    }
}


