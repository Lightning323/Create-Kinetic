package org.lightning323.createkinetic.content.thruster;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.config.KineticConfig;
import org.lightning323.createkinetic.content.thruster.thruster.ThrusterBlock;
import org.lightning323.createkinetic.content.thruster.vector_thruster.VectorThruster_I;
import org.lightning323.createkinetic.content.thruster.vector_thruster.ion_vector_thruster.IonVectorThrusterBlockEntity;

import java.lang.Math;

public class MeshedThrusterFlameUtils {

    public static final ResourceLocation THRUSTER_FLAME_SHADER = CreateKinetic.loc("thruster_flame");
    //            ResourceLocation.fromNamespaceAndPath("aeronautics", "burner_flame");
    private static final float FLAME_SIZE = 2f;
    private static final float BLOCK_PIXEL = 1f / 16f;
    private static final float FLAME_PIXEL = BLOCK_PIXEL / FLAME_SIZE;
    protected static final float DISPLAY_THRESHOLD = 0.03f;
    public static final int RENDER_BOX_FLAME_LENGTH = 7;

    public static boolean isSpritePlume(AbstractThrusterBlockEntity be) {
        return be.getPlumeRenderType() == KineticConfig.ThrusterPlumeType.SPRITE_MESH ||
                be.getPlumeRenderType() == KineticConfig.ThrusterPlumeType.SPRITE_MESH_SINGLE_MULTIBLOCK;
    }

    public static void renderMultiblockFlame(AbstractThrusterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int w) {
        final var state = be.getBlockState();
        final var facing = state.getValue(ThrusterBlock.FACING);
        Vector3i offset = new Vector3i(0, 0, 0);
        switch (facing) {
            case UP -> offset.set(-w + 1, 0, 0);//This is down
            case SOUTH -> offset.set(0, 0, 0);//this is north
            case NORTH -> offset.set(0, w - 1, -w + 1);//this is south
            case WEST -> offset.set(-w + 1, 1, 0);//this is east
            case EAST -> offset.set(0, 0, 0);//this is west
            default -> {//this is up
                offset.set(0, w - 1, 0);
            }
        }
        final ShaderProgram shader = VeilRenderSystem.setShader(THRUSTER_FLAME_SHADER);
        boolean bluePlume = be.isBluePlume();

        if (be.getPlumeRenderType() == KineticConfig.ThrusterPlumeType.SPRITE_MESH_SINGLE_MULTIBLOCK) {
            ms.pushPose();
            ms.scale(w, w, w);
            MeshedThrusterFlameUtils.renderMeshFlame(be, partialTicks, ms, buffer, shader,
                    bluePlume, 0 + offset.x, offset.y, 0 + offset.z, true);
            ms.popPose();
        } else {
            for (int x = 0; x < w; x++) {
                for (int z = 0; z < w; z++) {
                    MeshedThrusterFlameUtils.renderMeshFlame(be, partialTicks, ms, buffer, shader, bluePlume,
                            x + offset.x, offset.y, z + offset.z, false);

                }
            }
        }
    }

    public final static AABB NULL_AABB = AABB.ofSize(Vec3.ZERO, 0, 0, 0);

    public static void renderMeshFlame(AbstractThrusterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer) {
        final ShaderProgram shader = VeilRenderSystem.setShader(THRUSTER_FLAME_SHADER);
        renderMeshFlame(be, partialTicks, ms, buffer, shader, be.isBluePlume(), 0, 0, 0, false);
    }


    public static void renderMeshFlame(AbstractThrusterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                                       ShaderProgram shader,
                                       boolean soulFlame, int offsetX, int offsetY, int offsetZ, boolean crossed) {
        if (shader != null) {
            //Get the interpolated power
            float power = Mth.clamp(be.interpolatedPlumePower.getValue(partialTicks), 0f, 1f);
            if (power < DISPLAY_THRESHOLD) return;

            final var state = be.getBlockState();
            final var pos = be.getBlockPos();
            final var facing = state.getValue(ThrusterBlock.FACING);
            var flameOffset = snapToBlockPixel(-1.5f + ((24 - 4 * Mth.clamp(power, 0.5f, 1f)) / 16f));
            var lengthMultiplier = snapToFlamePixel((power * 4f + 1f + (0.5f - power * power * power)));
            var widthMultiplier = snapToFlamePixel((power * 1.5f + 1));

            ms.pushPose();
            ms.translate(0.5f, 0.5f, 0.5f);
            ms.translate(facing.getStepX() * flameOffset, facing.getStepY() * flameOffset, facing.getStepZ() * flameOffset);
            rotateTowardsFacing(ms, facing);
            ms.translate(offsetX, offsetY, offsetZ);

            float r = random01((pos.getY() + offsetY + pos.getZ() + offsetZ * 31) * 31 + pos.getX() + offsetX); //Randomness of the flame to prevent flames next to each other from looking the same
            shader.getUniformSafe("FlameRenderTime").setFloat(getFlameRenderTime(be, partialTicks, r));
            shader.getUniformSafe("Intensity").setFloat(Mth.clamp(power * 2f - .35f, 0.25f, 1.5f));
            shader.getUniformSafe("Palette").setFloat(soulFlame ? 1 : 0); //Soul Fire modifier
            shader.getUniformSafe("LengthMultiplier").setFloat(Math.max(lengthMultiplier, FLAME_PIXEL));
            shader.getUniformSafe("WidthMultiplier").setFloat(Math.max(widthMultiplier, FLAME_PIXEL));

            ms.mulPose(Axis.YP.rotation(getBillboardAngle(ms)));
            if (crossed) ms.mulPose(Axis.YP.rotation((float) (Math.PI / 4)));
            renderFlame(ms, FLAME_SIZE, lengthMultiplier, widthMultiplier);
            if (crossed) {
                ms.mulPose(Axis.YP.rotation((float) -(Math.PI / 2)));
                renderFlame(ms, FLAME_SIZE, lengthMultiplier, widthMultiplier);
            }
            ms.popPose();
        }
    }


    public static void renderMeshVectorFlame(AbstractThrusterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                                             ShaderProgram shader, boolean soulFlame) {
        if (shader != null) {
            //Get the interpolated power
            float power = Mth.clamp(be.interpolatedPlumePower.getValue(partialTicks), 0f, 1f);
            if (power < DISPLAY_THRESHOLD) return;

            final var pos = be.getBlockPos();

            ms.pushPose();
            ms.translate(0.5f, 0.5f, 0.5f);
            ms.mulPose(Axis.XP.rotation((float) Math.PI / 2));


            var lengthMultiplier = snapToFlamePixel((power * 4f + 1f + (0.5f - power * power * power)));
            var widthMultiplier = snapToFlamePixel((power * 1.5f + 1));

            ms.mulPose(Axis.YP.rotation(getBillboardAngle(ms)));

            float r = random01(pos.hashCode());
            shader.getUniformSafe("FlameRenderTime").setFloat(getFlameRenderTime(be, partialTicks, r));
            shader.getUniformSafe("Intensity").setFloat(Mth.clamp(power * 2f - .35f, 0.25f, 1.5f));
            shader.getUniformSafe("Palette").setFloat(soulFlame ? 1 : 0); //Soul Fire modifier
            shader.getUniformSafe("LengthMultiplier").setFloat(Math.max(lengthMultiplier, FLAME_PIXEL));
            shader.getUniformSafe("WidthMultiplier").setFloat(Math.max(widthMultiplier, FLAME_PIXEL));
            renderFlame(ms, FLAME_SIZE, lengthMultiplier, widthMultiplier);

            ms.popPose();
        }
    }

    /**
     * return a random float between 0 and 1 based on the seed
     *
     * @param seed
     * @return
     */
    private static float random01(int seed) {
        long z = seed + 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        z ^= z >>> 31;
        return (float) ((z >>> 40) * (1.0 / (1L << 24)));
    }


    private static float getRenderBoxLength(AbstractThrusterBlockEntity be) {
        return (float) (Math.ceil(be.interpolatedPlumePower.getValue() * 10) / 10 * RENDER_BOX_FLAME_LENGTH);
    }

    /**
     * inflates the render bounding box of a thruster for
     *
     * @param be Thruster Block Entity
     * @param box
     * @return the inflated render bounding box if a change was detected, otherwise null
     */
    public static AABB inflateRenderBoundingBox(AbstractThrusterBlockEntity be, AABB box) {
        float length = getRenderBoxLength(be);

        //Hash the state of the thruster
        int hash = 1;
        hash = 31 * hash + Float.floatToRawIntBits(length);
        hash = 31 * hash + box.hashCode();


        if (be.boundingBoxHash != hash) {
            be.boundingBoxHash = hash;
            final var state = be.getBlockState();
            Vec3 center = box.getCenter();
            if (be.getPlumeRenderType() == KineticConfig.ThrusterPlumeType.SPRITE_MESH_SINGLE_MULTIBLOCK) {
                length *= be.width;
            }

            return switch (state.getValue(ThrusterBlock.FACING)) {
                case DOWN -> fitPoint(box, center.x, box.maxY + length, center.z);
                case UP -> fitPoint(box, center.x, box.minY - length, center.z);
                case SOUTH -> fitPoint(box, center.x, center.y, box.minZ - length);
                case NORTH -> fitPoint(box, center.x, center.y, box.maxZ + length);
                case WEST -> fitPoint(box, box.maxX + length, center.y, center.z);
                case EAST -> fitPoint(box, box.minX - length, center.y, center.z);
            };
        }
        return null;
    }

    /**
     *
     * @param vbe Vector Thruster Block Entity
     * @param box
     * @return the inflated render bounding box if a change was detected, otherwise null
     */
    public static AABB inflateVectorRenderBoundingBox(VectorThruster_I vbe, AABB box) {
        AbstractThrusterBlockEntity be = (AbstractThrusterBlockEntity) vbe;
        float xInflate = vbe.getInterpolatedVectorX(1) * 3.5f;
        float yInflate = vbe.getInterpolatedVectorY(1) * 3.5f;
        float length = Math.max(0, getRenderBoxLength(be) - Math.max(Math.abs(xInflate), Math.abs(yInflate)) * 0.8f);

        //Hash the state of the thruster
        int hash = 1;
        hash = 31 * hash + Float.floatToRawIntBits(xInflate);
        hash = 31 * hash + Float.floatToRawIntBits(yInflate);
        hash = 31 * hash + Float.floatToRawIntBits(length);
        hash = 31 * hash + box.hashCode();

        //Compare the current vs previous state of the thruster
        if (be.boundingBoxHash != hash) {
            be.boundingBoxHash = hash;
            Vec3 center = box.getCenter();
            Direction facing = be.getBlockState().getValue(ThrusterBlock.FACING);


            Vector3d end = switch (facing) {
                case DOWN -> new Vector3d(center.x, box.maxY + length, center.z).add(xInflate, 0, -yInflate);
                case UP -> new Vector3d(center.x, box.minY - length, center.z).add(-xInflate, 0, -yInflate);
                case SOUTH -> new Vector3d(center.x, center.y, box.minZ - length).add(-xInflate, yInflate, 0);
                case NORTH -> new Vector3d(center.x, center.y, box.maxZ + length).add(xInflate, yInflate, 0);
                case WEST -> new Vector3d(box.maxX + length, center.y, center.z).add(0, yInflate, -xInflate);
                case EAST -> new Vector3d(box.minX - length, center.y, center.z).add(0, yInflate, xInflate);
            };
//        System.out.println("rotx=" + rotX + " roty=" + rotY + " flameEnd=" + end + " center=" + center);
            return fitPoint(box, end.x, end.y, end.z);
        }
        return null;
    }


    private static AABB fitPoint(AABB box, double x, double y, double z) {
        return new AABB(
                Math.min(box.minX, x),
                Math.min(box.minY, y),
                Math.min(box.minZ, z),

                Math.max(box.maxX, x),
                Math.max(box.maxY, y),
                Math.max(box.maxZ, z)
        );
    }


    private static void rotateTowardsFacing(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case UP -> poseStack.mulPose(Axis.ZP.rotation((float) Math.PI));
            case SOUTH -> poseStack.mulPose(Axis.XN.rotation((float) (Math.PI / 2f)));
            case NORTH -> poseStack.mulPose(Axis.XP.rotation((float) (Math.PI / 2f)));
            case WEST -> poseStack.mulPose(Axis.ZN.rotation((float) (Math.PI / 2f)));
            case EAST -> poseStack.mulPose(Axis.ZP.rotation((float) (Math.PI / 2f)));
            default -> {
            }
        }
    }

    /**
     * Computes the billboard rotation from the current render pose instead of reconstructing
     * the world/sublevel transform from block and camera coordinates. This keeps the mesh in
     * the same coordinate frame as its block entity on dedicated-server clients.
     */
    private static float getBillboardAngle(PoseStack ms) {
        Vector4f pivotInViewSpace = new Vector4f(0, 0, 0, 1);
        ms.last().pose().transform(pivotInViewSpace);

        // The camera is the origin in view space. Transform that direction back into the
        // flame's local frame, then rotate its local Y axis toward the camera.
        Vector3f toCameraInViewSpace = new Vector3f(
                -pivotInViewSpace.x,
                -pivotInViewSpace.y,
                -pivotInViewSpace.z
        );
        Matrix3f localFromView = new Matrix3f(ms.last().pose()).invert();
        localFromView.transform(toCameraInViewSpace);
        return (float) Math.atan2(toCameraInViewSpace.x(), toCameraInViewSpace.z());
    }

    /**
     * Sublevels on a remote client do not necessarily advance their own game-time counter.
     * The root ClientLevel is authoritative for the visual clock and advances every client tick.
     */
    private static float getFlameRenderTime(AbstractThrusterBlockEntity be, float partialTicks, float phase) {
        final var rootClientLevel = Minecraft.getInstance().level;
        long gameTime = rootClientLevel != null
                ? rootClientLevel.getGameTime()
                : be.getLevel() != null ? be.getLevel().getGameTime() : 0L;
        return phase + (gameTime + partialTicks) * 0.1f;
    }

    private static float snapToBlockPixel(float value) {
        return Math.round(value / BLOCK_PIXEL) * BLOCK_PIXEL;
    }

    private static float snapToFlamePixel(float value) {
        return Math.max(0f, Math.round(value / FLAME_PIXEL) * FLAME_PIXEL);
    }

    @SuppressWarnings("SameParameterValue")
    private static void renderFlame(final PoseStack poseStack, final float size, final float lengthMultiplier, final float widthMultiplier) {
        final var builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        final var halfSize = (size / 2f) * widthMultiplier;

        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();

        final var pose = poseStack.last().pose();
        builder.addVertex(pose, -halfSize, 0f, 0f).setUv(0f, 1f);
        builder.addVertex(pose, halfSize, 0f, 0f).setUv(1f, 1f);
        builder.addVertex(pose, halfSize, size * lengthMultiplier, 0f).setUv(1f, 0f);
        builder.addVertex(pose, -halfSize, size * lengthMultiplier, 0f).setUv(0f, 0f);

        BufferUploader.drawWithShader(builder.buildOrThrow());

        RenderSystem.disableDepthTest();
        RenderSystem.enableCull();
    }
}
