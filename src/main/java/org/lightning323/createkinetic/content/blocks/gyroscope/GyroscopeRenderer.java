package org.lightning323.createkinetic.content.blocks.gyroscope;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.lightning323.createkinetic.registry.KineticPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import org.joml.Quaternionf;

public class GyroscopeRenderer extends SafeBlockEntityRenderer<GyroscopeBlockEntity> {
   private static final float FLYWHEEL_LIFT = 0.61875F;
   private static final float FLYWHEEL_HEIGHT = 0.375F;
   private static final float FLYWHEEL_LIFT_INVERTED = 0.006250024F;
   private static final float SHAFT_DROP = 0.0F;
   private static final int REDSTONE_OFF = -11140863;
   private static final int REDSTONE_ON = -3342336;


   public GyroscopeRenderer(BlockEntityRendererProvider.Context context) {
   }

   protected void renderSafe(GyroscopeBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
      renderRedstoneStrips(be, partialTicks, ms, buffer, light);
      if (!VisualizationManager.supportsVisualization(be.getLevel())) {
         VertexConsumer vb = buffer.getBuffer(RenderType.cutout());
         float flywheelSpeed = be.effectiveSpeedAt(partialTicks) * 3.0F / 10.0F;
         float flywheelAngle = be.getAngle() + flywheelSpeed * partialTicks;
         float shaftAngle = (float)Math.toDegrees((double)KineticBlockEntityRenderer.getAngleForBe(be, be.getBlockPos(), Axis.Y));
         ClientSubLevel subLevel = Sable.HELPER.getContainingClient(be);
         Quaternionf comp = subLevel != null ? (new Quaternionf(subLevel.renderPose(partialTicks).orientation())).invert() : null;
         boolean inverted = be.getBlockState().getValue(GyroscopeBlock.FACING) == Direction.UP;
         float flywheelLift = inverted ? 0.006250024F : 0.61875F;
         float shaftXDeg = inverted ? -90.0F : 90.0F;
         float shaftAngleSigned = inverted ? shaftAngle : -shaftAngle;
         ms.pushPose();
         if (comp != null) {
            ms.translate(0.5F, 0.5F, 0.5F);
            ms.mulPose(comp);
            ms.translate(-0.5F, -0.5F, -0.5F);
         }

         ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial(KineticPartialModels.GYROSCOPE_FLYWHEEL, be.getBlockState()).translate(0.5F, 0.5F + flywheelLift, 0.5F)).rotateYDegrees(flywheelAngle)).translate(-0.5F, -0.5F, -0.5F)).light(light).renderInto(ms, vb);
         ms.popPose();
         ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial(AllPartialModels.SHAFT_HALF, be.getBlockState()).translate(0.5F, 0.5F, 0.5F)).rotateXDegrees(shaftXDeg)).rotateZDegrees(shaftAngleSigned)).translate(-0.5F, -0.5F, -0.5F)).light(light).renderInto(ms, vb);
      }
   }

   private static void renderRedstoneStrips(GyroscopeBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light) {
      boolean inverted = be.getBlockState().getValue(GyroscopeBlock.FACING) == Direction.UP;
      VertexConsumer vb = buffer.getBuffer(RenderType.cutout());
      ms.pushPose();
      if (inverted) {
         ms.translate(0.5F, 0.5F, 0.5F);
         ms.mulPose((new Quaternionf()).rotateX((float)Math.PI));
         ms.translate(-0.5F, -0.5F, -0.5F);
      }

      CachedBuffers.partial(KineticPartialModels.GYROSCOPE_INDICATOR, be.getBlockState()).color(redstoneTint(be.indicatorZAt(partialTicks))).light(light).renderInto(ms, vb);
      ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial(KineticPartialModels.GYROSCOPE_INDICATOR, be.getBlockState()).translate(0.5F, 0.0F, 0.5F)).rotateYDegrees(90.0F)).translate(-0.5F, 0.0F, -0.5F)).color(redstoneTint(be.indicatorXAt(partialTicks))).light(light).renderInto(ms, vb);
      ms.popPose();
   }

   private static int redstoneTint(float fraction) {
      return Color.mixColors(-11140863, -3342336, fraction);
   }
}
