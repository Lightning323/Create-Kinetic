package org.lightning323.createkinetic.content.blocks.joystick;

import org.lightning323.createkinetic.registry.KineticPartialModels;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public class JoystickVisual extends AbstractBlockEntityVisual<JoystickBlockEntity> implements SimpleDynamicVisual {
   private static final float PIVOT_X = 0.5F;
   private static final float PIVOT_Y = 0.15625F;
   private static final float PIVOT_Z = 0.5F;
   private static final float DEGREES_PER_STEP = 3.0F;
   private static final float BLOCK_CENTER = 0.5F;
   private static final float BUTTON_PRESS_DEPTH = 0.03125F;
   private final TransformedInstance handle;
   private final TransformedInstance button;
   private final TransformedInstance[] indicators;

   public JoystickVisual(VisualizationContext ctx, JoystickBlockEntity be, float partialTick) {
      super(ctx, be, partialTick);
      this.indicators = new TransformedInstance[JoystickDirection.VALUES.length];
      this.handle = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(KineticPartialModels.JOYSTICK_HANDLE)).createInstance();
      this.button = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(KineticPartialModels.JOYSTICK_BUTTON)).createInstance();

      for(JoystickDirection dir : JoystickDirection.VALUES) {
         this.indicators[dir.index] = (TransformedInstance)this.instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(KineticPartialModels.JOYSTICK_INDICATOR)).createInstance();
      }

      this.applyTransforms(partialTick);
      this.relight(new FlatLit[]{this.handle});
      this.relight(new FlatLit[]{this.button});

      for(TransformedInstance ind : this.indicators) {
         this.relight(new FlatLit[]{ind});
      }

   }

   public void beginFrame(Context context) {
      this.applyTransforms(context.partialTick());
      this.applyIndicatorColors();
   }

   private void applyTransforms(float partialTick) {
      float tx = ((JoystickBlockEntity)this.blockEntity).tiltXAt(partialTick);
      float ty = ((JoystickBlockEntity)this.blockEntity).tiltYAt(partialTick);
      float rotX = (float)Math.toRadians((double)(ty * 3.0F));
      float rotZ = (float)Math.toRadians((double)(-tx * 3.0F));
      Direction facing = (Direction)((JoystickBlockEntity)this.blockEntity).getBlockState().getValue(JoystickBlock.FACING);
      float facingRad = (float)Math.toRadians((double)(-facing.toYRot()));
      ((TransformedInstance)this.handle.setIdentityTransform().translate(this.getVisualPosition())).translate(0.5F, 0.15625F, 0.5F).rotateY(facingRad).rotateZ(rotZ).rotateX(rotX).translate(-0.5F, -0.15625F, -0.5F).setChanged();
      float buttonDrop = -((JoystickBlockEntity)this.blockEntity).buttonPressAt(partialTick) * 0.03125F;
      ((TransformedInstance)this.button.setIdentityTransform().translate(this.getVisualPosition())).translate(0.5F, 0.15625F, 0.5F).rotateY(facingRad).rotateZ(rotZ).rotateX(rotX).translate(-0.5F, -0.15625F + buttonDrop, -0.5F).setChanged();

      for(JoystickDirection dir : JoystickDirection.VALUES) {
         float dirRad = (float)Math.toRadians((double)((float)(-dir.index) * 90.0F));
         ((TransformedInstance)this.indicators[dir.index].setIdentityTransform().translate(this.getVisualPosition())).translate(0.5F, 0.0F, 0.5F).rotateY(facingRad + dirRad).translate(-0.5F, 0.0F, -0.5F).setChanged();
      }

   }

   private void applyIndicatorColors() {
      int tiltX = ((JoystickBlockEntity)this.blockEntity).getTiltX();
      int tiltY = ((JoystickBlockEntity)this.blockEntity).getTiltY();

      for(JoystickDirection dir : JoystickDirection.VALUES) {
         float curved = brightnessCurve((float)dir.strengthFor(tiltX, tiltY) / 15.0F);
         this.indicators[dir.index].colorArgb(scaleBrightness(dir.colorRgb, curved)).setChanged();
      }

   }

   static float brightnessCurve(float brightness) {
      return brightness <= 0.0F ? 0.3F : 0.4F + 0.6F * brightness;
   }

   static int scaleBrightness(int rgb, float scale) {
      int r = (int)Math.min(255.0F, (float)(rgb >> 16 & 255) * scale);
      int g = (int)Math.min(255.0F, (float)(rgb >> 8 & 255) * scale);
      int b = (int)Math.min(255.0F, (float)(rgb & 255) * scale);
      return -16777216 | r << 16 | g << 8 | b;
   }

   public void updateLight(float partialTick) {
      this.relight(new FlatLit[]{this.handle});
      this.relight(new FlatLit[]{this.button});

      for(TransformedInstance ind : this.indicators) {
         this.relight(new FlatLit[]{ind});
      }

   }

   public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
      consumer.accept(this.handle);
      consumer.accept(this.button);

      for(TransformedInstance ind : this.indicators) {
         consumer.accept(ind);
      }

   }

   protected void _delete() {
      this.handle.delete();
      this.button.delete();

      for(TransformedInstance ind : this.indicators) {
         ind.delete();
      }

   }
}
