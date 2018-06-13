package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.item.ItemRecord;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SongPlayData {
	private boolean random = false;
	private boolean playOnPlayerEntry = false;
	private int minDelay;
	private int maxDelay;
	private List<SongEntry> tunes = new ArrayList<>();

	public int size() {
		return tunes.size();
	}

	public SongEntry get(int index) {
		return tunes.get(index);
	}

	public void addNewEntry() {
		SongEntry e = new SongEntry();
		tunes.add(e);
	}

	public void decrementEntry(int index) {
		if (index <= 0 || index >= tunes.size()) {
			return;
		}
		SongEntry e = tunes.remove(index);
		index--;
		tunes.add(index, e);
	}

	public void incrementEntry(int index) {
		if (index < 0 || index >= tunes.size() - 1) {
			return;
		}
		SongEntry e = tunes.remove(index);
		index++;
		tunes.add(index, e);
	}

	public void deleteEntry(int index) {
		if (index < 0 || index >= tunes.size()) {
			return;
		}
		tunes.remove(index);
	}

	public int getMinDelay() {
		return minDelay;
	}

	public int getMaxDelay() {
		return maxDelay;
	}

	public boolean getPlayOnPlayerEntry() {
		return playOnPlayerEntry;
	}

	public boolean getIsRandom() {
		return random;
	}

	public void setMinDelay(int val) {
		minDelay = val;
	}

	public void setMaxDelay(int val) {
		maxDelay = val;
	}

	public void setPlayOnPlayerEntry(boolean val) {
		playOnPlayerEntry = val;
	}

	public void setRandom(boolean val) {
		random = val;
	}

	public void readFromNBT(NBTTagCompound tag) {
		minDelay = tag.getInteger("minDelay");
		maxDelay = tag.getInteger("maxDelay");
		random = tag.getBoolean("random");
		playOnPlayerEntry = tag.getBoolean("playerEntry");
		tunes.clear();
		SongEntry d;
		NBTTagList l = tag.getTagList("entries", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < l.tagCount(); i++) {
			d = new SongEntry();
			d.readFromNBT(l.getCompoundTagAt(i));
			tunes.add(d);
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("minDelay", minDelay);
		tag.setInteger("maxDelay", maxDelay);
		tag.setBoolean("random", random);
		tag.setBoolean("playerEntry", playOnPlayerEntry);
		NBTTagList l = new NBTTagList();
		for (SongEntry tune : tunes) {
			l.appendTag(tune.writeToNBT(new NBTTagCompound()));
		}
		tag.setTag("entries", l);
		return tag;
	}

	public static final class SongEntry {
		@Nullable
		SoundEvent sound;
		float length;//length in seconds, used to determine when count down for next tune should start
		int volume;// percentage, as integer 0 = 0%, 100=100%, 150=150%

		public SongEntry() {
			sound = null;
			length = 5;
			volume = 100;
		}

		public void setLength(float length) {
			this.length = length;
		}

		@SideOnly(Side.CLIENT)
		public void setSound(@Nullable SoundEvent sound) {
			this.sound = sound;
			if (sound != null) {
				boolean isRecord = sound.getSoundName().getResourcePath().startsWith("records.") || ItemRecord.getBySound(sound) != null;
				if (isRecord && length() < 120)
					setLength(120);
			}
		}

		public void setVolume(int volume) {
			this.volume = volume;
		}

		public int volume() {
			return volume;
		}

		@SideOnly(Side.CLIENT)
		public String name() {
			return sound != null ? sound.getSoundName().toString() : "";
		}

		public SoundEvent getSound() {
			return sound;
		}

		public float length() {
			return length;
		}

		public void readFromNBT(NBTTagCompound tag) {
			if (tag.hasKey("name")) {
				sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(tag.getString("name")));
			}
			length = tag.getFloat("length");
			volume = tag.getInteger("volume");
		}

		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			if (sound != null) {
				tag.setString("name", sound.getRegistryName().toString());
			}
			tag.setFloat("length", length);
			tag.setInteger("volume", volume);
			return tag;
		}

		public int play(World world, BlockPos pos) {
			world.playRecord(pos, sound);
			world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, sound, SoundCategory.BLOCKS, volume * 0.03F, 1);
			return (int) (length * 20);//seconds(decimal) to ticks conversion
		}
	}

}