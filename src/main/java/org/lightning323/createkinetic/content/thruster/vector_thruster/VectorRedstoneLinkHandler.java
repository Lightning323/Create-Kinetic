package org.lightning323.createkinetic.content.thruster.vector_thruster;

import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.RaycastHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.lightning323.createkinetic.CreateKinetic;

import java.util.Arrays;

@EventBusSubscriber(modid = CreateKinetic.ID)
public class VectorRedstoneLinkHandler {

    @SuppressWarnings("unchecked")
    private static final BehaviourType<VectorRedstoneLinkBehaviour>[] ALL_TYPES = new BehaviourType[]{
        VectorRedstoneLinkBehaviour.WEST_TYPE,
        VectorRedstoneLinkBehaviour.EAST_TYPE,
        VectorRedstoneLinkBehaviour.DOWN_TYPE,
        VectorRedstoneLinkBehaviour.UP_TYPE
    };

    @SubscribeEvent
    public static void onBlockActivated(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled())
            return;

        Level world = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();

        if (player.isShiftKeyDown() || player.isSpectator())
            return;

        ItemStack heldItem = player.getItemInHand(hand);
        BlockHitResult ray = RaycastHelper.rayTraceRange(world, player, 10);
        if (ray == null)
            return;
        String heldItemId = BuiltInRegistries.ITEM.getKey(heldItem.getItem()).toString();
        if ("create:linked_controller".equals(heldItemId) || "create:wrench".equals(heldItemId))
            return;

        for (BehaviourType<VectorRedstoneLinkBehaviour> type : ALL_TYPES) {
            VectorRedstoneLinkBehaviour behaviour = BlockEntityBehaviour.get(world, pos, type);
            if (behaviour == null)
                continue;

            boolean fakePlayer = player instanceof FakePlayer;
            boolean fakePlayerChoice = false;

            if (fakePlayer) {
                BlockState blockState = world.getBlockState(pos);
                Vec3 localHit = ray.getLocation().subtract(Vec3.atLowerCornerOf(pos))
                        .add(Vec3.atLowerCornerOf(ray.getDirection().getNormal()).scale(.25f));
                fakePlayerChoice = localHit.distanceToSqr(behaviour.getFirstSlot().getLocalOffset(world, pos, blockState)) >
                        localHit.distanceToSqr(behaviour.getSecondSlot().getLocalOffset(world, pos, blockState));
            }

            for (boolean first : Arrays.asList(false, true)) {
                if (behaviour.testHit(first, ray.getLocation()) || fakePlayer && fakePlayerChoice == first) {
                    if (event.getSide() != LogicalSide.CLIENT)
                        behaviour.setFrequency(first, heldItem);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    world.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, .25f, .1f);
                    return;
                }
            }
        }
    }
}
