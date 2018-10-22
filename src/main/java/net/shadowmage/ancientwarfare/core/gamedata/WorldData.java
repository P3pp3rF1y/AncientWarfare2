package net.shadowmage.ancientwarfare.core.gamedata;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WorldData extends WorldSavedData {
	NBTTagCompound dataTag = new NBTTagCompound();

	private Set<UUID> playersGivenManual = new HashSet<>();

	/*
	 * reflection constructor for mc-vanilla code
	 */
	public WorldData(String par) {
		super(par);
	}

	public final boolean get(String key) {
		return dataTag.getBoolean(key);
	}

	public final void set(String name, boolean val) {
		dataTag.setBoolean(name, val);
		markDirty();
	}

	public final void addPlayerThatWasGivenManual(EntityPlayer player) {
		playersGivenManual.add(player.getUniqueID());
		markDirty();
	}

	public boolean wasPlayerGivenManual(EntityPlayer player) {
		return playersGivenManual.contains(player.getUniqueID());
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		this.dataTag = tag.getCompoundTag("AWWorldData");
		playersGivenManual = NBTHelper.getUniqueIdSet(dataTag.getTag("playersGivenManual"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		dataTag.setTag("playersGivenManual", NBTHelper.getNBTUniqueIdList(playersGivenManual));
		tag.setTag("AWWorldData", this.dataTag);
		return tag;
	}

}
