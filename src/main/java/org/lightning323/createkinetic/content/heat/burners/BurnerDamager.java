package org.lightning323.createkinetic.content.heat.burners;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.lightning323.createkinetic.utility.AbstractAreaDamagerBehaviour;

import java.awt.*;
import java.util.Optional;

public class BurnerDamager extends AbstractAreaDamagerBehaviour {
    public BurnerDamager(SmartBlockEntity be) { super(be); }

    @Override
    protected int getTickFrequency() {
        return 1;
    }

    @Override
    protected boolean shouldDamage() {
        AbstractBurnerBlockEntity burner = (AbstractBurnerBlockEntity) blockEntity;
        HeatLevel heatLevel = burner.getBlockState().getValue(AbstractBurnerBlock.HEAT);
        return heatLevel == HeatLevel.KINDLED && getWorld().getBlockState(getPos().above()).isAir();
    }

    @Override
    protected DamageSource getDamageSource() {
        return getWorld().damageSources().hotFloor();
    }

    @Override
    protected Optional<DamageZone> calculateDamageZone() {
        Vec3 boxDimensions = new Vec3(0.9, 0.1, 0.9);
        Vec3 boxOffset = new Vec3(0, 0.5, 0);
        return Optional.of(new DamageZone(
            boxDimensions,
            boxOffset,
            Vec3.atLowerCornerOf(Direction.UP.getNormal()),
            Vec3.atLowerCornerOf(Direction.SOUTH.getNormal()),
            null,
            null
        ));
    }

    @Override
    protected void applyDamage(LivingEntity entity, DamageSource source, DamageZone zone) {
        entity.hurt(source, 3.0f);
    }

    @Override
    protected boolean shouldDebug() {
        return false;
    }

    @Override
    protected Color getDebugColor() {
        return Color.RED;
    }
}
