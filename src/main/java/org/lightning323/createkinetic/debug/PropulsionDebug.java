package org.lightning323.createkinetic.debug;

import org.lightning323.createkinetic.config.PropulsionConfig;
import org.lightning323.createkinetic.debug.routes.MainDebugRoute;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PropulsionDebug {
    private static final Map<IDebugRoute, Boolean> activeDebugStates = new ConcurrentHashMap<>();
    private static final Map<String, Float> floatVars = new ConcurrentHashMap<>();

    public static void registerFloat(String name, float defaultValue) {
        floatVars.put(name, defaultValue);
    }

    public static float getFloat(String name) {
        return floatVars.getOrDefault(name, 0f);
    }

    public static Set<String> getFloatKeys() {
        return floatVars.keySet();
    }

    public static boolean isDebug(IDebugRoute route) {
        if (route == MainDebugRoute.THRUSTER) {
            return PropulsionConfig.CLIENT_SPEC.isLoaded() && PropulsionConfig.DEBUG_THRUSTER.get();
        }
        return activeDebugStates.getOrDefault(route, false);
    }

    //Resolve static debug routes
    static {
        initializeLeafStates(MainDebugRoute.values());
    }

    private static void initializeLeafStates(IDebugRoute[] routes) {
        for (IDebugRoute route : routes) {
            if (route.getChildren().length == 0) {
                activeDebugStates.put(route, false);
            } else {
                initializeLeafStates(route.getChildren());
            }
        }
    }
}
