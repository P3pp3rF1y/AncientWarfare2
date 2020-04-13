package net.shadowmage.ancientwarfare.structure.gamedata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

public class TownEntry {
	private StructureBB bb;
	private boolean preventNaturalHostileSpawns;
	private TownMap townMap;
	private boolean isConquered = false;

	public TownEntry(StructureBB bb, boolean preventNaturalHostileSpawns) {
		this.bb = bb;
		this.preventNaturalHostileSpawns = preventNaturalHostileSpawns;
	}

	public TownEntry setTownMap(TownMap townMap) {
		this.townMap = townMap;
		return this;
	}

	public void setConquered() {
		isConquered = true;
		preventNaturalHostileSpawns = false;
		if (townMap != null) {
			townMap.markDirty();
		}
	}

	public static TownEntry deserializeNBT(NBTTagCompound tag) {
		StructureBB bb = new StructureBB(BlockPos.ORIGIN, BlockPos.ORIGIN);
		bb.deserializeNBT(tag.getCompoundTag("bb"));
		return new TownEntry(bb, tag.getBoolean("preventNaturalHostileSpawns"));
	}

	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("bb", bb.serializeNBT());
		tag.setBoolean("preventNaturalHostileSpawns", preventNaturalHostileSpawns);
		return tag;
	}

	public boolean shouldPreventNaturalHostileSpawns() {
		return preventNaturalHostileSpawns;
	}

	public StructureBB getBB() {
		return bb;
	}

	public boolean getConquered() {
		return isConquered;
	}
}
