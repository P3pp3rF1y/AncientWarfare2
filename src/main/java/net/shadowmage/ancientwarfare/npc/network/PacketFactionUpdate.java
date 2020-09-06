package net.shadowmage.ancientwarfare.npc.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.gamedata.FactionData;

import java.io.IOException;

public class PacketFactionUpdate extends PacketBase {
	private NBTTagCompound packetData;

	public PacketFactionUpdate(NBTTagCompound tag) {
		this.packetData = tag;
	}

	public PacketFactionUpdate() {}

	@Override
	protected void writeToStream(ByteBuf data) {
		if (packetData != null) {
			try (ByteBufOutputStream outputStream = new ByteBufOutputStream(data)) {
				CompressedStreamTools.writeCompressed(packetData, outputStream);
			}
			catch (IOException e) {
				AncientWarfareNPC.LOG.error("Error writing faction update packet data: ", e);
			}
		}
	}

	@Override
	protected void readFromStream(ByteBuf data) {
		try (ByteBufInputStream inputStream = new ByteBufInputStream(data)) {
			packetData = CompressedStreamTools.readCompressed(inputStream);
		}
		catch (IOException e) {
			AncientWarfareNPC.LOG.error("Error reading faction update packet data: ", e);
		}
	}

	@Override
	protected void execute(EntityPlayer player) {
		if (packetData != null) {
			AWGameData.INSTANCE.getData(player.world, FactionData.class).handlePacketData(player, packetData);
		}
	}
}
