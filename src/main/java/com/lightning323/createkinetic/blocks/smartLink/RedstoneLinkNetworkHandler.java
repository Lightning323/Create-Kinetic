package com.lightning323.createkinetic.blocks.smartLink;

import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.levelWrappers.WorldHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RedstoneLinkNetworkHandler {

	static final Map<LevelAccessor, Map<Couple<Frequency>, Set<com.simibubi.create.content.redstone.link.IRedstoneLinkable>>> connections =
		new IdentityHashMap<>();

	public final AtomicInteger globalPowerVersion = new AtomicInteger();

	public static class Frequency {
		public static final Frequency EMPTY = new Frequency(ItemStack.EMPTY);
		private static final Map<Item, Frequency> simpleFrequencies = new IdentityHashMap<>();
		private ItemStack stack;
		private Item item;
		private int color;

		public static Frequency of(ItemStack stack) {
			if (stack.isEmpty())
				return EMPTY;
			if (!stack.hasTag())
				return simpleFrequencies.computeIfAbsent(stack.getItem(), $ -> new Frequency(stack));
			return new Frequency(stack);
		}

		private Frequency(ItemStack stack) {
			this.stack = stack;
			item = stack.getItem();
			CompoundTag displayTag = stack.getTagElement("display");
			color = displayTag != null && displayTag.contains("color") ? displayTag.getInt("color") : -1;
		}

		public ItemStack getStack() {
			return stack;
		}

		@Override
		public int hashCode() {
			return (item.hashCode() * 31) ^ color;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			return obj instanceof Frequency ? ((Frequency) obj).item == item && ((Frequency) obj).color == color
				: false;
		}

	}

	public void onLoadWorld(LevelAccessor world) {
		connections.put(world, new HashMap<>());
		Create.LOGGER.debug("Prepared Redstone Network Space for " + WorldHelper.getDimensionID(world));
	}

	public void onUnloadWorld(LevelAccessor world) {
		connections.remove(world);
		Create.LOGGER.debug("Removed Redstone Network Space for " + WorldHelper.getDimensionID(world));
	}

	public Set<com.simibubi.create.content.redstone.link.IRedstoneLinkable> getNetworkOf(LevelAccessor world, com.simibubi.create.content.redstone.link.IRedstoneLinkable actor) {
		Map<Couple<Frequency>, Set<com.simibubi.create.content.redstone.link.IRedstoneLinkable>> networksInWorld = networksIn(world);
		Couple<Frequency> key = actor.getNetworkKey();
		if (!networksInWorld.containsKey(key))
			networksInWorld.put(key, new LinkedHashSet<>());
		return networksInWorld.get(key);
	}

	public void addToNetwork(LevelAccessor world, com.simibubi.create.content.redstone.link.IRedstoneLinkable actor) {
		getNetworkOf(world, actor).add(actor);
		updateNetworkOf(world, actor);
	}

	public void removeFromNetwork(LevelAccessor world, com.simibubi.create.content.redstone.link.IRedstoneLinkable actor) {
		Set<com.simibubi.create.content.redstone.link.IRedstoneLinkable> network = getNetworkOf(world, actor);
		network.remove(actor);
		if (network.isEmpty()) {
			networksIn(world).remove(actor.getNetworkKey());
			return;
		}
		updateNetworkOf(world, actor);
	}

	public void updateNetworkOf(LevelAccessor world, com.simibubi.create.content.redstone.link.IRedstoneLinkable actor) {
		Set<com.simibubi.create.content.redstone.link.IRedstoneLinkable> network = getNetworkOf(world, actor);
		globalPowerVersion.incrementAndGet();
		int power = 0;

		for (Iterator<com.simibubi.create.content.redstone.link.IRedstoneLinkable> iterator = network.iterator(); iterator.hasNext(); ) {
			com.simibubi.create.content.redstone.link.IRedstoneLinkable other = iterator.next();
			if (!other.isAlive()) {
				iterator.remove();
				continue;
			}

			if (!withinRange(actor, other))
				continue;

			if (power < 15)
				power = Math.max(other.getTransmittedStrength(), power);
		}

		if (actor instanceof LinkBehaviour linkBehaviour) {
			// fix one-to-one loading order problem
			if (linkBehaviour.isListening()) {
				linkBehaviour.newPosition = true;
				linkBehaviour.setReceivedStrength(power);
			}
		}

		for (com.simibubi.create.content.redstone.link.IRedstoneLinkable other : network) {
			if (other != actor && other.isListening() && withinRange(actor, other))
				other.setReceivedStrength(power);
		}
	}

	public static boolean withinRange(com.simibubi.create.content.redstone.link.IRedstoneLinkable from, com.simibubi.create.content.redstone.link.IRedstoneLinkable to) {
		if (from == to)
			return true;
		return from.getLocation()
			.closerThan(to.getLocation(), AllConfigs.server().logistics.linkRange.get());
	}

	public Map<Couple<Frequency>, Set<com.simibubi.create.content.redstone.link.IRedstoneLinkable>> networksIn(LevelAccessor world) {
		if (!connections.containsKey(world)) {
			Create.LOGGER.warn("Tried to Access unprepared network space of " + WorldHelper.getDimensionID(world));
			return new HashMap<>();
		}
		return connections.get(world);
	}

	public boolean hasAnyLoadedPower(Couple<Frequency> frequency) {
		for (Map<Couple<Frequency>, Set<com.simibubi.create.content.redstone.link.IRedstoneLinkable>> map : connections.values()) {
			Set<com.simibubi.create.content.redstone.link.IRedstoneLinkable> set = map.get(frequency);
			if (set == null || set.isEmpty())
				continue;
			for (IRedstoneLinkable link : set)
				if (link.getTransmittedStrength() > 0)
					return true;
		}
		return false;
	}

}
