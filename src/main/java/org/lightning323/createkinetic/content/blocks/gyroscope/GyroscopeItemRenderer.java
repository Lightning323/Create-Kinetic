package org.lightning323.createkinetic.content.blocks.gyroscope;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import org.lightning323.createkinetic.registry.KineticPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class GyroscopeItemRenderer extends CustomRenderedItemModelRenderer {
   private static final float FLYWHEEL_LIFT = 0.375F;
   private static final float SHAFT_DROP = 0.0F;

   protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
      renderer.render(model.getOriginalModel(), light);
      ms.pushPose();
      ms.translate(0.0F, 0.375F, 0.0F);
      renderer.render(KineticPartialModels.GYROSCOPE_FLYWHEEL.get(), light);
      ms.popPose();
      ms.pushPose();
      ms.translate(0.0F, -0.0F, 0.0F);
      ms.mulPose(Axis.XP.rotationDegrees(90.0F));
      renderer.render(AllPartialModels.SHAFT_HALF.get(), light);
      ms.popPose();
   }
}
