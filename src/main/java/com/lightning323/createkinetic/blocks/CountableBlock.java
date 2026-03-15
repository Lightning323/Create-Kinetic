package com.lightning323.createkinetic.blocks;

import com.lightning323.createkinetic.ship.KineticShipControl;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

import java.util.List;
import java.util.Map;

public abstract class CountableBlock extends Block {
    public CountableBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClientSide) {
            return;
        }
        if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
            ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, pos);
            if (ship != null) {
                KineticShipControl controller = KineticShipControl.getOrCreate((LoadedServerShip) ship, (ServerLevel) world);
                addToShip(controller);
            } else {
                ship = VSGameUtilsKt.getShipManagingPos((ServerLevel) world, pos);
                if (ship instanceof LoadedServerShip) {
                    KineticShipControl controller = KineticShipControl.getOrCreate((LoadedServerShip) ship, (ServerLevel) world);
                    addToShip(controller);
                }
            }
        }
    }


    abstract void addToShip(KineticShipControl controller);

    abstract void removeFromShip(KineticShipControl controller);


    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (world.isClientSide) {
            return;
        }
        if (newState.isAir() || !newState.is(state.getBlock())) {
            if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
                LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerLevel) world, pos);
                assert ship != null;
                KineticShipControl controller = ship.getAttachment(KineticShipControl.class);
                assert controller != null;
                removeFromShip(controller);
            }
        }
    }
}
