package org.lightning323.createkinetic.content.joystick;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.lightning323.createkinetic.KineticPartialModels;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaternionf;

public class JoystickRenderer extends SafeBlockEntityRenderer<JoystickBlockEntity> {
   private static final float PIVOT_X = 0.5F;
   private static final float PIVOT_Y = 0.15625F;
   private static final float PIVOT_Z = 0.5F;
   private static final float BLOCK_CENTER = 0.5F;
   private static final float DEGREES_PER_STEP = 3.0F;
   private static final float BUTTON_PRESS_DEPTH = 0.03125F;

   public JoystickRenderer(BlockEntityRendererProvider.Context context) {
   }

   public AABB getRenderBoundingBox(JoystickBlockEntity be) {
      BlockPos p = be.getBlockPos();
      return new AABB((double)(p.getX() - 1), (double)p.getY(), (double)(p.getZ() - 1), (double)(p.getX() + 2), (double)(p.getY() + 2), (double)(p.getZ() + 2));
   }

   protected void renderSafe(JoystickBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
      Direction facing = (Direction)be.getBlockState().getValue(JoystickBlock.FACING);
      float facingDeg = facing.toYRot();
      float tx = be.tiltXAt(partialTicks);
      float ty = be.tiltYAt(partialTicks);
      if (!VisualizationManager.supportsVisualization(be.getLevel())) {
         VertexConsumer vb = buffer.getBuffer(RenderType.cutout());
         float rotX = ty * 3.0F;
         float rotZ = -tx * 3.0F;
         ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial(KineticPartialModels.JOYSTICK_HANDLE, be.getBlockState()).translate(0.5F, 0.15625F, 0.5F)).rotateYDegrees(-facingDeg)).rotateZDegrees(rotZ)).rotateXDegrees(rotX)).translate(-0.5F, -0.15625F, -0.5F)).light(light).renderInto(ms, vb);
         float buttonDrop = -be.buttonPressAt(partialTicks) * 0.03125F;
         ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial(KineticPartialModels.JOYSTICK_BUTTON, be.getBlockState()).translate(0.5F, 0.15625F, 0.5F)).rotateYDegrees(-facingDeg)).rotateZDegrees(rotZ)).rotateXDegrees(rotX)).translate(-0.5F, -0.15625F + buttonDrop, -0.5F)).light(light).renderInto(ms, vb);
         int tiltX = be.getTiltX();
         int tiltY = be.getTiltY();

         for(JoystickDirection dir : JoystickDirection.VALUES) {
            float curved = JoystickVisual.brightnessCurve((float)dir.strengthFor(tiltX, tiltY) / 15.0F);
            int color = JoystickVisual.scaleBrightness(dir.colorRgb, curved);
            ((SuperByteBuffer)((SuperByteBuffer)((SuperByteBuffer)CachedBuffers.partial(KineticPartialModels.JOYSTICK_INDICATOR, be.getBlockState()).translate(0.5F, 0.0F, 0.5F)).rotateYDegrees(-facingDeg - (float)dir.index * 90.0F)).translate(-0.5F, 0.0F, -0.5F)).color(color).light(light).renderInto(ms, vb);
         }
      }

      Minecraft mc = Minecraft.getInstance();
      HitResult buttonDrop = mc.hitResult;
      if (buttonDrop instanceof BlockHitResult hit) {
         if (hit.getType() != Type.MISS && hit.getBlockPos().equals(be.getBlockPos())) {
            float buttonDrop2 = -be.buttonPressAt(partialTicks) * 0.03125F;
            renderHandleOutline(ms, buffer, facingDeg, tx, ty, buttonDrop2);
         }
      }

   }

   private static void renderHandleOutline(PoseStack ms, MultiBufferSource buffer, float facingDeg, float tx, float ty, float buttonDrop) {
      VertexConsumer lines = buffer.getBuffer(RenderType.lines());
      float rotXRad = (float)Math.toRadians((double)(ty * 3.0F));
      float rotZRad = (float)Math.toRadians((double)(-tx * 3.0F));
      float facingRad = (float)Math.toRadians((double)facingDeg);
      ms.pushPose();
      ms.translate(0.5F, 0.0F, 0.5F);
      ms.mulPose((new Quaternionf()).rotateY(-facingRad));
      ms.translate(-0.5F, 0.0F, -0.5F);
      ms.translate(0.5F, 0.15625F, 0.5F);
      ms.mulPose((new Quaternionf()).rotateZ(rotZRad).rotateX(rotXRad));
      ms.translate(-0.5F, -0.15625F, -0.5F);
      drawShapeLines(ms, lines, JoystickBlock.HANDLE_CORE_SHAPE, 0.0F, 0.0F, 0.0F, 0.4F);
      ms.pushPose();
      ms.translate(0.0F, buttonDrop, 0.0F);
      drawShapeLines(ms, lines, JoystickBlock.BUTTON_SHAPE, 0.0F, 0.0F, 0.0F, 0.4F);
      ms.popPose();
      ms.popPose();
   }

   private static void drawShapeLines(PoseStack ms, VertexConsumer vc, VoxelShape shape, float r, float g, float b, float a) {
      PoseStack.Pose pose = ms.last();
      shape.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
         float dx = (float)(x2 - x1);
         float dy = (float)(y2 - y1);
         float dz = (float)(z2 - z1);
         float len = Mth.sqrt(dx * dx + dy * dy + dz * dz);
         dx /= len;
         dy /= len;
         dz /= len;
         vc.addVertex(pose, (float)x1, (float)y1, (float)z1).setColor(r, g, b, a).setNormal(pose, dx, dy, dz);
         vc.addVertex(pose, (float)x2, (float)y2, (float)z2).setColor(r, g, b, a).setNormal(pose, dx, dy, dz);
      });
   }
}
