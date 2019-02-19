package net.shadowmage.ancientwarfare.structure.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.core.util.PacketHelper;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureEntry;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;

import java.io.IOException;

public class PacketStructureEntry extends PacketBase {
	private int dimension;
	private int cx;
	private int cz;
	private StructureEntry entry;
	private boolean unique;

	public PacketStructureEntry() {}

	public PacketStructureEntry(int dimension, int cx, int cz, StructureEntry entry, boolean unique) {
		this.dimension = dimension;
		this.cx = cx;
		this.cz = cz;
		this.unique = unique;
		this.entry = entry;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		data.writeInt(dimension);
		data.writeInt(cx);
		data.writeInt(cz);
		data.writeBoolean(unique);
		NBTTagCompound entryTag = new NBTTagCompound();
		entry.writeToNBT(entryTag);
		PacketHelper.writeNBTTag(data, entryTag);
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		dimension = data.readInt();
		cx = data.readInt();
		cz = data.readInt();
		unique = data.readBoolean();
		entry = new StructureEntry();
		entry.readFromNBT(PacketHelper.readNBTTag(data));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void execute() {
		WorldClient world = Minecraft.getMinecraft().world;
		if (world != null) {
			AWGameData.INSTANCE.getData(world, StructureMap.class).setGeneratedAt(dimension, cx, cz, entry, unique);
		}
	}
}
