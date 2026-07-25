package org.lightning323.createkinetic.content.thruster;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.lightning323.createkinetic.config.PropulsionConfig;
import org.lightning323.createkinetic.content.thruster.thruster.ThrusterBlockEntity;
import org.lightning323.createkinetic.particles.ion.IonParticleData;
import org.lightning323.createkinetic.particles.plasma.PlasmaParticleData;
import org.lightning323.createkinetic.particles.plume.PlumeParticleData;

public final class ThrusterParticles {
    private static final float PARTICLE_VELOCITY = 4.0f;

    private ThrusterParticles() {
    }

    public static void spawn(final ThrusterBlockEntity blockEntity) {
        if (!blockEntity.shouldEmitPlume()) {
            return;
        }

        final float throttle = (float) blockEntity.getThrottle();
        if (throttle < 0.05f) {
            return;
        }

        final int maxCap = PropulsionConfig.CLIENT_PARTICLES_PER_TICK.get();
        if (maxCap <= 0) {
            return;
        }

        final Direction exhaust = blockEntity.getFacing().getOpposite();
        final double nozzleOffset = blockEntity.getNozzleOffsetFromCenter();
        final int stepX = exhaust.getStepX();
        final int stepY = exhaust.getStepY();
        final int stepZ = exhaust.getStepZ();
        final double baseX = blockEntity.getBlockPos().getX() + 0.5d;
        final double baseY = blockEntity.getBlockPos().getY() + 0.5d;
        final double baseZ = blockEntity.getBlockPos().getZ() + 0.5d;
        final Vector3d localNozzle = new Vector3d(
                baseX + stepX * nozzleOffset,
                baseY + stepY * nozzleOffset,
                baseZ + stepZ * nozzleOffset
        );

        final SimulatedThrustAdapter.Projection projection = SimulatedThrustAdapter.projectToWorld(
                blockEntity.getLevel(),
                blockEntity.getBlockPos(),
                localNozzle,
                new Vector3d(stepX, stepY, stepZ)
        );

        final Level level = projection.level();
        final Vec3 basePos = projection.position();
        final Vec3 dir = projection.direction().normalize();
        final double speed = PARTICLE_VELOCITY * throttle;
        final int densityCount = Math.max(1, (int) Math.ceil(speed / AbstractThrusterBlockEntity.TARGET_PARTICLE_SPACING_BLOCKS));
        final int count = Math.min(maxCap, densityCount);

        final ParticleOptions particle = blockEntity.isIon()
                ? new IonParticleData()
                : (blockEntity.isCreative()
                ? switch (blockEntity.getPlumeType()) {
                    case PLASMA -> new PlasmaParticleData();
                    case ION -> new IonParticleData();
                    case PLUME, NONE -> new PlumeParticleData();
                }
                : new PlumeParticleData());

        final double vx = dir.x * speed;
        final double vy = dir.y * speed;
        final double vz = dir.z * speed;

        if (level instanceof final ClientLevel clientLevel) {
            for (int i = 0; i < count; i++) {
                final double beamFrac = count <= 1 ? 0.0 : (double) i / (double) count;
                clientLevel.addParticle(particle, true,
                        basePos.x + vx * beamFrac,
                        basePos.y + vy * beamFrac,
                        basePos.z + vz * beamFrac,
                        vx, vy, vz);
            }
        } else {
            for (int i = 0; i < count; i++) {
                final double beamFrac = count <= 1 ? 0.0 : (double) i / (double) count;
                level.addParticle(particle,
                        basePos.x + vx * beamFrac,
                        basePos.y + vy * beamFrac,
                        basePos.z + vz * beamFrac,
                        vx, vy, vz);
            }
        }
    }
}


