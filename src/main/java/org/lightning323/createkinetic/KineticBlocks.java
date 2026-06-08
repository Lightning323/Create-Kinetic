package org.lightning323.createkinetic;

import org.lightning323.createkinetic.content.gyroscope.GyroscopeBlock;
import org.lightning323.createkinetic.content.joystick.JoystickBlock;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;

public final class KineticBlocks {

   public static final BlockEntry<GyroscopeBlock> GYROSCOPE = ((BlockBuilder) CreateKinetic.getRegistrate()
           .block("gyroscope", GyroscopeBlock::new).initialProperties(SharedProperties::softMetal)
           .properties((p) -> p.noOcclusion())
           .onRegister((block) -> BlockStressValues.IMPACTS.register(block, Config::gyroscopeStressImpact)))
           .simpleItem().register();

   public static final BlockEntry<JoystickBlock> JOYSTICK = CreateKinetic.getRegistrate()
           .block("joystick", JoystickBlock::new).initialProperties(SharedProperties::wooden)
           .properties((p) -> p.noOcclusion())
           .simpleItem().register();

   private KineticBlocks() {
   }

   public static void register() {
   }
}
