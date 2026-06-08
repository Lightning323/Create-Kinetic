package org.lightning323.createkinetic.content.gyroscope;

import org.lightning323.createkinetic.KineticPartialModels;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class GyroscopeVisual extends AbstractBlockEntityVisual<GyroscopeBlockEntity> implements SimpleDynamicVisual {
   private static final float FLYWHEEL_LIFT = 0.61875F;
   private static final float FLYWHEEL_HEIGHT = 0.375F;
   private static final float FLYWHEEL_LIFT_INVERTED = 0.006250024F;
   private static final float SHAFT_DROP = 0.0F;
   private final OrientedInstance flywheel;
   private final OrientedInstance shaft;
   private final Quaternionf compensation = new Quaternionf();
   private final Quaternionf flywheelOrientation = new Quaternionf();
   private final Quaternionf shaftOrientation = new Quaternionf();

   public GyroscopeVisual(VisualizationContext ctx, GyroscopeBlockEntity be, float partialTick) {
      super(ctx, be, partialTick);
      boolean inverted = be.getBlockState().getValue(GyroscopeBlock.FACING) == Direction.UP;
      float flywheelLift = inverted ? 0.006250024F : 0.61875F;
      this.flywheel = ((OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(KineticPartialModels.GYROSCOPE_FLYWHEEL)).createInstance()).position(this.getVisualPosition()).translatePosition(0.0F, flywheelLift, 0.0F);
      this.shaft = ((OrientedInstance)this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(AllPartialModels.SHAFT_HALF)).createInstance()).position(this.getVisualPosition()).translatePosition(0.0F, -0.0F, 0.0F);
      this.relight(new FlatLit[]{this.flywheel, this.shaft});
   }

   public void beginFrame(Context context) {
      float partialTick = context.partialTick();
      this.computeCompensation(partialTick, this.compensation);
      float flywheelSpeed = ((GyroscopeBlockEntity)this.blockEntity).effectiveSpeedAt(partialTick) * 3.0F / 10.0F;
      float flywheelAngle = ((GyroscopeBlockEntity)this.blockEntity).getAngle() + flywheelSpeed * partialTick;
      float shaftAngleRad = KineticBlockEntityRenderer.getAngleForBe((KineticBlockEntity)this.blockEntity, this.pos, Axis.Y);
      this.flywheelOrientation.set(this.compensation).rotateY((float)Math.toRadians((double)flywheelAngle));
      this.flywheel.rotation(this.flywheelOrientation).setChanged();
      boolean inverted = ((GyroscopeBlockEntity)this.blockEntity).getBlockState().getValue(GyroscopeBlock.FACING) == Direction.UP;
      float xRot = inverted ? (-(float)Math.PI / 2F) : ((float)Math.PI / 2F);
      float zRot = inverted ? shaftAngleRad : -shaftAngleRad;
      this.shaftOrientation.identity().rotateX(xRot).rotateZ(zRot);
      this.shaft.rotation(this.shaftOrientation).setChanged();
   }

   public void updateLight(float partialTick) {
      this.relight(new FlatLit[]{this.flywheel, this.shaft});
   }

   public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
      consumer.accept(this.flywheel);
      consumer.accept(this.shaft);
   }

   protected void _delete() {
      this.flywheel.delete();
      this.shaft.delete();
   }

   private void computeCompensation(float partialTick, Quaternionf dest) {
      ClientSubLevel subLevel = Sable.HELPER.getContainingClient(this.blockEntity);
      if (subLevel == null) {
         dest.identity();
      } else {
         dest.set(subLevel.renderPose(partialTick).orientation()).invert();
      }
   }
}
