/*
 * Decompiled with CFR 0.152.
 */
package dev.qwxon.tracks;

import dev.qwxon.tracks.index.TracksPartialModels;
import dev.qwxon.tracks.index.TracksSpriteShifts;

public class TracksClient {
    public static boolean holdingSuspensionKey = false;
    public static boolean holdingSuspensionKeyInPositionMode = false;
    public static boolean holdingSuspensionKeyInAllPositionMode = false;
    public static boolean holdingSuspensionKeyInResetMode = false;

    public static void init() {
        TracksPartialModels.init();
        TracksSpriteShifts.init();
    }
}

