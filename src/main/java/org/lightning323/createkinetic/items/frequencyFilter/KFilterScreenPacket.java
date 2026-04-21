//package org.lightning323.createkinetic.items.frequencyFilter;
//
//
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//import net.minecraft.server.level.ServerPlayer;
//
//public class KFilterScreenPacket implements CustomPacketPayload {
//
//	@Override
//	public Type<? extends CustomPacketPayload> type() {
//		return TYPE;
//	}
//
//	public enum KOption {
//		WHITELIST, WHITELIST2, BLACKLIST, RESPECT_DATA, IGNORE_DATA, UPDATE_FILTER_ITEM, ADD_TAG, ADD_INVERTED_TAG, UPDATE_ADDRESS;
//	}
//
//	private final KOption option;
//	private final CompoundTag data;
//
//	public KFilterScreenPacket(KOption option) {
//		this(option, new CompoundTag());
//	}
//
//	public KFilterScreenPacket(KOption option, CompoundTag data) {
//		this.option = option;
//		this.data = data;
//	}
//
//	public KFilterScreenPacket(FriendlyByteBuf buffer) {
//		option = KOption.values()[buffer.readInt()];
//		data = buffer.readNbt();
//	}
//
//	@Override
//	public void write(FriendlyByteBuf buffer) {
//		buffer.writeInt(option.ordinal());
//		buffer.writeNbt(data);
//	}
//
//	@Override
//	public boolean handle(Context context) {
//		context.enqueueWork(() -> {
//			ServerPlayer player = context.getSender();
//			if (player == null)
//				return;
//
//			if (player.containerMenu instanceof FrequencyFilterMenu c) {
//				if (option == KOption.UPDATE_ADDRESS)
//					c.address = data.getString(FrequencyFilterItem.ADDRESS_TAG);
//			}
//		});
//		return true;
//	}
//
//}