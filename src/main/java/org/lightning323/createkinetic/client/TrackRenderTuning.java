package org.lightning323.createkinetic.client;

import org.lightning323.createkinetic.content.blocks.track.SableTrackPart;

public class TrackRenderTuning {
    public static final float BASE_SLOPE_DEGREES = 0.0f;
    public static final Profile SMALL_SUSPENSION = new Profile("small_suspension");
    public static final Profile SUSPENSION = new Profile("suspension");
    public static final Profile LARGE_SUSPENSION = new Profile("large_suspension");
    public static final Profile SMALL_DRIVE = new Profile("small_drive");
    public static final Profile DRIVE = new Profile("drive");
    public static final Profile LARGE_DRIVE = new Profile("large_drive");
    public static final Profile[] PROFILES = new Profile[]{SMALL_SUSPENSION, SUSPENSION, LARGE_SUSPENSION, SMALL_DRIVE, DRIVE, LARGE_DRIVE};

    private TrackRenderTuning() {
    }

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
    }
}