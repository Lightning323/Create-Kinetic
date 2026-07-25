package org.lightning323.createkinetic.content.creative_tools.client;

import com.simibubi.create.foundation.utility.RaycastHelper;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.entities.honey_glue.HoneyGlueEntity;
import dev.simulated_team.simulated.index.SimSpecialTextures;
import dev.simulated_team.simulated.mixin.aabb.AABBMixin;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.util.SimColors;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.jetbrains.annotations.Nullable;
import org.lightning323.createkinetic.CreateKinetic;
import org.lightning323.createkinetic.content.creative_tools.item.ModItems;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = CreateKinetic.ID, value = Dist.CLIENT)
public final class AutoGlueClientRenderer {
    private AutoGlueClientRenderer() {
    }

    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Post event) {
        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer player = mc.player;
        final Level level = mc.level;
        if (player == null || level == null) {
            return;
        }

        final boolean holdingAutoGlue = player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.AUTO_GLUE.get()) ||
                player.getItemInHand(InteractionHand.OFF_HAND).is(ModItems.AUTO_GLUE.get());
        if (!holdingAutoGlue) {
            return;
        }

        final int range = SimConfigService.INSTANCE.server().assembly.honeyGlueRange.get();
        final List<HoneyGlueEntity> honeyGlues = level.getEntitiesOfClass(HoneyGlueEntity.class, player.getBoundingBox().inflate(range));
        final HoveredGlue hovered = findHoveredGlue(player, honeyGlues);

        for (final HoneyGlueEntity glue : honeyGlues) {
            if (hovered != null && hovered.glue() == glue) {
                Outliner.getInstance()
                        .chaseAABB("AutoGlueHoneyActive" + glue.getId(), glue.getBoundingBox())
                        .colored(SimColors.ACTIVE_YELLOW)
                        .withFaceTexture(SimSpecialTextures.HONEY_GLUE)
                        .highlightFace(hovered.face())
                        .disableLineNormals()
                        .lineWidth(1 / 16f);
            } else {
                Outliner.getInstance()
                        .showAABB("AutoGlueHoneyPassive" + glue.getId(), glue.getBoundingBox())
                        .colored(SimColors.PERCHANCE_ORANGE)
                        .disableLineNormals()
                        .lineWidth(1 / 64f);
            }
        }
    }

    private static @Nullable HoveredGlue findHoveredGlue(final LocalPlayer player, final List<HoneyGlueEntity> honeyGlues) {
        final Vec3 baseOrigin = player.getEyePosition();
        final Vec3 baseTarget = RaycastHelper.getTraceTarget(player, player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) * 5, baseOrigin);

        HoneyGlueEntity closestGlue = null;
        Direction closestDir = null;
        double bestDistance = Double.MAX_VALUE;

        for (final HoneyGlueEntity glue : honeyGlues) {
            final AABB toClip = glue.getBoundingBox();

            final SubLevel subLevel = Sable.HELPER.getContainingClient(toClip.getCenter());
            Vec3 subLevelOrigin = baseOrigin;
            Vec3 subLevelTarget = baseTarget;
            if (subLevel != null) {
                subLevelOrigin = subLevel.logicalPose().transformPositionInverse(baseOrigin);
                subLevelTarget = subLevel.logicalPose().transformPositionInverse(baseTarget);
            }

            final boolean contains = toClip.contains(subLevelOrigin);
            final Optional<Vec3> clip = contains ? toClip.clip(subLevelTarget, subLevelOrigin) : toClip.clip(subLevelOrigin, subLevelTarget);
            if (clip.isEmpty()) {
                continue;
            }

            final double hitDist = clip.get().distanceToSqr(subLevelOrigin);
            if (hitDist < bestDistance) {
                bestDistance = hitDist;
                closestGlue = glue;
                closestDir = getDirectionFromAABBClip(toClip, subLevelOrigin, subLevelTarget, contains);
            }
        }

        if (closestGlue == null) {
            return null;
        }

        return new HoveredGlue(closestGlue, closestDir);
    }

    private static @Nullable Direction getDirectionFromAABBClip(final AABB aabb, Vec3 origin, Vec3 end, final boolean inside) {
        if (inside) {
            final Vec3 tmp = origin;
            origin = end;
            end = tmp;
        }

        final double d = end.x - origin.x;
        final double e = end.y - origin.y;
        final double f = end.z - origin.z;
        return AABBMixin.invokeGetDirection(aabb, origin, new double[]{1f}, null, d, e, f);
    }

    private record HoveredGlue(HoneyGlueEntity glue, @Nullable Direction face) {
    }
}
