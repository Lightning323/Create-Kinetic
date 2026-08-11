package org.lightning323.createkinetic.mixin.simulated.dockingConnector;

import dev.simulated_team.simulated.content.blocks.docking_connector.DockingConnectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.lightning323.createkinetic.compat.simulated.DockEnergyStorage;
import org.lightning323.createkinetic.compat.simulated.DockingConnectorBEAccess;
import org.lightning323.createkinetic.config.KineticConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({DockingConnectorBlockEntity.class})
public abstract class DockingConnectorBEMixin implements DockingConnectorBEAccess {
    @Unique
    private DockEnergyStorage energyStorage;
    @Shadow
    public BlockPos otherConnectorPosition;

    public BlockPos getOtherConnectorPosition() {
        return this.otherConnectorPosition;
    }

    @Inject(
            method = {"<init>"},
            at = {@At("TAIL")},
            remap = false
    )
    private void onInit(BlockEntityType<?> type, BlockPos pos, BlockState state, CallbackInfo ci) {
        DockingConnectorBEAccess self = ((DockingConnectorBEAccess) this);
        this.energyStorage = new DockEnergyStorage((BlockEntity) self, KineticConfig.DOCK_ENERGY_CAPACITY.getAsInt());
    }

    @Inject(
            method = {"setDock"},
            at = {@At(
                    value = "INVOKE",
                    target = "Ldev/simulated_team/simulated/content/blocks/docking_connector/DockingConnectorTank;connect(Lnet/minecraft/core/BlockPos;Ldev/simulated_team/simulated/content/blocks/docking_connector/DockingConnectorTank;)V"
            )},
            remap = false
    )
    private void onConnect(CallbackInfo ci) {
        BlockPos otherPos = ((DockingConnectorBEAccess) this).getOtherConnectorPosition();
        if (otherPos != null) {
            Level level = ((DockingConnectorBEAccess) this).getLevel();
            if (level != null) {
                BlockEntity other = level.getBlockEntity(otherPos);
                if (other instanceof DockingConnectorBEAccess) {
                    DockingConnectorBEAccess otherAccess = (DockingConnectorBEAccess) other;
                    this.energyStorage.connect(otherPos, otherAccess.getEnergyStorage());
                }

            }
        }
    }

    @Inject(
            method = {"unDock"},
            at = {@At(
                    value = "INVOKE",
                    target = "Ldev/simulated_team/simulated/content/blocks/docking_connector/DockingConnectorTank;disconnect()V"
            )},
            remap = false
    )
    private void onDisconnect(CallbackInfo ci) {
        this.energyStorage.disconnect();
    }

    public DockEnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }
}
