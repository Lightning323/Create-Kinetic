package com.lightning323.createkinetic.blocks.helm

import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.block.state.properties.WoodType

enum class WoodTypeEnum(val id: String) : StringRepresentable {
    OAK("oak"),
    SPRUCE("spruce"),
    BIRCH("birch"),
    JUNGLE("jungle"),
    ACACIA("acacia"),
    DARK_OAK("dark_oak"),
    CHERRY("cherry"),
    CRIMSON("crimson"),
    WARPED("warped"),
    MANGROVE("mangrove"),
    BAMBOO("bamboo");

    override fun getSerializedName(): String = id

    companion object {
        // Cache the mapping at startup for O(1) lookup speed
        private val BY_NAME = values().associateBy { it.id }

        /**
         * Converts a vanilla Minecraft WoodType to this Enum.
         * Falls back to OAK if no match is found.
         */
        @JvmStatic
        fun fromVanilla(woodType: WoodType): WoodTypeEnum {
            // vanilla WoodType.name() returns strings like "oak", "spruce", etc.
            return BY_NAME[woodType.name()] ?: OAK
        }
    }
}