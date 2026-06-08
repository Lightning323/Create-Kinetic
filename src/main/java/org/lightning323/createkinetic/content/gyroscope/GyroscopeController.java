package org.lightning323.createkinetic.content.gyroscope;

import org.lightning323.createkinetic.Config;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelObserver;
import dev.ryanhcode.sable.neoforge.event.ForgeSableSubLevelContainerReadyEvent;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import java.util.Map;
import java.util.WeakHashMap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.joml.Matrix3dc;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class GyroscopeController {
   private static final Map<ServerSubLevel, GyroscopeController> INSTANCES = new WeakHashMap();
   private static final Vector3dc WORLD_UP = new Vector3d((double)0.0F, (double)1.0F, (double)0.0F);
   private static final double DISTURBANCE_DELTA_OMEGA_CAP = (double)1.0F;
   private static final double NEVER_TICKED = (double)-1.0F;
   private final ServerSubLevel subLevel;
   private final Vector3d currentUpWorld = new Vector3d();
   private final Vector3d errorAxisWorld = new Vector3d();
   private final Vector3d errorAxisLocal = new Vector3d();
   private final Vector3d angularVelocityWorld = new Vector3d();
   private final Vector3d angularVelocityLocal = new Vector3d();
   private final Vector3d worldUpLocal = new Vector3d();
   private final Vector3d restoringImpulseLocal = new Vector3d();
   private final Vector3d dampingImpulseLocal = new Vector3d();
   private final Vector3d deltaOmegaLocal = new Vector3d();
   private final Vector3d prevAngularVelocityLocal = new Vector3d();
   private final Vector3d lastAppliedImpulseLocal = new Vector3d();
   private final Vector3d disturbanceImpulseLocal = new Vector3d();
   private final Vector3d filteredDisturbanceLocal = new Vector3d();
   private final Vector3d disturbanceDeltaOmega = new Vector3d();
   private double lastTickedPartial = (double)-1.0F;
   private boolean hasObservedTick = false;

   private GyroscopeController(ServerSubLevel subLevel) {
      this.subLevel = subLevel;
   }

   public static GyroscopeController of(ServerSubLevel subLevel) {
      return (GyroscopeController)INSTANCES.computeIfAbsent(subLevel, GyroscopeController::new);
   }

   static void detach(ServerSubLevel subLevel) {
      INSTANCES.remove(subLevel);
   }

   public void tick(double partialPhysicsTick, RigidBodyHandle handle, double timeStep) {
      if (partialPhysicsTick != this.lastTickedPartial) {
         this.lastTickedPartial = partialPhysicsTick;
         FleetState fleet = this.computeFleetState();
         if (fleet.totalCapacity() <= (double)0.0F) {
            this.resetObserver();
            this.pushStabilizedPercentToBEs(0);
         } else {
            MassData mass = this.subLevel.getMassTracker();
            if (mass.isInvalid()) {
               this.resetObserver();
            } else {
               double inertia = averageInertia(mass);
               Gains gains = computeGains(inertia, fleet.totalCapacity(), fleet.meanRpmScale());
               if (gains.scale() <= (double)0.0F) {
                  this.resetObserver();
                  this.pushStabilizedPercentToBEs(0);
               } else {
                  Matrix3dc inverseInertia = mass.getInverseInertiaTensor();
                  Quaterniondc orientation = this.subLevel.logicalPose().orientation();
                  handle.getAngularVelocity(this.angularVelocityWorld);
                  orientation.transformInverse(this.angularVelocityWorld, this.angularVelocityLocal);
                  orientation.transformInverse(WORLD_UP, this.worldUpLocal);
                  this.angularVelocityLocal.fma(-this.angularVelocityLocal.dot(this.worldUpLocal), this.worldUpLocal);
                  this.disturbanceImpulseLocal.zero();
                  if (this.hasObservedTick) {
                     this.disturbanceDeltaOmega.set(this.angularVelocityLocal).sub(this.prevAngularVelocityLocal);
                     inverseInertia.transform(this.lastAppliedImpulseLocal, this.deltaOmegaLocal);
                     this.disturbanceDeltaOmega.sub(this.deltaOmegaLocal);
                     double distMag = this.disturbanceDeltaOmega.length();
                     if (distMag > (double)1.0F) {
                        this.disturbanceDeltaOmega.mul((double)1.0F / distMag);
                     }

                     mass.getInertiaTensor().transform(this.disturbanceDeltaOmega, this.disturbanceImpulseLocal);
                     this.disturbanceImpulseLocal.fma(-this.disturbanceImpulseLocal.dot(this.worldUpLocal), this.worldUpLocal);
                  }

                  this.filteredDisturbanceLocal.lerp(this.disturbanceImpulseLocal, Config.gyroscopeFeedForwardSmoothing());
                  if (fleet.kX() <= (double)0.0F) {
                     this.filteredDisturbanceLocal.x = (double)0.0F;
                  }

                  if (fleet.kZ() <= (double)0.0F) {
                     this.filteredDisturbanceLocal.z = (double)0.0F;
                  }

                  orientation.transform(WORLD_UP, this.currentUpWorld);
                  this.currentUpWorld.cross(WORLD_UP, this.errorAxisWorld);
                  orientation.transformInverse(this.errorAxisWorld, this.errorAxisLocal);
                  this.restoringImpulseLocal.set(this.errorAxisLocal).mul(gains.kp() * timeStep);
                  this.dampingImpulseLocal.set(this.angularVelocityLocal).mul(-gains.kd() * timeStep);
                  inverseInertia.transform(this.dampingImpulseLocal, this.deltaOmegaLocal);
                  this.dampingImpulseLocal.mul(clampingFactor(this.angularVelocityLocal, this.deltaOmegaLocal));
                  this.restoringImpulseLocal.add(this.dampingImpulseLocal);
                  double feedForward = gains.scale() * Config.gyroscopeFeedForwardGain();
                  this.restoringImpulseLocal.fma(-feedForward, this.filteredDisturbanceLocal);
                  Vector3d var10000 = this.restoringImpulseLocal;
                  var10000.x *= fleet.kX();
                  var10000 = this.restoringImpulseLocal;
                  var10000.z *= fleet.kZ();
                  double wUpY = this.worldUpLocal.y;
                  if (Math.abs(wUpY) > 0.001) {
                     this.restoringImpulseLocal.y = -(this.restoringImpulseLocal.x * this.worldUpLocal.x + this.restoringImpulseLocal.z * this.worldUpLocal.z) / wUpY;
                  }

                  handle.applyTorqueImpulse(this.restoringImpulseLocal);
                  this.prevAngularVelocityLocal.set(this.angularVelocityLocal);
                  this.lastAppliedImpulseLocal.set(this.restoringImpulseLocal);
                  this.hasObservedTick = true;
                  double tiltX = this.errorAxisLocal.x() * fleet.kX();
                  double tiltZ = this.errorAxisLocal.z() * fleet.kZ();
                  double omegaX = this.angularVelocityLocal.x() * fleet.kX();
                  double omegaZ = this.angularVelocityLocal.z() * fleet.kZ();
                  double omegaTarget = Config.gyroscopeOmegaTarget();
                  double phaseDistance = Math.min((double)1.0F, Math.sqrt(tiltX * tiltX + tiltZ * tiltZ + (omegaX * omegaX + omegaZ * omegaZ) / (omegaTarget * omegaTarget)));
                  int reportedPercent = (int)Math.round(((double)1.0F - phaseDistance) * (double)100.0F);
                  this.pushStabilizedPercentToBEs(reportedPercent);
               }
            }
         }
      }
   }

   private void resetObserver() {
      this.filteredDisturbanceLocal.zero();
      this.hasObservedTick = false;
   }

   private void pushStabilizedPercentToBEs(int percent) {
      for(BlockEntitySubLevelActor actor : this.subLevel.getPlot().getBlockEntityActors()) {
         if (actor instanceof GyroscopeBlockEntity gyro) {
            gyro.setStabilizedPercent(percent);
         }
      }

   }

   private static double averageInertia(MassData mass) {
      Matrix3dc tensor = mass.getInertiaTensor();
      return (tensor.m00() + tensor.m11() + tensor.m22()) / (double)3.0F;
   }

   private FleetState computeFleetState() {
      double totalCapacity = (double)0.0F;
      double totalRpmScale = (double)0.0F;
      double weightedKX = (double)0.0F;
      double weightedKZ = (double)0.0F;
      int gyroCount = 0;

      for(BlockEntitySubLevelActor actor : this.subLevel.getPlot().getBlockEntityActors()) {
         if (actor instanceof GyroscopeBlockEntity gyro) {
            double rpmScale = gyro.currentRpmScale();
            double authority = gyroAuthority(rpmScale);
            totalCapacity += authority;
            totalRpmScale += rpmScale;
            weightedKX += authority * ((double)1.0F - (double)gyro.getOwnSignalX() / (double)15.0F);
            weightedKZ += authority * ((double)1.0F - (double)gyro.getOwnSignalZ() / (double)15.0F);
            ++gyroCount;
         }
      }

      double meanRpmScale = gyroCount > 0 ? totalRpmScale / (double)gyroCount : (double)0.0F;
      double kX = totalCapacity > (double)0.0F ? weightedKX / totalCapacity : (double)1.0F;
      double kZ = totalCapacity > (double)0.0F ? weightedKZ / totalCapacity : (double)1.0F;
      return new FleetState(totalCapacity, meanRpmScale, kX, kZ);
   }

   private static Gains computeGains(double inertia, double totalCapacity, double meanRpmScale) {
      double omega = Config.gyroscopeOmegaTarget();
      double zeta = Config.gyroscopeDampingRatio();
      double scale = inertia <= (double)0.0F ? (double)0.0F : Math.min(meanRpmScale, totalCapacity / inertia);
      double kpFull = omega * omega * inertia;
      double kdFull = (double)2.0F * zeta * omega * inertia;
      return new Gains(scale * kpFull, scale * kdFull, scale);
   }

   private static double gyroAuthority(double rpmScale) {
      return rpmScale * Config.gyroscopeAuthorityPerUnit();
   }

   private static double clampingFactor(Vector3dc currentVelocity, Vector3dc expectedVelocityChange) {
      double k = -currentVelocity.dot(expectedVelocityChange);
      double v = currentVelocity.lengthSquared();
      if (k < (double)0.0F) {
         return (double)0.0F;
      } else if ((double)10.0F * k < v) {
         return (double)1.0F - k / ((double)2.0F * v);
      } else {
         return v < 1.0E-10 ? v / (k + 1.0E-10) : v * ((double)1.0F - Math.exp(-k / v)) / k;
      }
   }

   @SubscribeEvent
   public static void onSubLevelContainerReady(ForgeSableSubLevelContainerReadyEvent event) {
      SubLevelContainer container = event.getContainer();
      container.addObserver(new SubLevelObserver() {
         public void onSubLevelRemoved(SubLevel subLevel, SubLevelRemovalReason r) {
            if (subLevel instanceof ServerSubLevel s) {
               GyroscopeController.detach(s);
            }

         }
      });
   }

   @SubscribeEvent
   public static void onServerStarting(ServerStartingEvent event) {
      INSTANCES.clear();
   }

   @SubscribeEvent
   public static void onServerStopping(ServerStoppingEvent event) {
      INSTANCES.clear();
   }

   private static record FleetState(double totalCapacity, double meanRpmScale, double kX, double kZ) {
   }

   private static record Gains(double kp, double kd, double scale) {
   }
}
