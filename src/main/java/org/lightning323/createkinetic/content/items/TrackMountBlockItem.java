/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.block.Block
 */
package org.lightning323.createkinetic.content.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

public class TrackMountBlockItem
extends BlockItem {
    public TrackMountBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public InteractionResult useOn(UseOnContext context) {
        if (context.getClickedFace() != Direction.UP) {
            return super.useOn(context);
        }
        BlockPos raisedPos = context.getClickedPos().relative(context.getClickedFace()).above();
        BlockPlaceContext raisedContext = BlockPlaceContext.at((BlockPlaceContext)new BlockPlaceContext(context), (BlockPos)raisedPos, (Direction)Direction.UP);
        return this.place(raisedContext);
    }
}

