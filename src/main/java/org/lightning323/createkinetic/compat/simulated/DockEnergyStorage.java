package org.lightning323.createkinetic.compat.simulated;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.lightning323.createkinetic.CreateKinetic;

public class DockEnergyStorage implements IEnergyStorage {
   private final BlockEntity blockEntity;
   private int energy;
   private final int capacity;
   private BlockPos connectedPos;
   private DockEnergyStorage connectedStorage;
   private boolean isPrimary;

   public DockEnergyStorage(BlockEntity be, int capacity) {
      this.blockEntity = be;
      this.capacity = capacity;
   }

   public static void initDockingConnectorCapability(RegisterCapabilitiesEvent event) {
      if (ModList.get().isLoaded("simulated")) {
         BlockEntityType<?> dockType = (BlockEntityType) BuiltInRegistries.BLOCK_ENTITY_TYPE.get(
                 ResourceKey.create(Registries.BLOCK_ENTITY_TYPE,
                         ResourceLocation.fromNamespaceAndPath("simulated", "docking_connector")));
         if (dockType == null) {
            CreateKinetic.LOGGER.warn("Could not find docking_connector block entity type - capability registration skipped");
         } else {
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, dockType, (be, side) ->
                    ((DockingConnectorBEAccess) be).getEnergyStorage());
         }
      }
   }

   public void connect(BlockPos pos, DockEnergyStorage other) {
      this.connectedPos = pos;
      this.connectedStorage = other;
      this.isPrimary = this.blockEntity.getBlockPos().compareTo(pos) < 0;
      if (this.blockEntity.getLevel() != null && !this.blockEntity.getLevel().isClientSide()) {
         this.blockEntity.getLevel().invalidateCapabilities(this.blockEntity.getBlockPos());
      }

   }

   public void disconnect() {
      this.connectedPos = null;
      this.connectedStorage = null;
      this.isPrimary = false;
      if (this.blockEntity.getLevel() != null && !this.blockEntity.getLevel().isClientSide()) {
         this.blockEntity.getLevel().invalidateCapabilities(this.blockEntity.getBlockPos());
      }

   }

   public boolean isConnected() {
      if (this.connectedPos == null) {
         return false;
      } else if (this.blockEntity.getLevel() == null) {
         return false;
      } else if (this.blockEntity.getLevel().isClientSide()) {
         return false;
      } else {
         BlockEntity other = this.blockEntity.getLevel().getBlockEntity(this.connectedPos);
         if (other instanceof DockingConnectorBEAccess) {
            DockingConnectorBEAccess access = (DockingConnectorBEAccess)other;
            return access.getEnergyStorage().connectedStorage == this;
         } else {
            return false;
         }
      }
   }

   private DockEnergyStorage getStorage() {
      return this.isPrimary ? this : this.connectedStorage;
   }

   public int receiveEnergy(int maxReceive, boolean simulate) {
      if (!this.isConnected()) {
         return 0;
      } else {
         DockEnergyStorage storage = this.getStorage();
         int canAccept = storage.capacity - storage.energy;
         int toStore = Math.min(canAccept, maxReceive);
         if (!simulate) {
            storage.energy += toStore;
         }

         return toStore;
      }
   }

   public int extractEnergy(int maxExtract, boolean simulate) {
      if (!this.isConnected()) {
         return 0;
      } else {
         DockEnergyStorage storage = this.getStorage();
         int toExtract = Math.min(storage.energy, maxExtract);
         if (!simulate) {
            storage.energy -= toExtract;
         }

         return toExtract;
      }
   }

   public int getEnergyStored() {
      return !this.isConnected() ? 0 : this.getStorage().energy;
   }

   public int getMaxEnergyStored() {
      return !this.isConnected() ? 0 : this.getStorage().capacity;
   }

   public boolean canExtract() {
      return this.isConnected();
   }

   public boolean canReceive() {
      return this.isConnected();
   }

   public DockEnergyStorage getConnectedStorage() {
      return this.connectedStorage;
   }
}
