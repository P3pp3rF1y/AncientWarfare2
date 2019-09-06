package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Supplier;

public abstract class PacketBase {

	private static HashMap<Integer, Supplier<? extends PacketBase>> packetTypes = new HashMap<>();
	private static HashMap<Class<? extends PacketBase>, Integer> packetIDs = new HashMap<>();

	public static void registerPacketType(int typeNum, Class<? extends PacketBase> packetClz, Supplier<? extends PacketBase> instantiate) {
		packetTypes.put(typeNum, instantiate);
		packetIDs.put(packetClz, typeNum);
	}

	public PacketBase() {
	}

	private void writeHeaderToStream(ByteBuf data) {
		data.writeByte(packetIDs.get(this.getClass()));
	}

	private static PacketBase readHeaderFromStream(ByteBuf data) {
		int typeNum = data.readByte();

		if (!packetTypes.containsKey(typeNum)) {
			throw new IllegalArgumentException("Unregistered packet id received - " + typeNum);
		}

		return packetTypes.get(typeNum).get();
	}

	protected abstract void writeToStream(ByteBuf data);

	protected abstract void readFromStream(ByteBuf data) throws IOException;

	protected void execute() {
	}

	@SuppressWarnings("squid:S1172") //used in overrides
	protected void execute(EntityPlayer player) {
		execute();
	}

	public static PacketBase readPacket(ByteBuf data) throws IOException {
		PacketBase pkt = readHeaderFromStream(data);
		pkt.readFromStream(data);
		return pkt;
	}

	public final FMLProxyPacket getFMLPacket() {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		writeHeaderToStream(buf);
		writeToStream(buf);
		return new FMLProxyPacket(buf, NetworkHandler.CHANNELNAME);
	}
}
