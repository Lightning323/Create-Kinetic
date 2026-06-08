package org.lightning323.createkinetic;

import org.lightning323.createkinetic.content.gyroscope.GyroscopeBlockEntity;
import org.lightning323.createkinetic.content.gyroscope.GyroscopeRenderer;
import org.lightning323.createkinetic.content.joystick.JoystickBlockEntity;
import org.lightning323.createkinetic.content.joystick.JoystickRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

public final class KineticBlockEntityTypes {

    public static final BlockEntityEntry<GyroscopeBlockEntity> GYROSCOPE = CreateKinetic.getRegistrate()
            .blockEntity("gyroscope", GyroscopeBlockEntity::new)
            .validBlocks(KineticBlocks.GYROSCOPE)
            .renderer(() -> GyroscopeRenderer::new)
            .register();

    public static final BlockEntityEntry<JoystickBlockEntity> JOYSTICK = CreateKinetic.getRegistrate()
            .blockEntity("joystick", JoystickBlockEntity::new)
            .validBlocks(KineticBlocks.JOYSTICK)
            .renderer(() -> JoystickRenderer::new)
            .register();

    private KineticBlockEntityTypes() {
    }

    public static void register() {
    }

}
