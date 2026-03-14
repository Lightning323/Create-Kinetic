package com.lightning323.createkinetic.ship

import net.minecraft.core.Direction
import org.valkyrienskies.mod.api.SeatedControllingPlayer

 data class ControlData(
    val seatInDirection: Direction,
    var forwardImpulse: Float = 0.0f,
    var leftImpulse: Float = 0.0f,
    var upImpulse: Float = 0.0f,
    var sprintOn: Boolean = false
) {
    companion object {
        fun create(player: SeatedControllingPlayer): ControlData {
            return ControlData(
                player.seatInDirection,
                player.forwardImpulse,
                player.leftImpulse,
                player.upImpulse,
                player.sprintOn
            )
        }
    }
}