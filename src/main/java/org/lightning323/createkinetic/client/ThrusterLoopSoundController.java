package org.lightning323.createkinetic.client;

import dev.ryanhcode.sable.Sable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.lightning323.createkinetic.content.thruster.AbstractThrusterBlockEntity;
import org.lightning323.createkinetic.registries.PropulsionSoundEvents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class ThrusterLoopSoundController {
    private static final float MIN_SYNCED_THRUST = 0.01f;
    private static final float VOLUME_BOOST_MULTIPLIER = 1.5f;
    private static final int THRUSTER_SOUND_RANGE_BLOCKS = 100;
    private static final Map<String, ThrusterLoopSoundInstance> ACTIVE_SOUNDS = new HashMap<>();

    private ThrusterLoopSoundController() {
    }

    public static void tick(final AbstractThrusterBlockEntity blockEntity) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.player == null || minecraft.getSoundManager() == null) {
            return;
        }

        // Always clean up stopped sounds so stale entries don't block new ones.
        cleanupStopped();

        final String key = soundKey(blockEntity);
        final boolean active = shouldPlay(blockEntity);
        ThrusterLoopSoundInstance existing = ACTIVE_SOUNDS.get(key);

        // If the map has a sound for a *different* BE instance at this position (e.g. after a
        // chunk reload which recreates the BE object), stop the stale sound immediately so a
        // fresh one can be created for the new BE.
        if (existing != null && !existing.isStopped() && !existing.isFor(blockEntity)) {
            existing.halt();
            ACTIVE_SOUNDS.remove(key);
            existing = null;
        }

        if (!active) {
            if (existing != null) {
                existing.halt();
                ACTIVE_SOUNDS.remove(key);
            }
            return;
        }

        if (existing == null || existing.isStopped()) {
            final ThrusterLoopSoundInstance instance = new ThrusterLoopSoundInstance(blockEntity);
            ACTIVE_SOUNDS.put(key, instance);
            minecraft.getSoundManager().play(instance);
        }
    }

    private static void cleanupStopped() {
        final Iterator<Map.Entry<String, ThrusterLoopSoundInstance>> iterator = ACTIVE_SOUNDS.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue().isStopped()) {
                iterator.remove();
            }
        }
    }

    private static boolean shouldPlay(final AbstractThrusterBlockEntity blockEntity) {
        return blockEntity.getLevel() != null
                && blockEntity.getLevel().isClientSide()
                && !blockEntity.isRemoved()
                && blockEntity.getCurrentThrust() > MIN_SYNCED_THRUST;
    }

    private static String soundKey(final AbstractThrusterBlockEntity blockEntity) {
        final Level level = blockEntity.getLevel();
        final ResourceKey<Level> dimension = level != null ? level.dimension() : Level.OVERWORLD;
        final BlockPos pos = blockEntity.getBlockPos();
        return dimension.location() + "|" + pos.asLong();
    }

    private static final class ThrusterLoopSoundInstance extends AbstractTickableSoundInstance {
        private static final int STOP_GRACE_TICKS = 6;
        private final AbstractThrusterBlockEntity blockEntity;
        private int inactiveTicks;

        private ThrusterLoopSoundInstance(final AbstractThrusterBlockEntity blockEntity) {
            super(PropulsionSoundEvents.THRUSTER_LOOP.get(), SoundSource.BLOCKS, RandomSource.create());
            this.blockEntity = blockEntity;
            this.looping = true;
            this.delay = 0;
            this.attenuation = Attenuation.NONE;
            updateFromBlockEntity();
        }

        @Override
        public void tick() {
            if (!shouldPlay(this.blockEntity)) {
                inactiveTicks++;
                if (inactiveTicks >= STOP_GRACE_TICKS) {
                    stop();
                }
                return;
            }
            inactiveTicks = 0;
            updateFromBlockEntity();
        }

        private void updateFromBlockEntity() {
            final BlockPos pos = this.blockEntity.getBlockPos();
            final float power = Math.max(this.blockEntity.getPower(), 5.0f / 15.0f);
            final Vec3 localCenter = new Vec3(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);
            final Vec3 worldCenter = Sable.HELPER.projectOutOfSubLevel(this.blockEntity.getLevel(), localCenter);
            this.x = worldCenter.x;
            this.y = worldCenter.y;
            this.z = worldCenter.z;
            final Minecraft minecraft = Minecraft.getInstance();
            final float baseVolume = Math.min(1.0f, (0.2f + (0.35f * power)) * VOLUME_BOOST_MULTIPLIER);
            if (minecraft == null || minecraft.player == null) {
                this.volume = 0.0f;
            } else {
                final double distance = Math.sqrt(minecraft.player.distanceToSqr(worldCenter));
                final float proximityFactor = (float) Math.max(0.0d, 1.0d - (distance / THRUSTER_SOUND_RANGE_BLOCKS));
                this.volume = baseVolume * proximityFactor;
            }
            this.pitch = 0.85f + (0.25f * power);
        }

        boolean isFor(final AbstractThrusterBlockEntity be) {
            return this.blockEntity == be;
        }

        private void halt() {
            super.stop();
        }
    }
}
