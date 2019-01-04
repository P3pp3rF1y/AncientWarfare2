package net.shadowmage.ancientwarfare.structure.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;

import java.io.IOException;

public class PacketStructure extends PacketBase {

	public NBTTagCompound packetData = new NBTTagCompound();

	public PacketStructure() {
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		if (packetData != null) {
			ByteBufOutputStream bbos = new ByteBufOutputStream(data);
			try {
				CompressedStreamTools.writeCompressed(packetData, bbos);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			try {
				bbos.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void readFromStream(ByteBuf data) {
		ByteBufInputStream bbis = new ByteBufInputStream(data);
		try {
			packetData = CompressedStreamTools.readCompressed(bbis);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bbis.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void execute() {
		StructureTemplateManager.onTemplateData(packetData);
	}
}
