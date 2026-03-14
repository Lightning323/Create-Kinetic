package com.lightning323.createkinetic.blocks.helm

import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.block.Block

public interface IWoodType : StringRepresentable {

    fun getWood(): Block

    fun getPlanks(): Block
}