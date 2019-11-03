package net.shadowmage.ancientwarfare.structure.gamedata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

public class StructureEntry {
	public String name;
	private int value;
	public final StructureBB bb;
	private BlockPos protectionFlagPos = BlockPos.ORIGIN;
	private int cx;
	private int cz;

	public void setProtectionFlagPos(BlockPos protectionFlagPos) {
		this.protectionFlagPos = protectionFlagPos;
	}

	public StructureEntry(int x, int y, int z, EnumFacing face, StructureTemplate template) {
		name = template.name;
		bb = new StructureBB(new BlockPos(x, y, z), face, template.getSize(), template.getOffset());
		cx = x >> 4;
		cz = z >> 4;
		value = template.getValidationSettings().getClusterValue();
	}

	public StructureEntry(StructureBB bb, String name, int value, int cx, int cz) {
		this.name = name;
		this.bb = bb;
		this.value = value;
		this.cx = cx;
		this.cz = cz;
	}

	public StructureEntry() {
		bb = new StructureBB(BlockPos.ORIGIN, BlockPos.ORIGIN);
	}//NBT constructor

	public void writeToNBT(NBTTagCompound tag) {
		tag.setString("name", name);
		tag.setInteger("value", value);
		tag.setIntArray("bb", new int[] {bb.min.getX(), bb.min.getY(), bb.min.getZ(), bb.max.getX(), bb.max.getY(), bb.max.getZ()});
		tag.setLong("protectionFlagPos", protectionFlagPos.toLong());
		tag.setInteger("cx", cx);
		tag.setInteger("cz", cz);
	}

	public void readFromNBT(NBTTagCompound tag) {
		name = tag.getString("name");
		value = tag.getInteger("value");
		int[] datas = tag.getIntArray("bb");
		if (datas.length >= 6) {
			bb.min = new BlockPos(datas[0], datas[1], datas[2]);
			bb.max = new BlockPos(datas[3], datas[4], datas[5]);
		}
		protectionFlagPos = BlockPos.fromLong(tag.getLong("protectionFlagPos"));
		cx = tag.getInteger("cx");
		cz = tag.getInteger("cz");
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public StructureBB getBB() {
		return bb;
	}

	public BlockPos getProtectionFlagPos() {
		return protectionFlagPos;
	}

	public int getChunkZ() {
		return cz;
	}

	public int getChunkX() {
		return cx;
	}
}
