package org.lightning323.createkinetic.utility;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.lightning323.createkinetic.debug.DebugRenderer;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public abstract class AbstractAreaDamagerBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<AbstractAreaDamagerBehaviour> TYPE = new BehaviourType<>();
    public record WorldOrientedBox(Level level, Vec3 center, Vec3 direction) {}
    public record DamageZone(Vec3 dimensions, Vec3 offset, Vec3 facing, Vec3 up, WorldOrientedBox worldBox, Object context) {}

    public AbstractAreaDamagerBehaviour(SmartBlockEntity be) {
        super(be);
    }

    @Override
    public void tick() {
        super.tick();
        Level level = getWorld();
        if (level.isClientSide() || !shouldDamage()) { return; }
        
        if (level.getGameTime() % getTickFrequency() != 0) { return; }

        //Calculate the damage zone
        Optional<DamageZone> damageZoneOpt = calculateDamageZone();
        if (damageZoneOpt.isEmpty()) { return; }
        DamageZone zone = damageZoneOpt.get();

        if (shouldDebug()) {
            debugZone(zone);
        }

        //Find entities in the zone
        List<LivingEntity> entities;
        if (zone.worldBox() != null) {
            entities = OBBEntityFinder.getEntitiesInWorldOrientedBox(
                zone.worldBox().level(),
                zone.worldBox().center(),
                zone.worldBox().direction(),
                zone.dimensions()
            );
        } else {
            entities = OBBEntityFinder.getEntitiesInOrientedBox(
                level,
                getPos(),
                zone.up(),
                zone.facing(),
                zone.dimensions(),
                zone.offset()
            );
        }

        if (entities.isEmpty()) { return; }

        //Apply damage to entities
        DamageSource damageSource = getDamageSource();
        for (LivingEntity entity : entities) {
            if (entity.isRemoved() || entity.fireImmune()) continue;
            applyDamage(entity, damageSource, zone);
        }
    }

    protected abstract int getTickFrequency();

    protected abstract boolean shouldDamage();

    protected abstract DamageSource getDamageSource();

    protected abstract Optional<DamageZone> calculateDamageZone();

    protected abstract void applyDamage(LivingEntity entity, DamageSource source, DamageZone zone);

    protected abstract boolean shouldDebug();

    protected abstract Color getDebugColor();

    private void debugZone(DamageZone zone) {
        Quaterniond worldOrientation;
        Vec3 worldCenter;
        if (zone.worldBox() != null) {
            worldOrientation = OBBEntityFinder.calculateWorldOrientationFromDirection(zone.worldBox().direction());
            worldCenter = zone.worldBox().center();
        } else {
            worldOrientation = OBBEntityFinder.calculateWorldOrientation(getWorld(), getPos(), zone.up(), zone.facing());
            worldCenter = OBBEntityFinder.calculateWorldCenter(getWorld(), getPos(), zone.offset(), worldOrientation);
        }
        
        String dimensionKey = getWorld().dimension().location().toString();
        String identifier = "damager_" + dimensionKey + "_" + getPos().asLong() + "_obb";
        Quaternionf debugRotation = new Quaternionf((float)worldOrientation.x, (float)worldOrientation.y, (float)worldOrientation.z, (float)worldOrientation.w);
        
        DebugRenderer.drawBox(identifier, worldCenter, zone.dimensions(), debugRotation, getDebugColor(), false, getTickFrequency() + 1);
    }
    
    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
