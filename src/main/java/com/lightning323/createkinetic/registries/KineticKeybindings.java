package com.lightning323.createkinetic.registries;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KineticKeybindings {
    public static final String CONTROL_CATEGORY = "key.createkinetic.control";

    public static final KeyMapping CONTROL_UP = new KeyMapping(
            "key.createkinetic.control.up", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_SPACE, CONTROL_CATEGORY);

    public static final KeyMapping CONTROL_DOWN = new KeyMapping(
            "key.createkinetic.control.down", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CONTROL_CATEGORY);
}