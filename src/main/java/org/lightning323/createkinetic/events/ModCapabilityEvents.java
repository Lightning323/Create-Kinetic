package org.lightning323.createkinetic.events;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.lightning323.createkinetic.compat.PropulsionCompatibility;
import org.lightning323.createkinetic.content.thruster.ion_thruster.IonThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.thruster.ThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.vector_thruster.ion_vector_thruster.IonVectorThrusterBlockEntity;
import org.lightning323.createkinetic.content.thruster.vector_thruster.liquid_vector_thruster.LiquidVectorThrusterBlockEntity;
import org.lightning323.createkinetic.registries.KineticBlockEntities;

public class ModCapabilityEvents {
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            KineticBlockEntities.THRUSTER_BLOCK_ENTITY.get(),
            ModCapabilityEvents::getThrusterFluidHandler
        );
        event.registerBlockEntity(
            Capabilities.FluidHandler.BLOCK,
            KineticBlockEntities.LIQUID_VECTOR_THRUSTER_BLOCK_ENTITY.get(),
            ModCapabilityEvents::getLiquidVectorThrusterFluidHandler
        );
        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            KineticBlockEntities.ION_THRUSTER_BLOCK_ENTITY.get(),
            (be, side) -> ((IonThrusterBlockEntity) be).getEnergyHandler(side)
        );
        event.registerBlockEntity(
            Capabilities.EnergyStorage.BLOCK,
            KineticBlockEntities.ION_VECTOR_THRUSTER_BLOCK_ENTITY.get(),
            (be, side) -> ((IonVectorThrusterBlockEntity) be).getEnergyHandler(side)
        );

        registerComputerCraftCapabilitiesIfAvailable(event);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerComputerCraftCapabilitiesIfAvailable(RegisterCapabilitiesEvent event) {
        if (!PropulsionCompatibility.CC_ACTIVE) {
            return;
        }
        try {
            Class<?> peripheralCapabilityClass = Class.forName("dan200.computercraft.api.peripheral.PeripheralCapability");
            Object peripheralCapability = peripheralCapabilityClass.getMethod("get").invoke(null);
            BlockCapability capability = (BlockCapability) peripheralCapability;

            event.registerBlockEntity(
                capability,
                KineticBlockEntities.THRUSTER_BLOCK_ENTITY.get(),
                (be, side) -> be.computerBehaviour == null ? null : be.computerBehaviour.getPeripheralCapability()
            );
            event.registerBlockEntity(
                capability,
                KineticBlockEntities.ION_THRUSTER_BLOCK_ENTITY.get(),
                (be, side) -> be.computerBehaviour == null ? null : be.computerBehaviour.getPeripheralCapability()
            );
            event.registerBlockEntity(
                capability,
                KineticBlockEntities.CREATIVE_THRUSTER_BLOCK_ENTITY.get(),
                (be, side) -> be.computerBehaviour == null ? null : be.computerBehaviour.getPeripheralCapability()
            );
            event.registerBlockEntity(
                capability,
                KineticBlockEntities.CREATIVE_VECTOR_THRUSTER_BLOCK_ENTITY.get(),
                (be, side) -> be.computerBehaviour == null ? null : be.computerBehaviour.getPeripheralCapability()
            );
            event.registerBlockEntity(
                capability,
                KineticBlockEntities.LIQUID_VECTOR_THRUSTER_BLOCK_ENTITY.get(),
                (be, side) -> be.computerBehaviour == null ? null : be.computerBehaviour.getPeripheralCapability()
            );

        } catch (Throwable ignored) {
            // ComputerCraft not installed or API unavailable.
        }
    }

    private static IFluidHandler getThrusterFluidHandler(ThrusterBlockEntity thrusterBlockEntity, Direction side) {
        return thrusterBlockEntity.getFluidHandler(side);
    }

    private static IFluidHandler getLiquidVectorThrusterFluidHandler(LiquidVectorThrusterBlockEntity blockEntity, Direction side) {
        return blockEntity.getFluidHandler(side);
    }

}
