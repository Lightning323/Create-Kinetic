package com.lightning323.createkinetic.blocks.helm

import net.minecraft.util.StringRepresentable

enum class WoodTypeSR(val id: String) : StringRepresentable {
    OAK("oak"),
    SPRUCE("spruce");

    override fun getSerializedName(): String = id
}