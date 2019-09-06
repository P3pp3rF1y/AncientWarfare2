package net.shadowmage.ancientwarfare.structure.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;

import java.io.IOException;

public class PacketStructure extends PacketBase {
	public NBTTagCompound packetData = new NBTTagCompound();

	@Override
	protected void writeToStream(ByteBuf data) {
		if (packetData != null) {
			try (ByteBufOutputStream outputStream = new ByteBufOutputStream(data)) {
				CompressedStreamTools.writeCompressed(packetData, outputStream);
			}
			catch (IOException e) {
				AncientWarfareStructure.LOG.error("Error writing structure template packet: ", e);
			}
		}
	}

	@Override
	protected void readFromStream(ByteBuf data) {
		try (ByteBufInputStream inputStream = new ByteBufInputStream(data)) {
			packetData = CompressedStreamTools.readCompressed(inputStream);
		}
		catch (IOException e) {
			AncientWarfareStructure.LOG.error("Error reading structure template packet: ", e);
		}
	}

	@Override
	protected void execute() {
		StructureTemplateManager.onTemplateData(packetData);
	}
}
