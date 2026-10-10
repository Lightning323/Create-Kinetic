/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlockEntity
 *  dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountRenderer
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package org.lightning323.createkinetic.mixin.tracks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlockEntity;
import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountRenderer;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static org.lightning323.createkinetic.CreateKinetic.trackHiddenTag;


@Mixin(value = {WheelMountRenderer.class})
public abstract class WheelMountRendererMixin {
    @WrapOperation(method = {"renderSafe"}, at = {@At(value = "INVOKE", target = "Ldev/ryanhcode/offroad/content/blocks/wheel_mount/WheelMountRenderer;renderRotatingBuffer(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;Lnet/createmod/catnip/render/SuperByteBuffer;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;I)V")}, require = 0)
    private void kinetic$skipHiddenMountShaft(KineticBlockEntity be, SuperByteBuffer superByteBuffer, PoseStack poseStack, VertexConsumer vertexConsumer, int light, Operation<Void> original) {
        WheelMountBlockEntity wheelMount;
        if (be instanceof WheelMountBlockEntity && WheelMountRendererMixin.kinetic$isMountHidden((wheelMount = (WheelMountBlockEntity) be).getBlockState())) {
            return;
        }
        original.call(new Object[]{be, superByteBuffer, poseStack, vertexConsumer, light});
    }

    private static boolean kinetic$isMountHidden(BlockState state) {
        for (Property property : state.getProperties()) {
            BooleanProperty booleanProperty;
            if (!(property instanceof BooleanProperty) || !trackHiddenTag.equals((booleanProperty = (BooleanProperty) property).getName()))
                continue;
            return (Boolean) state.getValue((Property) booleanProperty);
        }
        return false;
    }
}

