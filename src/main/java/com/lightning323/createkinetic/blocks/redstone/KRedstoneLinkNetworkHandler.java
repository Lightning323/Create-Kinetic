package com.lightning323.createkinetic.blocks.redstone;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.createmod.catnip.data.Couple;
import net.createmod.catnip.levelWrappers.WorldHelper;
import net.minecraft.world.level.LevelAccessor;

public class KRedstoneLinkNetworkHandler {

	static final Map<LevelAccessor, Map<Couple<KFrequency>, Set<KIRedstoneLinkable>>> connections =
		new IdentityHashMap<>();

	public final AtomicInteger globalPowerVersion = new AtomicInteger();


	public void onLoadWorld(LevelAccessor world) {
		connections.put(world, new HashMap<>());
		Create.LOGGER.debug("Prepared Redstone Network Space for " + WorldHelper.getDimensionID(world));
	}

	public void onUnloadWorld(LevelAccessor world) {
		connections.remove(world);
		Create.LOGGER.debug("Removed Redstone Network Space for " + WorldHelper.getDimensionID(world));
	}

	public Set<KIRedstoneLinkable> getNetworkOf(LevelAccessor world, KIRedstoneLinkable actor) {
		Map<Couple<KFrequency>, Set<KIRedstoneLinkable>> networksInWorld = networksIn(world);
		Couple<KFrequency> key = actor.getNetworkKey();
		if (!networksInWorld.containsKey(key))
			networksInWorld.put(key, new LinkedHashSet<>());
		return networksInWorld.get(key);
	}

	public void addToNetwork(LevelAccessor world, KIRedstoneLinkable actor) {
		getNetworkOf(world, actor).add(actor);
		updateNetworkOf(world, actor);
	}

	public void removeFromNetwork(LevelAccessor world, KIRedstoneLinkable actor) {
		Set<KIRedstoneLinkable> network = getNetworkOf(world, actor);
		network.remove(actor);
		if (network.isEmpty()) {
			networksIn(world).remove(actor.getNetworkKey());
			return;
		}
		updateNetworkOf(world, actor);
	}

	public void updateNetworkOf(LevelAccessor world, KIRedstoneLinkable actor) {
		Set<KIRedstoneLinkable> network = getNetworkOf(world, actor);
		globalPowerVersion.incrementAndGet();
		int power = 0;

		for (Iterator<KIRedstoneLinkable> iterator = network.iterator(); iterator.hasNext(); ) {
			KIRedstoneLinkable other = iterator.next();
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

		for (KIRedstoneLinkable other : network) {
			if (other != actor && other.isListening() && withinRange(actor, other))
				other.setReceivedStrength(power);
		}
	}

	public static boolean withinRange(KIRedstoneLinkable from, KIRedstoneLinkable to) {
		if (from == to)
			return true;
		return from.getLocation()
			.closerThan(to.getLocation(), AllConfigs.server().logistics.linkRange.get());
	}

	public Map<Couple<KFrequency>, Set<KIRedstoneLinkable>> networksIn(LevelAccessor world) {
		if (!connections.containsKey(world)) {
			Create.LOGGER.warn("Tried to Access unprepared network space of " + WorldHelper.getDimensionID(world));
			return new HashMap<>();
		}
		return connections.get(world);
	}

	public boolean hasAnyLoadedPower(Couple<KFrequency> frequency) {
		for (Map<Couple<KFrequency>, Set<KIRedstoneLinkable>> map : connections.values()) {
			Set<KIRedstoneLinkable> set = map.get(frequency);
			if (set == null || set.isEmpty())
				continue;
			for (KIRedstoneLinkable link : set)
				if (link.getTransmittedStrength() > 0)
					return true;
		}
		return false;
	}

}