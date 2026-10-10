package org.lightning323.createkinetic.mixin.client;

import org.lightning323.createkinetic.content.blocks.joystick.JoystickBlockEntity;
import org.lightning323.createkinetic.content.blocks.joystick.JoystickClientRaycast;
import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public class GameRendererMixin {
   @Shadow
   @Final
   private Minecraft minecraft;

   @Inject(
      method = {"pick(F)V"},
      at = {@At("TAIL")}
   )
   private void createkinetic$pickJoystick(float partialTicks, CallbackInfo ci) {
      if (this.minecraft != null) {
         LocalPlayer player = this.minecraft.player;
         if (player != null) {
            Vec3 eyePos = Sable.HELPER.getEyePositionInterpolated(player, partialTicks);
            HitResult current = this.minecraft.hitResult;
            double minDistSq = current != null && current.getType() != Type.MISS ? Sable.HELPER.distanceSquaredWithSubLevels(player.level(), eyePos, current.getLocation()) : Double.MAX_VALUE;

            for(JoystickBlockEntity be : JoystickClientRaycast.getNearby()) {
               if (!be.isRemoved()) {
                  Double dSq = JoystickClientRaycast.raycastHandle(eyePos, player.getViewVector(partialTicks), be, partialTicks);
                  if (dSq != null && dSq < minDistSq) {
                     minDistSq = dSq;
                     this.minecraft.hitResult = JoystickClientRaycast.buildHitResult(be);
                  }
               }
            }

         }
      }
   }
}
