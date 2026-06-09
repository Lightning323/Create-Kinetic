/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package dev.qwxon.tracks.client;

import dev.qwxon.tracks.content.blocks.sable_track.SableTrackPart;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import net.minecraft.network.chat.Component;

public class TrackRenderTuning {
    public static final Profile SMALL_SUSPENSION = new Profile("small_suspension");
    public static final Profile SUSPENSION = new Profile("suspension");
    public static final Profile LARGE_SUSPENSION = new Profile("large_suspension");
    public static final Profile SMALL_DRIVE = new Profile("small_drive");
    public static final Profile DRIVE = new Profile("drive");
    public static final Profile LARGE_DRIVE = new Profile("large_drive");
    public static final Profile[] PROFILES = new Profile[]{SMALL_SUSPENSION, SUSPENSION, LARGE_SUSPENSION, SMALL_DRIVE, DRIVE, LARGE_DRIVE};
    public static float BASE_SLOPE_DEGREES = 0.0f;

    public static Profile profileFor(SableTrackPart part) {
        return switch (part) {
            case SableTrackPart.SMALL_SUSPENSION -> SMALL_SUSPENSION;
            case SableTrackPart.LARGE_SUSPENSION -> LARGE_SUSPENSION;
            case SableTrackPart.SMALL_DRIVE -> SMALL_DRIVE;
            case SableTrackPart.DRIVE -> DRIVE;
            case SableTrackPart.LARGE_DRIVE -> LARGE_DRIVE;
            default -> SUSPENSION;
        };
    }

    public static String dump() {
        StringBuilder builder = new StringBuilder("Tracks render tuning:");
        for (Profile profile : PROFILES) {
            builder.append("\n[").append(profile.name).append("]");
            for (Element element : profile.elements) {
                builder.append("\n").append(element.name).append(" x=").append(element.x).append(" y=").append(element.y).append(" z=").append(element.z).append(" scale_x=").append(element.scaleX).append(" scale_y=").append(element.scaleY).append(" scale_z=").append(element.scaleZ).append(" slope=").append(element.slopeDegrees);
            }
        }
        builder.append("\nbase_slope_degrees=").append(BASE_SLOPE_DEGREES);
        return builder.toString();
    }

    public static Component dumpComponent() {
        return Component.literal((String)TrackRenderTuning.dump());
    }

    public static void save(Path path) {
        try {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            Files.writeString(path, (CharSequence)TrackRenderTuning.dump(), new OpenOption[0]);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public static void load(Path path) {
        if (!Files.isRegularFile(path, new LinkOption[0])) {
            return;
        }
        try {
            Profile currentProfile = null;
            for (String rawLine : Files.readAllLines(path)) {
                String[] tokens;
                Element element;
                String line = rawLine.trim();
                if (line.startsWith("[") && line.endsWith("]")) {
                    currentProfile = TrackRenderTuning.profileByName(line.substring(1, line.length() - 1));
                    continue;
                }
                if (line.startsWith("base_slope_degrees=")) {
                    BASE_SLOPE_DEGREES = Float.parseFloat(line.substring("base_slope_degrees=".length()));
                    continue;
                }
                if (currentProfile == null || line.isBlank() || line.startsWith("Tracks render tuning") || (element = currentProfile.elementByName((tokens = line.split(" "))[0])) == null) continue;
                for (int i = 1; i < tokens.length; ++i) {
                    String[] pair = tokens[i].split("=");
                    if (pair.length != 2) continue;
                    element.set(pair[0], Float.parseFloat(pair[1]));
                }
            }
        }
        catch (IOException | NumberFormatException exception) {
            // empty catch block
        }
    }

    private static Profile profileByName(String name) {
        for (Profile profile : PROFILES) {
            if (!profile.name.equals(name)) continue;
            return profile;
        }
        return null;
    }

    public static final class Profile {
        public final String name;
        public final Element wheel = new Element("wheel", 1.0f, 0.0f, 0.0f);
        public final Element topBelt = new Element("top_belt", 1.0f, 0.8f, 1.15f);
        public final Element bottomBelt = new Element("bottom_belt", 1.0f, 0.15f, 1.15f);
        public final Element wrapBelt = new Element("drive_wrap_belt", 1.0f, 0.0f, 0.65f);
        public final Element suspensionMount = new Element("suspension_mount", 0.0f, 0.0f, 0.0f);
        public final Element[] elements = new Element[]{this.wheel, this.topBelt, this.bottomBelt, this.wrapBelt, this.suspensionMount};

        private Profile(String name) {
            this.name = name;
            this.applyDefaults();
        }

        private void applyDefaults() {
            switch (this.name) {
                case "small_suspension": {
                    this.topBelt.setDefaults(1.0f, 0.75f, -0.5f, 1.2249999f, 1.0f, 1.0f, 0.0f);
                    this.bottomBelt.setDefaults(1.0f, -1.0500003f, -0.475f, 1.2249999f, 1.0f, 1.0f, 0.0f);
                    this.suspensionMount.setDefaults(0.49999997f, -1.1920929E-7f, 0.049999997f, 1.0f, 1.0f, 1.0f, 0.0f);
                    break;
                }
                case "large_suspension": {
                    this.wheel.setDefaults(1.0f, -0.85f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f);
                    this.topBelt.setDefaults(1.0f, 0.49999976f, -0.4750001f, 1.0f, 1.0f, 1.0f, 0.0f);
                    this.bottomBelt.setDefaults(1.0f, -1.8f, -0.475f, 1.0f, 1.0f, 1.0f, 0.0f);
                    this.wrapBelt.setDefaults(1.0f, 0.125f, 0.65f, 1.0f, 1.0f, 1.0f, 0.0f);
                    break;
                }
                case "small_drive": {
                    this.wheel.setDefaults(1.0f, 0.1f, -0.125f, 1.0f, 1.0f, 1.0f, 0.0f);
                    this.topBelt.setDefaults(1.0f, 0.8000001f, -0.47499996f, 1.1500001f, 1.0f, 1.0f, 3.0f);
                    this.bottomBelt.setDefaults(1.0f, -0.9f, -0.47500002f, 1.15f, 1.0f, 1.05f, 10.75f);
                    this.wrapBelt.setDefaults(1.0f, 0.82500005f, -0.22500002f, 1.1750001f, 1.125f, 1.0f, 0.0f);
                    this.suspensionMount.setDefaults(0.47500002f, 0.0f, 0.15f, 1.0f, 1.0f, 1.0f, 0.0f);
                    break;
                }
                case "large_drive": {
                    this.wheel.setDefaults(1.0f, -0.49999997f, -3.7252903E-9f, 1.0f, 1.0f, 1.0f, 0.0f);
                    this.topBelt.setDefaults(1.0f, 0.6249999f, -0.49999997f, 1.0f, 1.0f, 1.0f, 9.25f);
                    this.bottomBelt.setDefaults(1.0f, -1.8f, -0.45000005f, 1.0f, 1.0f, 1.0f, 0.0f);
                    this.wrapBelt.setDefaults(1.0f, 0.62500024f, -0.15000015f, 1.0f, 1.15f, 1.0f, 0.0f);
                    break;
                }
            }
        }

        private Element elementByName(String name) {
            for (Element element : this.elements) {
                if (!element.name.equals(name)) continue;
                return element;
            }
            return null;
        }
    }

    public static final class Element {
        public final String name;
        public float x;
        public float y;
        public float z;
        public float scaleX = 1.0f;
        public float scaleY = 1.0f;
        public float scaleZ = 1.0f;
        public float slopeDegrees = 0.0f;

        private Element(String name, float x, float y, float z) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private void setDefaults(float x, float y, float z, float scaleX, float scaleY, float scaleZ, float slopeDegrees) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.scaleX = scaleX;
            this.scaleY = scaleY;
            this.scaleZ = scaleZ;
            this.slopeDegrees = slopeDegrees;
        }

        public float get(int axis) {
            return switch (axis) {
                case 0 -> this.x;
                case 1 -> this.y;
                case 2 -> this.z;
                case 3 -> this.scaleX;
                case 4 -> this.scaleY;
                case 5 -> this.scaleZ;
                default -> this.slopeDegrees;
            };
        }

        public void add(int axis, float delta) {
            switch (axis) {
                case 0: {
                    this.x += delta;
                    break;
                }
                case 1: {
                    this.y += delta;
                    break;
                }
                case 2: {
                    this.z += delta;
                    break;
                }
                case 3: {
                    this.scaleX = Math.max(0.05f, this.scaleX + delta);
                    break;
                }
                case 4: {
                    this.scaleY = Math.max(0.05f, this.scaleY + delta);
                    break;
                }
                case 5: {
                    this.scaleZ = Math.max(0.05f, this.scaleZ + delta);
                    break;
                }
                default: {
                    this.slopeDegrees += delta * 10.0f;
                }
            }
        }

        private void set(String key, float value) {
            switch (key) {
                case "x": {
                    this.x = value;
                    break;
                }
                case "y": {
                    this.y = value;
                    break;
                }
                case "z": {
                    this.z = value;
                    break;
                }
                case "scale_x": {
                    this.scaleX = Math.max(0.05f, value);
                    break;
                }
                case "scale_y": {
                    this.scaleY = Math.max(0.05f, value);
                    break;
                }
                case "scale_z": {
                    this.scaleZ = Math.max(0.05f, value);
                    break;
                }
                case "slope": {
                    this.slopeDegrees = value;
                }
            }
        }

        public String toString() {
            return Arrays.asList(this.name, Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z), Float.valueOf(this.scaleX), Float.valueOf(this.scaleY), Float.valueOf(this.scaleZ), Float.valueOf(this.slopeDegrees)).toString();
        }
    }
}

