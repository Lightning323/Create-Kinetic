package com.lightning323.createkinetic.blocks

import com.lightning323.createkinetic.registries.KineticShapes
import com.lightning323.createkinetic.ship.ShipUtils
import net.minecraft.world.level.block.HorizontalDirectionalBlock

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.util.GameTickOnly

class AnchorBlock(properties: BlockBehaviour.Properties) : HorizontalDirectionalBlock(properties){


    val ANCHOR_SHAPE = KineticShapes.cuboid(2.0, -1.0, 2.0, 14.0, 29.0, 14.0)

    init {
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING).add(BlockStateProperties.POWERED)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        return defaultBlockState()
            .setValue(FACING, ctx.horizontalDirection.opposite)
            .setValue(
                BlockStateProperties.POWERED,
                ctx.level.hasNeighborSignal(ctx.clickedPos)
            )
    }

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape {
        return ANCHOR_SHAPE;//[blockState.getValue(FACING)]
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        if (level.isClientSide) return
        level as ServerLevel

        val bl = level.hasNeighborSignal(pos)
        val prevBl = state.getValue(BlockStateProperties.POWERED)
        if (bl != prevBl)
            level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, bl), 11)

        super.neighborChanged(state, level, pos, block, fromPos, isMoving)
    }

    @OptIn(GameTickOnly::class)
    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        val bl = state.getValue(BlockStateProperties.POWERED)

        val controller = ShipUtils.getOrCreateShipController(level, pos);
        if(controller!=null) {
            controller.anchors += 1
            controller.anchorsActive += if (bl) 1 else 0
        }

    }

    @OptIn(VsBeta::class, GameTickOnly::class)
    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)
        if (level.isClientSide) return
        level as ServerLevel
        val bl = state.getValue(BlockStateProperties.POWERED)

        val controller = ShipUtils.getOrCreateShipController(level, pos);
        if(controller!=null) {
            controller.anchors -= 1
            controller.anchorsActive -= if (bl) 1 else 0
        }
    }
}