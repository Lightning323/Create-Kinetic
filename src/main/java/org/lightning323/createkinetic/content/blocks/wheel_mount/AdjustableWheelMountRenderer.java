/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  com.simibubi.create.AllPartialModels
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlockEntity
 *  dev.ryanhcode.offroad.content.components.TireLike
 *  dev.ryanhcode.offroad.index.OffroadDataComponents
 *  dev.ryanhcode.offroad.index.OffroadPartialModels
 *  dev.simulated_team.simulated.util.SimColors
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector2d
 */
package org.lightning323.createkinetic.content.blocks.wheel_mount;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import org.lightning323.createkinetic.mixin_interface.WheelMountOffsetAccess;
import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlockEntity;
import dev.ryanhcode.offroad.content.components.TireLike;
import dev.ryanhcode.offroad.index.OffroadDataComponents;
import dev.ryanhcode.offroad.index.OffroadPartialModels;
import dev.simulated_team.simulated.util.SimColors;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2d;

public class AdjustableWheelMountRenderer
extends KineticBlockEntityRenderer<WheelMountBlockEntity> {
    public AdjustableWheelMountRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(WheelMountBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState state = this.getRenderedBlockState(be);
        RenderType type = this.getRenderType(be, state);
        AdjustableWheelMountRenderer.renderRotatingBuffer(be, this.getRotatedModel(be, state), ms, buffer.getBuffer(type), light);
        FilteringRenderer.renderOnBlockEntity((SmartBlockEntity)be, (float)partialTicks, (PoseStack)ms, (MultiBufferSource)buffer, (int)light, (int)overlay);
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
        Direction direction = ((Direction)be.getBlockState().getValue((Property)BlockStateProperties.HORIZONTAL_FACING)).getOpposite();
        BlockState blockState = be.getBlockState();
        WheelMountOffsetAccess offsetAccess = (WheelMountOffsetAccess)be;
        boolean hideSuspension = offsetAccess.kinetic$isVisualSuspensionHidden();
        SuperByteBuffer diodeLeft = CachedBuffers.partial((PartialModel)OffroadPartialModels.DIODE_LEFT, (BlockState)blockState);
        SuperByteBuffer diodeRight = CachedBuffers.partial((PartialModel)OffroadPartialModels.DIODE_RIGHT, (BlockState)blockState);
        SuperByteBuffer teleOuter = CachedBuffers.partial((PartialModel)OffroadPartialModels.TELE_OUTER, (BlockState)blockState);
        SuperByteBuffer teleInner = CachedBuffers.partial((PartialModel)OffroadPartialModels.TELE_INNER, (BlockState)blockState);
        SuperByteBuffer teleMount = CachedBuffers.partial((PartialModel)OffroadPartialModels.TELE_MOUNT, (BlockState)blockState);
        SuperByteBuffer springTop = CachedBuffers.partial((PartialModel)OffroadPartialModels.SPRING_UPPER, (BlockState)blockState);
        SuperByteBuffer springBottom = CachedBuffers.partial((PartialModel)OffroadPartialModels.SPRING_LOWER, (BlockState)blockState);
        SuperByteBuffer springMiddle = CachedBuffers.partial((PartialModel)OffroadPartialModels.SPRING_MIDDLE, (BlockState)blockState);
        double wheelPivotOffsetHor = 0.625;
        double springWheelPivotOffsetHor = 0.75;
        double springWheelPivotOffsetVer = -0.125;
        double horizontalWheelPosition = 1.375;
        double verticalWheelPosition = -be.getLerpedExtension(partialTicks) + offsetAccess.kinetic$getLerpedHeightOffset(partialTicks);
        double lateralWheelPosition = offsetAccess.kinetic$getLerpedLateralOffset(partialTicks);
        double longitudinalWheelPosition = offsetAccess.kinetic$getLerpedLongitudinalOffset(partialTicks);
        double teleMountHor = 0.0;
        double teleMountVer = -0.375;
        double springMountHor = 0.4375;
        double springMountVer = 0.4375;
        double teleAngle = Math.atan2(verticalWheelPosition - -0.375, 0.75);
        double teleDistance = new Vector2d(verticalWheelPosition - -0.375, 0.75).length();
        double springAngle = Math.atan2(verticalWheelPosition - -0.125 - 0.4375, 0.1875);
        double springDistance = new Vector2d(verticalWheelPosition - -0.125 - 0.4375, 0.1875).length();
        ms.pushPose();
        ((PoseTransformStack)((PoseTransformStack)((PoseTransformStack)TransformStack.of((PoseStack)ms).center()).rotateYDegrees(AngleHelper.horizontalAngle((Direction)direction))).rotateXDegrees(AngleHelper.verticalAngle((Direction)direction))).uncenter();
        ms.pushPose();
        if (!hideSuspension) {
            ms.pushPose();
            ms.translate(lateralWheelPosition, -0.375, longitudinalWheelPosition);
            ms.translate(0.5, 0.5, 0.5);
            ms.mulPose(Axis.XP.rotation((float)teleAngle));
            ms.translate(-0.5, -0.5, -0.5);
            teleOuter.light(light).renderInto(ms, vb);
            ms.translate(0.0, 0.0, -(teleDistance - 1.0));
            teleInner.light(light).renderInto(ms, vb);
            ms.popPose();
        }
        ms.pushPose();
        ms.translate(lateralWheelPosition, verticalWheelPosition, longitudinalWheelPosition + 1.625 - 1.375);
        ms.translate(0.5, 0.5, 0.5);
        ms.rotateAround(Axis.YP.rotation((float)offsetAccess.kinetic$getLerpedYaw(partialTicks)), 0.0f, 0.0f, -1.0f);
        ms.translate(-0.5, -0.5, -0.5);
        if (!hideSuspension) {
            teleMount.light(light).renderInto(ms, vb);
        }
        ms.translate(0.5, 0.5, 0.5);
        ms.translate(0.0, 0.0, -1.625);
        double signMultiplier = (double)(-be.getLerpedAngle(partialTicks)) * (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 : -1.0) * (direction.getAxis() == Direction.Axis.X ? 1.0 : -1.0);
        ms.mulPose(Axis.ZP.rotation((float)signMultiplier));
        ItemStack itemStack = be.getHeldItem();
        TireLike tireLike = (TireLike)itemStack.get(OffroadDataComponents.TIRE);
        if (tireLike != null) {
            Vec3 rotation = tireLike.rotation();
            ms.mulPose(Axis.XP.rotation((float)Math.toRadians(rotation.x)));
            ms.mulPose(Axis.YP.rotation((float)Math.toRadians(rotation.y)));
            ms.mulPose(Axis.ZP.rotation((float)Math.toRadians(rotation.z)));
            if (tireLike.model().isPresent()) {
                ResourceLocation model = (ResourceLocation)tireLike.model().get();
                ms.translate(tireLike.offset().x, tireLike.offset().y, tireLike.offset().z);
                SuperByteBuffer wheel = CachedBuffers.partial((PartialModel)PartialModel.of((ResourceLocation)model), (BlockState)state);
                ((SuperByteBuffer)wheel.light(light).translate(-0.5f, 0.0f, -0.5f)).renderInto(ms, vb);
            } else {
                ms.translate(tireLike.offset().x, tireLike.offset().y, tireLike.offset().z);
                Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.NONE, light, overlay, ms, buffer, be.getLevel(), 0);
            }
        }
        ms.popPose();
        ms.popPose();
        if (!hideSuspension) {
            ms.pushPose();
            ms.translate(0.5 + lateralWheelPosition, 0.9375, 0.5 + longitudinalWheelPosition - 0.4375);
            ms.mulPose(Axis.XP.rotation((float)springAngle + 1.5707964f));
            ms.translate(-0.5 - lateralWheelPosition, -0.9375, -0.5 - longitudinalWheelPosition + 0.4375);
            float springExtension = (float)springDistance;
            float springSpan = springExtension - 0.25f;
            springTop.light(light).renderInto(ms, vb);
            ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)springMiddle.light(light).translate(0.0f, 0.8125f, 0.0f)).scale(1.0f, springSpan / 0.875f, 1.0f)).translateBack(0.0f, 0.8125f, 0.0f)).renderInto(ms, vb);
            ((SuperByteBuffer)springBottom.light(light).translate(0.0, -((double)springSpan + -0.875), 0.0)).renderInto(ms, vb);
            ms.popPose();
        }
        diodeLeft.light(light).color(SimColors.redstone((float)((float)offsetAccess.kinetic$getClientSteeringSignalLeft() / 15.0f))).renderInto(ms, vb);
        diodeRight.light(light).color(SimColors.redstone((float)((float)offsetAccess.kinetic$getClientSteeringSignalRight() / 15.0f))).renderInto(ms, vb);
        ms.popPose();
    }

    public int getViewDistance() {
        return 512;
    }

    protected SuperByteBuffer getRotatedModel(WheelMountBlockEntity te, BlockState state) {
        return CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT, (BlockState)te.getBlockState(), (Direction)((Direction)te.getBlockState().getValue((Property)BlockStateProperties.HORIZONTAL_FACING)).getOpposite());
    }
}

