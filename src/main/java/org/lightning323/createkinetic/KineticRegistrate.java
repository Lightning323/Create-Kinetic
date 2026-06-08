package org.lightning323.createkinetic;

import com.simibubi.create.foundation.data.CreateRegistrate;

public final class KineticRegistrate extends CreateRegistrate {
   private KineticRegistrate(String modid) {
      super(modid);
   }

   public static KineticRegistrate create(String modid) {
      return new KineticRegistrate(modid);
   }
}
