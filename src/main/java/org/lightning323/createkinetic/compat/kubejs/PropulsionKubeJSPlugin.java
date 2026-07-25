package org.lightning323.createkinetic.compat.kubejs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import org.lightning323.createkinetic.content.thruster.SolidThrusterFuelManager;
import org.lightning323.createkinetic.content.thruster.ThrusterFuelManager;

public class PropulsionKubeJSPlugin implements KubeJSPlugin {
    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("ThrusterFuelManager", ThrusterFuelManager.class);
        bindings.add("SolidThrusterFuelManager", SolidThrusterFuelManager.class);
    }
}


