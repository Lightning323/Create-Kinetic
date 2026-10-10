package org.lightning323.createkinetic.content.thruster;

import java.lang.reflect.Method;

public final class ThrusterSoundHooks {
    private ThrusterSoundHooks() {
    }

    public static void clientTick(final AbstractThrusterBlockEntity blockEntity) {
        if (blockEntity.getLevel() == null || !blockEntity.getLevel().isClientSide()) {
            return;
        }

        try {
            final Class<?> controller = Class.forName("org.lightning323.createkinetic.client.ThrusterLoopSoundController");
            final Method tickMethod = controller.getMethod("tick", AbstractThrusterBlockEntity.class);
            tickMethod.invoke(null, blockEntity);
        } catch (final ReflectiveOperationException ignored) {
        }
    }
}

