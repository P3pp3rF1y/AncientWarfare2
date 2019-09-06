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

	@SuppressWarnings("unused") //necessary for client side handling
	public PacketStructureEntry() {}

	public PacketStructureEntry(int dimension, int cx, int cz, StructureEntry entry) {
		this.dimension = dimension;
		this.cx = cx;
		this.cz = cz;
		this.entry = entry;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		data.writeInt(dimension);
		data.writeInt(cx);
		data.writeInt(cz);
		NBTTagCompound entryTag = new NBTTagCompound();
		entry.writeToNBT(entryTag);
		PacketHelper.writeNBTTag(data, entryTag);
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		dimension = data.readInt();
		cx = data.readInt();
		cz = data.readInt();
		entry = new StructureEntry();
		entry.readFromNBT(PacketHelper.readNBTTag(data));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void execute() {
		WorldClient world = Minecraft.getMinecraft().world;
		if (world != null) {
			//passing false to unique as it's not needed client side, but client side still uses the same structure data storage that needs some value there
			AWGameData.INSTANCE.getData(world, StructureMap.class).setGeneratedAt(dimension, cx, cz, entry, false);
		}
	}
}
