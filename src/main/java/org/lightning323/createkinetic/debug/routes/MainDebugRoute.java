package org.lightning323.createkinetic.debug.routes;

import org.lightning323.createkinetic.debug.IDebugRoute;

public enum MainDebugRoute implements IDebugRoute {
    THRUSTER;

    private final IDebugRoute[] children;
    MainDebugRoute(IDebugRoute... children) { this.children = children; }
    MainDebugRoute() { this(new IDebugRoute[0]); }

    @Override public IDebugRoute[] getChildren() { return children; }
}
