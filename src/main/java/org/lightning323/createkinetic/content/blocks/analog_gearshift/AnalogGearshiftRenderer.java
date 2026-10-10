package org.lightning323.createkinetic.content.blocks.analog_gearshift;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import com.simibubi.create.content.kinetics.transmission.SplitShaftRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import org.lightning323.createkinetic.registries.KineticPartialModels;

public class AnalogGearshiftRenderer extends SplitShaftRenderer {
    public AnalogGearshiftRenderer(Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(SplitShaftBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        BlockState blockState = be.getBlockState();
        Axis axis = ((IRotate) blockState.getBlock()).getRotationAxis(blockState);
        float time = AnimationTickHolder.getRenderTime(be.getLevel());
        float angle = time * be.getSpeed() * 3.0F / 10.0F % 360.0F;
        float shaftAngle = 0.0F;
        float modifier = 0.0F;
        float offset = 0.0F;
        if (be.hasSource()
            && !blockState.getValue(AnalogGearshiftBlock.LEFT_POWERED)
            && blockState.getValue(AnalogGearshiftBlock.RIGHT_POWERED)) {
            shaftAngle = angle;
        }

        if (be.hasSource()
            && blockState.getValue(AnalogGearshiftBlock.LEFT_POWERED)
            && !blockState.getValue(AnalogGearshiftBlock.RIGHT_POWERED)) {
            modifier = be.getRotationSpeedModifier(be.getSourceFacing().getOpposite());
            offset = getRotationOffsetForPosition(be, be.getBlockPos(), axis);
        }

        angle *= modifier;
        angle += offset;
        angle = angle / 180.0F * (float) Math.PI;
        shaftAngle = shaftAngle / 180.0F * (float) Math.PI;
        Direction direction = blockState.getValue(AnalogGearshiftBlock.FACING);
        boolean vertical = axis.isVertical()
            || direction.getAxis().isVertical() && !blockState.getValue(AnalogGearshiftBlock.AXIS_ALONG_FIRST_COORDINATE);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.solid());
        SuperByteBuffer barrel = CachedBuffers.partial(KineticPartialModels.ANALOG_GEARSHIFT_CENTER, blockState);
        SuperByteBuffer barrelShaftA = CachedBuffers.partial(KineticPartialModels.ANALOG_GEARSHIFT_BARREL_SHAFT, blockState);
        kineticRotationTransform(barrelShaftA, be, axis, angle, light);
        barrelShaftA.center().rotateToFace(direction).uncenter();
        if (vertical) {
            barrelShaftA.rotateZCenteredDegrees(90.0F);
        }

        barrelShaftA.rotateZCentered((float) Math.PI);
        barrelShaftA.rotateYCentered(shaftAngle);
        barrelShaftA.light(light).renderInto(ms, consumer);
        SuperByteBuffer barrelShaftB = CachedBuffers.partial(KineticPartialModels.ANALOG_GEARSHIFT_BARREL_SHAFT, blockState);
        kineticRotationTransform(barrelShaftB, be, axis, angle, light);
        barrelShaftB.center().rotateToFace(direction).uncenter();
        if (vertical) {
            barrelShaftB.rotateZCenteredDegrees(90.0F);
        }

        barrelShaftB.rotateYCentered(shaftAngle);
        barrelShaftB.light(light).renderInto(ms, consumer);
        kineticRotationTransform(barrel, be, axis, angle, light);
        barrel.center().rotateToFace(direction).uncenter();
        if (vertical) {
            barrel.rotateZCenteredDegrees(90.0F);
        }

        barrel.light(light).renderInto(ms, consumer);
        super.renderSafe(be, partialTicks, ms, bufferSource, light, overlay);
    }
}