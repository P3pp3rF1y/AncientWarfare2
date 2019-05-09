package net.shadowmage.ancientwarfare.structure.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.core.util.PacketHelper;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;

import java.io.IOException;

public class PacketStructureMap extends PacketBase {
	private NBTTagCompound packetData;

	public PacketStructureMap(NBTTagCompound packetData) {
		this.packetData = packetData;
	}

	public PacketStructureMap() {}

	@Override
	protected void writeToStream(ByteBuf data) {
		PacketHelper.writeNBTTag(data, packetData);
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		packetData = PacketHelper.readNBTTag(data);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void execute() {
		StructureMap structureMap = AWGameData.INSTANCE.getData(Minecraft.getMinecraft().world, StructureMap.class);
		structureMap.synchronizeFromNBT(packetData);
	}
}
