package org.lightning323.createkinetic.content.joystick;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

public final class JoystickClientRaycast {
   private static final float PIVOT_X = 0.5F;
   private static final float PIVOT_Y = 0.15625F;
   private static final float PIVOT_Z = 0.5F;
   private static final float BLOCK_CENTER = 0.5F;
   private static final float DEGREES_PER_STEP = 3.0F;
   private static final Set<JoystickBlockEntity> NEARBY = Collections.newSetFromMap(new ConcurrentHashMap());

   private JoystickClientRaycast() {
   }

   public static void tickGrip(JoystickBlockEntity be) {
      if (!isInvalid(be)) {
         NEARBY.add(be);
      }
   }

   public static void clearNearby() {
      NEARBY.removeIf(JoystickClientRaycast::isInvalid);
   }

   public static Collection<JoystickBlockEntity> getNearby() {
      return NEARBY;
   }

   private static boolean isInvalid(JoystickBlockEntity be) {
      if (be.isRemoved()) {
         return true;
      } else {
         LocalPlayer player = Minecraft.getInstance().player;
         if (player == null) {
            return true;
         } else {
            double reach = player.blockInteractionRange() + (double)2.0F;
            return player.distanceToSqr(be.getBlockPos().getCenter()) > reach * reach;
         }
      }
   }

   public static Double raycastHandle(Vec3 eyeWorld, Vec3 viewVector, JoystickBlockEntity be, float partialTicks) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null) {
         return null;
      } else {
         Vector3d eyePos = new Vector3d(eyeWorld.x, eyeWorld.y, eyeWorld.z);
         Vector3d viewDir = new Vector3d(viewVector.x, viewVector.y, viewVector.z);
         ClientSubLevel subLevel = Sable.HELPER.getContainingClient(be);
         if (subLevel != null) {
            Pose3dc pose = subLevel.renderPose(partialTicks);
            pose.transformPositionInverse(eyePos);
            pose.transformNormalInverse(viewDir);
         }

         BlockPos blockPos = be.getBlockPos();
         Direction facing = (Direction)be.getBlockState().getValue(JoystickBlock.FACING);
         float facingRad = (float)Math.toRadians((double)(-facing.toYRot()));
         float tx = be.tiltXAt(partialTicks);
         float ty = be.tiltYAt(partialTicks);
         float rotZ = (float)Math.toRadians((double)(-tx * 3.0F));
         float rotX = (float)Math.toRadians((double)(ty * 3.0F));
         PoseStack stack = new PoseStack();
         stack.translate((double)blockPos.getX() - eyePos.x, (double)blockPos.getY() - eyePos.y, (double)blockPos.getZ() - eyePos.z);
         stack.translate(0.5F, 0.0F, 0.5F);
         stack.mulPose((new Quaternionf()).rotateY(facingRad));
         stack.translate(-0.5F, 0.0F, -0.5F);
         stack.translate(0.5F, 0.15625F, 0.5F);
         stack.mulPose((new Quaternionf()).rotateZ(rotZ).rotateX(rotX));
         stack.translate(-0.5F, -0.15625F, -0.5F);
         Matrix4f inverse = (new Matrix4f(stack.last().pose())).invert();
         Vector3f localEye = inverse.transformPosition(new Vector3f());
         Vector3f localDir = inverse.transformDirection(new Vector3f((float)viewDir.x, (float)viewDir.y, (float)viewDir.z));
         double reach = player.blockInteractionRange();
         Vec3 start = new Vec3((double)localEye.x, (double)localEye.y, (double)localEye.z);
         Vec3 end = new Vec3((double)localEye.x + (double)localDir.x * reach, (double)localEye.y + (double)localDir.y * reach, (double)localEye.z + (double)localDir.z * reach);
         BlockHitResult hit = JoystickBlock.HANDLE_SHAPE.clip(start, end, BlockPos.ZERO);
         if (hit != null && hit.getType() != Type.MISS) {
            Vec3 loc = hit.getLocation();
            return start.distanceToSqr(loc);
         } else {
            return null;
         }
      }
   }

   public static BlockHitResult buildHitResult(JoystickBlockEntity be) {
      return new BlockHitResult(be.getBlockPos().getCenter(), Direction.UP, be.getBlockPos(), false);
   }
}
