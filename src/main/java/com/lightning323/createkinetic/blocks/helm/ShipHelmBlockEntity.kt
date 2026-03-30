package com.lightning323.createkinetic.blocks.helm

import com.lightning323.createkinetic.CreateKinetic
import com.lightning323.createkinetic.ship.KineticShipControl
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction.Axis
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.block.state.properties.Half
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.util.GameTickOnly
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.entity.ShipMountingEntity
import org.valkyrienskies.mod.common.getLoadedShipManagingPos
import org.valkyrienskies.mod.common.util.toDoubles


class ShipHelmBlockEntity(
    type: BlockEntityType<*>, // Add this
    pos: BlockPos,
    state: BlockState
) : KineticBlockEntity(type, pos, state) {

    //For the renderer
    var smoothedHelmRotation = 0.0

    @OptIn(GameTickOnly::class)
    private val ship: LoadedServerShip? get() = (level as ServerLevel).getLoadedShipManagingPos(this.blockPos)

    @OptIn(GameTickOnly::class, VsBeta::class)
    private val control: KineticShipControl? get() = ship?.getAttachment(KineticShipControl::class.java)
    private val seats = mutableListOf<ShipMountingEntity>()

    @OptIn(GameTickOnly::class)
    val assembled get() = ship != null
//    val aligning get() = control?.aligning == true

    // Needs to get called server-side
    fun spawnSeat(blockPos: BlockPos, state: BlockState, level: ServerLevel): ShipMountingEntity {
        val newPos = blockPos.relative(state.getValue(HorizontalDirectionalBlock.FACING))
        val newState = level.getBlockState(newPos)
        var height = 0.0
        if (!newState.isAir) {
            height = if (
                newState.block is StairBlock &&
                (!newState.hasProperty(StairBlock.HALF) || newState.getValue(StairBlock.HALF) == Half.BOTTOM)
            )
                0.5 // Valid StairBlock
            else
                newState.getShape(level, newPos).max(Axis.Y)
        } else {
            val stateBelow = level.getBlockState(BlockPos(newPos.x, newPos.y - 1, newPos.z))

            // If block below expected seat is valid slab or stair, move seat down one block
            val shapeHeight = stateBelow.getShape(level, newPos).max(Axis.Y)
            // if block is slab or higher
            if (shapeHeight >= 0.5 && shapeHeight < 1.0) {
                height = shapeHeight - 1.0
            }
        }

        val entity = ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE.create(level)!!.apply {

            val offset =
                if (height > 0.15)
                // when seated, place player 0.1m closer to helm
                    state.getValue(HorizontalDirectionalBlock.FACING).normal.toDoubles().scale(-0.1)
                        .add(.5, height - .5, .5)
                else
                    Vec3(.5, height + 0.1, .5)

            val seatEntityPos: Vector3dc = Vector3d(newPos.x + offset.x, newPos.y + offset.y, newPos.z + offset.z)
            moveTo(seatEntityPos.x(), seatEntityPos.y(), seatEntityPos.z())

            lookAt(
                EntityAnchorArgument.Anchor.EYES,
                state.getValue(HORIZONTAL_FACING).normal.toDoubles().add(position())
            )

            isController = true
        }

        level.addFreshEntityWithPassengers(entity)
        return entity
    }

    fun startRiding(
        player: Player,
        force: Boolean,
        blockPos: BlockPos,
        state: BlockState,
        level: ServerLevel
    ): Boolean {
        for (i in seats.size - 1 downTo 0) {
            if (!seats[i].isVehicle) {
                seats[i].kill()
                seats.removeAt(i)
            } else if (!seats[i].isAlive) {
                seats.removeAt(i)
            }
        }

        val seat = spawnSeat(blockPos, blockState, level)
        val ride = player.startRiding(seat, force)

        if (ride) {
            control?.seatedPlayer = player
            seats.add(seat)
        }

        return ride
    }

    @OptIn(VsBeta::class, GameTickOnly::class)
    override fun tick() {
        control?.ship = ship
        super.tick()
    }

    override fun remove() {
        if (level?.isClientSide == false) {
            for (i in seats.indices) {
                seats[i].kill()
            }
            seats.clear()
        }

        super.remove()
    }

    fun sit(player: Player, force: Boolean = false): Boolean {

        val seat = spawnSeat(blockPos, blockState, level as ServerLevel)

        //When we sit in a helm, set the preferred direction to the direction the helm is facing
        var direction = blockState.getValue(HORIZONTAL_FACING);
        CreateKinetic.LOGGER.debug("Helm seating direction: {}", direction)
        control?.preferredDirection = direction
        control?.updateShipDirection()

        control?.seatedPlayer = player
        return player.startRiding(seat, force)
        return startRiding(player, force, blockPos, blockState, level as ServerLevel)
    }

}
