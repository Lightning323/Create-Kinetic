package com.lightning323.createkinetic.blocks.helm

import com.lightning323.createkinetic.CreateKinetic
import com.lightning323.createkinetic.CreateKinetic.MOD_ID
import com.lightning323.createkinetic.registries.KineticBlockEntities
import com.lightning323.createkinetic.ship.ShipUtils
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.block.state.properties.WoodType
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.util.GameTickOnly
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.getLoadedShipManagingPos
import org.valkyrienskies.mod.common.getShipManagingPos

class ShipHelmBlock(properties: Properties, val woodType: WoodType) : BaseEntityBlock(properties) {
    //    val HELM_BASE = RotShapes.box(2.0, 0.0, 2.0, 14.0, 2.0, 14.0)
//    val HELM_POLE = RotShapes.box(4.0, 2.0, 5.0, 12.0, 13.0, 13.0)
    val SIMPLE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0)

    //    val HELM_SHAPE = DirectionalShape(RotShapes.or(HELM_BASE, HELM_POLE))
    private val woodTypeEnum: WoodTypeEnum

    fun getWoodTypeEnum(): WoodTypeEnum {
        return this.woodTypeEnum
    }

    init {
        registerDefaultState(this.stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH))
        woodTypeEnum = WoodTypeEnum.fromVanilla(woodType)
    }

    override fun newBlockEntity(blockPos: BlockPos, state: BlockState): BlockEntity {
        return KineticBlockEntities.SHIP_HELM.create(blockPos, state)
    }

    @OptIn(GameTickOnly::class)
    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        val ship = level.getLoadedShipManagingPos(pos) ?: level.getShipManagingPos(pos) ?: return
        val it = ShipUtils.getOrAddShipController(level, pos);
        if (it != null) {
            //When we set the helm, set the preferred direction to the direction the helm is facing
            val direction = state.getValue(HORIZONTAL_FACING);
//            CreateKinetic.LOGGER.debug("Helm preffered direction: {}", direction)
            it.preferredDirection = direction
            it.helms += 1;
            it.updateShipDirection()
        }
//        KineticShipControl.deferUntilLoaded(ship) { it.helms += 1 }
    }

    @OptIn(GameTickOnly::class, VsBeta::class)
    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        val ship = level.getLoadedShipManagingPos(pos) ?: level.getShipManagingPos(pos) ?: return

        val it = ShipUtils.getShipController(level, pos);
        if (it != null) {
            if (it.helms <= 1 && it.seatedPlayer?.vehicle?.type == ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE) {
                it.seatedPlayer!!.unRide()
                it.seatedPlayer = null
            }
            it.helms -= 1
        }
//        KineticShipControl.deferUntilLoaded(ship) {
//            if (it.helms <= 1 && it.seatedPlayer?.vehicle?.type == ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE) {
//                it.seatedPlayer!!.unRide()
//                it.seatedPlayer = null
//            }
//            it.helms -= 1
//        }
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS
        val blockEntity = level.getBlockEntity(pos) as ShipHelmBlockEntity

        return if (level.getShipManagingPos(pos) == null) {
            player.displayClientMessage(Component.translatable("info."+MOD_ID+".needs_ship"), true)
            InteractionResult.CONSUME
        } else if (blockEntity.sit(player)) {
            InteractionResult.CONSUME
        } else InteractionResult.PASS
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        return defaultBlockState()
            .setValue(HORIZONTAL_FACING, ctx.horizontalDirection.opposite)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(HORIZONTAL_FACING)
    }


    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape {
        return SIMPLE_SHAPE;
//        return HELM_SHAPE[blockState.getValue(HORIZONTAL_FACING)]
    }

    //
    override fun useShapeForLightOcclusion(blockState: BlockState): Boolean {
        return true
    }

    override fun isPathfindable(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        pathComputationType: PathComputationType
    ): Boolean {
        return false
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState? {
        return state.setValue(
            HORIZONTAL_FACING,
            rotation.rotate(state.getValue(HORIZONTAL_FACING) as Direction)
        ) as BlockState
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> = BlockEntityTicker { level, pos, state, blockEntity ->
        if (level.isClientSide) return@BlockEntityTicker
        if (blockEntity is ShipHelmBlockEntity) {
            blockEntity.tick()
        }
    }


}
