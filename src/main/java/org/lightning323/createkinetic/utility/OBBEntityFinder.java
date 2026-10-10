package org.lightning323.createkinetic.utility;

import dev.ryanhcode.sable.Sable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lightning323.createkinetic.utility.math.DeltasOBB;

import java.util.ArrayList;
import java.util.List;

public class OBBEntityFinder {
    private static final double MAX_BROAD_PHASE_RADIUS = 256.0;
    private static final Quaterniond TEMP_WORLD_QUATERNION = new Quaterniond();
    private static final Vector3d TEMP_VECTOR2 = new Vector3d();
    private static final Matrix3f TEMP_OBB_ROTATION = new Matrix3f();
    private static final Vector3f TEMP_OBB_CENTER = new Vector3f();
    private static final Vector3f TEMP_OBB_HALFEXT = new Vector3f();

    public static List<LivingEntity> getEntitiesInOrientedBox(Level level, BlockPos pos, Direction boxPrimaryAxis, Direction localDirection, Vec3 boxDimensions, Vec3 boxOffset) {
        return getEntitiesInOrientedBox(
            level,
            pos,
            Vec3.atLowerCornerOf(boxPrimaryAxis.getNormal()),
            Vec3.atLowerCornerOf(localDirection.getNormal()),
            boxDimensions,
            boxOffset
        );
    }

    public static List<LivingEntity> getEntitiesInOrientedBox(Level level, BlockPos pos, Vec3 boxPrimaryAxis, Vec3 localDirection, Vec3 boxDimensions, Vec3 boxOffset) {
        TEMP_WORLD_QUATERNION.identity();
        TEMP_OBB_ROTATION.identity();
        Quaterniond worldOrientation = calculateWorldOrientation(level, pos, boxPrimaryAxis, localDirection);
        Vec3 worldCenter = calculateWorldCenter(level, pos, boxOffset);
        Level queryLevel = getRealWorldLevel(level, pos);

        TEMP_WORLD_QUATERNION.set(worldOrientation);
        TEMP_VECTOR2.set(worldCenter.x, worldCenter.y, worldCenter.z);

        //Broad phase
        double inflation = Math.max(boxDimensions.x, Math.max(boxDimensions.y, boxDimensions.z));
        if (!Double.isFinite(TEMP_VECTOR2.x) || !Double.isFinite(TEMP_VECTOR2.y) || !Double.isFinite(TEMP_VECTOR2.z)
            || !Double.isFinite(inflation) || inflation <= 0 || inflation > MAX_BROAD_PHASE_RADIUS) {
            return List.of();
        }

        AABB broadPhaseBox = AABB.ofSize(new Vec3(TEMP_VECTOR2.x, TEMP_VECTOR2.y, TEMP_VECTOR2.z), 0, 0, 0).inflate(inflation);
        List<LivingEntity> candidateEntities = queryLevel.getEntitiesOfClass(LivingEntity.class, broadPhaseBox);
        if (candidateEntities.isEmpty()) return List.of();

        //Narrow phase
        TEMP_OBB_CENTER.set((float) TEMP_VECTOR2.x, (float) TEMP_VECTOR2.y, (float) TEMP_VECTOR2.z);
        TEMP_OBB_HALFEXT.set((float) (boxDimensions.x * 0.5), (float) (boxDimensions.y * 0.5), (float) (boxDimensions.z * 0.5));
        TEMP_OBB_ROTATION.set(TEMP_WORLD_QUATERNION);

        List<LivingEntity> intersectingEntities = new ArrayList<>();
        for (LivingEntity entity : candidateEntities) {
            AABB bb = entity.getBoundingBox();
            float bx = (float) ((bb.minX + bb.maxX) * 0.5);
            float by = (float) ((bb.minY + bb.maxY) * 0.5);
            float bz = (float) ((bb.minZ + bb.maxZ) * 0.5);
            float bex = (float) ((bb.maxX - bb.minX) * 0.5);
            float bey = (float) ((bb.maxY - bb.minY) * 0.5);
            float bez = (float) ((bb.maxZ - bb.minZ) * 0.5);
            if (DeltasOBB.intersectsAABB(TEMP_OBB_CENTER, TEMP_OBB_HALFEXT, TEMP_OBB_ROTATION, bx, by, bz, bex, bey, bez)) {
                intersectingEntities.add(entity);
            }
        }

        return intersectingEntities;
    }

    public static List<LivingEntity> getEntitiesInWorldOrientedBox(Level level, Vec3 worldCenter, Vec3 worldDirection, Vec3 boxDimensions) {
        TEMP_WORLD_QUATERNION.identity();
        TEMP_OBB_ROTATION.identity();
        Quaterniond worldOrientation = calculateWorldOrientationFromDirection(worldDirection);
        TEMP_WORLD_QUATERNION.set(worldOrientation);
        TEMP_VECTOR2.set(worldCenter.x, worldCenter.y, worldCenter.z);

        double inflation = Math.max(boxDimensions.x, Math.max(boxDimensions.y, boxDimensions.z));
        if (!Double.isFinite(TEMP_VECTOR2.x) || !Double.isFinite(TEMP_VECTOR2.y) || !Double.isFinite(TEMP_VECTOR2.z)
            || !Double.isFinite(inflation) || inflation <= 0 || inflation > MAX_BROAD_PHASE_RADIUS) {
            return List.of();
        }

        AABB broadPhaseBox = AABB.ofSize(new Vec3(TEMP_VECTOR2.x, TEMP_VECTOR2.y, TEMP_VECTOR2.z), 0, 0, 0).inflate(inflation);
        List<LivingEntity> candidateEntities = level.getEntitiesOfClass(LivingEntity.class, broadPhaseBox);
        if (candidateEntities.isEmpty()) return List.of();

        TEMP_OBB_CENTER.set((float) TEMP_VECTOR2.x, (float) TEMP_VECTOR2.y, (float) TEMP_VECTOR2.z);
        TEMP_OBB_HALFEXT.set((float) (boxDimensions.x * 0.5), (float) (boxDimensions.y * 0.5), (float) (boxDimensions.z * 0.5));
        TEMP_OBB_ROTATION.set(TEMP_WORLD_QUATERNION);

        List<LivingEntity> intersectingEntities = new ArrayList<>();
        for (LivingEntity entity : candidateEntities) {
            AABB bb = entity.getBoundingBox();
            float bx = (float) ((bb.minX + bb.maxX) * 0.5);
            float by = (float) ((bb.minY + bb.maxY) * 0.5);
            float bz = (float) ((bb.minZ + bb.maxZ) * 0.5);
            float bex = (float) ((bb.maxX - bb.minX) * 0.5);
            float bey = (float) ((bb.maxY - bb.minY) * 0.5);
            float bez = (float) ((bb.maxZ - bb.minZ) * 0.5);
            if (DeltasOBB.intersectsAABB(TEMP_OBB_CENTER, TEMP_OBB_HALFEXT, TEMP_OBB_ROTATION, bx, by, bz, bex, bey, bez)) {
                intersectingEntities.add(entity);
            }
        }

        return intersectingEntities;
    }

    public static Quaterniond calculateWorldOrientation(Level level, BlockPos pos, Direction boxPrimaryAxis, Direction localDirection) {
        return calculateWorldOrientation(
            level,
            pos,
            Vec3.atLowerCornerOf(boxPrimaryAxis.getNormal()),
            Vec3.atLowerCornerOf(localDirection.getNormal())
        );
    }

    public static Quaterniond calculateWorldOrientation(Level level, BlockPos pos, Vec3 boxPrimaryAxis, Vec3 localDirection) {
        // Keep OBB forward axis locked to the plume axis seen in debug rays.
        Vec3 worldForward = projectDirectionToWorld(level, pos, localDirection);
        Vector3d zAxis = new Vector3d(worldForward.x, worldForward.y, worldForward.z);
        if (zAxis.lengthSquared() < 1.0e-8) {
            zAxis.set(localDirection.x, localDirection.y, localDirection.z);
        }
        if (zAxis.lengthSquared() < 1.0e-8) {
            zAxis.set(0, 0, 1);
        }
        zAxis.normalize();

        // Use a stable projected reference to define roll around the plume axis.
        Vec3 worldReference = projectDirectionToWorld(level, pos, boxPrimaryAxis);
        Vector3d reference = new Vector3d(worldReference.x, worldReference.y, worldReference.z);
        if (reference.lengthSquared() < 1.0e-8) {
            reference.set(boxPrimaryAxis.x, boxPrimaryAxis.y, boxPrimaryAxis.z);
        }
        if (reference.lengthSquared() < 1.0e-8) {
            reference.set(0, 1, 0);
        }

        Vector3d xAxis = reference.cross(zAxis, new Vector3d());
        if (xAxis.lengthSquared() < 1.0e-8) {
            Vector3d fallback = Math.abs(zAxis.y) > 0.999
                ? new Vector3d(1, 0, 0)
                : new Vector3d(0, 1, 0);
            xAxis = fallback.cross(zAxis, new Vector3d());
        }
        xAxis.normalize();
        Vector3d yAxis = new Vector3d(zAxis).cross(xAxis).normalize();

        // Empirical correction: the OBB local basis is quarter-turned versus plume debug basis.
        Vector3d correctedX = yAxis;
        Vector3d correctedY = new Vector3d(xAxis).negate();

        Matrix3f basis = new Matrix3f(
            (float) correctedX.x, (float) correctedY.x, (float) zAxis.x,
            (float) correctedX.y, (float) correctedY.y, (float) zAxis.y,
            (float) correctedX.z, (float) correctedY.z, (float) zAxis.z
        );
        return new Quaterniond().setFromNormalized(basis).normalize();
    }

    public static Quaterniond calculateWorldOrientationFromDirection(Vec3 worldDirection) {
        Vector3d direction = new Vector3d(worldDirection.x, worldDirection.y, worldDirection.z);
        if (direction.lengthSquared() < 1.0e-8) {
            direction.set(0, 0, 1);
        } else {
            direction.normalize();
        }
        return new Quaterniond().rotateTo(new Vector3d(0, 0, 1), direction).normalize();
    }

    public static Vec3 calculateWorldCenter(Level level, BlockPos pos, Vec3 localOffset, Quaterniond worldOrientation) {
        return calculateWorldCenter(level, pos, localOffset);
    }

    public static Vec3 calculateWorldCenter(Level level, BlockPos pos, Vec3 localOffset) {
        Vec3 localCenter = new Vec3(
            pos.getX() + 0.5 + localOffset.x,
            pos.getY() + 0.5 + localOffset.y,
            pos.getZ() + 0.5 + localOffset.z
        );
        return Sable.HELPER.projectOutOfSubLevel(level, localCenter);
    }

    private static Vec3 projectDirectionToWorld(Level level, BlockPos pos, Vec3 localDirection) {
        Vec3 localOrigin = Vec3.atCenterOf(pos);
        Vec3 worldOrigin = Sable.HELPER.projectOutOfSubLevel(level, localOrigin);
        Vec3 worldAhead = Sable.HELPER.projectOutOfSubLevel(level, localOrigin.add(localDirection));
        Vec3 worldDirection = worldAhead.subtract(worldOrigin);
        if (worldDirection.lengthSqr() < 1.0e-8) {
            return localDirection;
        }
        return worldDirection.normalize();
    }

    private static Level getRealWorldLevel(Level level, BlockPos pos) {
        var containing = Sable.HELPER.getContaining(level, pos);
        if (containing != null && containing.getLevel() != null) {
            return containing.getLevel();
        }
        return level;
    }
}
