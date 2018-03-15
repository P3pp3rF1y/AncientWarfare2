package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.io.IOException;
import java.util.HashMap;

public abstract class PacketBase {

	private static HashMap<Integer, Class<? extends PacketBase>> packetTypes = new HashMap<>();
	private static HashMap<Class<? extends PacketBase>, Integer> packetIDs = new HashMap<>();

	public static void registerPacketType(int typeNum, Class<? extends PacketBase> packetClz) {
		packetTypes.put(typeNum, packetClz);
		packetIDs.put(packetClz, typeNum);
	}

	public PacketBase() {
	}

	protected void writeHeaderToStream(ByteBuf data) {
		data.writeByte(packetIDs.get(this.getClass()));
	}

	protected static PacketBase readHeaderFromStream(ByteBuf data) {
		int typeNum = data.readByte();
		try {
			return packetTypes.get(typeNum).newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected abstract void writeToStream(ByteBuf data);

	protected abstract void readFromStream(ByteBuf data) throws IOException;

	protected void execute() {
	}

	;

	protected void execute(EntityPlayer player) {
		execute();
	}

	public static PacketBase readPacket(ByteBuf data) throws IOException {
		PacketBase pkt = readHeaderFromStream(data);
		if (pkt != null) {
			pkt.readFromStream(data);
		}
		return pkt;
	}

	public final FMLProxyPacket getFMLPacket() {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		writeHeaderToStream(buf);
		writeToStream(buf);
		return new FMLProxyPacket(buf, NetworkHandler.CHANNELNAME);
	}

}
