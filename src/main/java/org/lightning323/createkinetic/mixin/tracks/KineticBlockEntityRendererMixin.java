/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
 *  dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlockEntity
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.lightning323.createkinetic.mixin.tracks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlockEntity;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lightning323.createkinetic.CreateKinetic.trackHiddenTag;

@Mixin(value = {KineticBlockEntityRenderer.class})
public abstract class KineticBlockEntityRendererMixin {
    @Inject(method = {"renderRotatingBuffer(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;Lnet/createmod/catnip/render/SuperByteBuffer;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;I)V"}, at = {@At(value = "HEAD")}, cancellable = true, require = 0)
    private static void kinetic$skipHiddenWheelMountShaft(KineticBlockEntity be, SuperByteBuffer superByteBuffer, PoseStack poseStack, VertexConsumer vertexConsumer, int light, CallbackInfo ci) {
        WheelMountBlockEntity wheelMount;
        if (be instanceof WheelMountBlockEntity && KineticBlockEntityRendererMixin.kinetic$isMountHidden((wheelMount = (WheelMountBlockEntity) be).getBlockState())) {
            ci.cancel();
        }
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

