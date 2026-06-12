/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.tterrag.registrate.util.entry.BlockEntityEntry
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  dev.simulated_team.simulated.registrate.SimulatedRegistrate
 */
package org.lightning323.createkinetic.registry;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.KineticRegistrate;
import org.lightning323.createkinetic.content.blocks.sable_track.SableTrackBlockEntity;
import org.lightning323.createkinetic.content.blocks.sable_track.SableTrackRenderer;
import org.lightning323.createkinetic.content.blocks.gyroscope.GyroscopeBlockEntity;
import org.lightning323.createkinetic.content.blocks.gyroscope.GyroscopeRenderer;
import org.lightning323.createkinetic.content.blocks.joystick.JoystickBlockEntity;
import org.lightning323.createkinetic.content.blocks.joystick.JoystickRenderer;

public class KineticBlockEntityTypes {
    private static final KineticRegistrate REGISTRATE = CreateKinetic.getRegistrate();

    public static final BlockEntityEntry<SableTrackBlockEntity> SABLE_TRACK = REGISTRATE.blockEntity("sable_track", SableTrackBlockEntity::new)
            .validBlocks(new NonNullSupplier[]{KineticBlocks.TRACK_MOUNT})
            .renderer(() -> (BlockEntityRendererProvider<SableTrackBlockEntity>) SableTrackRenderer::new)
            .register();

    public static final BlockEntityEntry<GyroscopeBlockEntity> GYROSCOPE = REGISTRATE
            .blockEntity("gyroscope", GyroscopeBlockEntity::new)
            .validBlocks(KineticBlocks.GYROSCOPE)
            .renderer(() -> GyroscopeRenderer::new)
            .register();

    public static final BlockEntityEntry<JoystickBlockEntity> JOYSTICK = REGISTRATE
            .blockEntity("joystick", JoystickBlockEntity::new)
            .validBlocks(KineticBlocks.JOYSTICK)
            .renderer(() -> JoystickRenderer::new)
            .register();

    public static void init() {
    }
}

