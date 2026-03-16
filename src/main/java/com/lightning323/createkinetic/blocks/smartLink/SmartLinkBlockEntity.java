package com.lightning323.createkinetic.blocks.smartLink;

import com.lightning323.createkinetic.registries.KineticBlocks;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class SmartLinkBlockEntity extends SmartBlockEntity {

	private boolean receivedSignalChanged;
	private int receivedSignal;
	private int transmittedSignal;
	private LinkBehaviour link;
	private boolean transmitter;

	public FactoryPanelSupportBehaviour panelSupport;

	public SmartLinkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(panelSupport = new FactoryPanelSupportBehaviour(this, () -> link != null && link.isListening(),
			() -> receivedSignal > 0, () -> KineticBlocks.SMART_LINK.get()
				.updateTransmittedSignal(getBlockState(), level, worldPosition)));
	}

	@Override
	public void addBehavioursDeferred(List<BlockEntityBehaviour> behaviours) {
		createLink();
		behaviours.add(link);
	}

	protected void createLink() {
		Pair<ValueBoxTransform, ValueBoxTransform> slots =
			ValueBoxTransform.Dual.makeSlots(SmartLinkFrequencySlot::new);
		link = transmitter ? LinkBehaviour.transmitter(this, slots, this::getSignal)
			: LinkBehaviour.receiver(this, slots, this::setSignal);
	}

	public int getSignal() {
		return transmittedSignal;
	}

	public void setSignal(int power) {
		if (receivedSignal != power)
			receivedSignalChanged = true;
		receivedSignal = power;
	}

	public void transmit(int strength) {
		transmittedSignal = strength;
		if (link != null)
			link.notifySignalChange();
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		compound.putBoolean("Transmitter", transmitter);
		compound.putInt("Receive", getReceivedSignal());
		compound.putBoolean("ReceivedChanged", receivedSignalChanged);
		compound.putInt("Transmit", transmittedSignal);
		super.write(compound, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		transmitter = compound.getBoolean("Transmitter");
		super.read(compound, clientPacket);

		receivedSignal = compound.getInt("Receive");
		receivedSignalChanged = compound.getBoolean("ReceivedChanged");
		if (level == null || level.isClientSide || !link.newPosition)
			transmittedSignal = compound.getInt("Transmit");
	}

	@Override
	public void tick() {
		super.tick();

		if (isTransmitterBlock() != transmitter) {
			transmitter = isTransmitterBlock();
			LinkBehaviour prevlink = link;
			removeBehaviour(LinkBehaviour.TYPE);
			createLink();
			link.copyItemsFrom(prevlink);
			attachBehaviourLate(link);
		}

		if (transmitter)
			return;
		if (level.isClientSide)
			return;

		BlockState blockState = getBlockState();
		if (!KineticBlocks.SMART_LINK.has(blockState))
			return;

		if ((getReceivedSignal() > 0) != blockState.getValue(SmartLinkBlock.POWERED)) {
			receivedSignalChanged = true;
			level.setBlockAndUpdate(worldPosition, blockState.cycle(SmartLinkBlock.POWERED));
		}

		if (receivedSignalChanged) {
			updateSelfAndAttached(blockState);
		}
	}

	@Override
	public void remove() {
		super.remove();

		updateSelfAndAttached(getBlockState());
	}

	public void updateSelfAndAttached(BlockState blockState) {
		Direction attachedFace = blockState.getValue(SmartLinkBlock.FACING)
			.getOpposite();
		BlockPos attachedPos = worldPosition.relative(attachedFace);
		level.blockUpdated(worldPosition, level.getBlockState(worldPosition)
			.getBlock());
		level.blockUpdated(attachedPos, level.getBlockState(attachedPos)
			.getBlock());
		receivedSignalChanged = false;
		panelSupport.notifyPanels();
	}

	protected Boolean isTransmitterBlock() {
		return !getBlockState().getValue(SmartLinkBlock.RECEIVER);
	}

	public int getReceivedSignal() {
		return receivedSignal;
	}

}
