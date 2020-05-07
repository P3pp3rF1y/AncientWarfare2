package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.item.ItemRecord;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.BuildConfig;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
		if (maxDelay < minDelay) {
			maxDelay = minDelay;
		}
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
		private static final String AUTO_LOAD_PREFIX = "auto_load/";
		private ResourceLocation soundRegistryName;
		@Nullable
		private SoundEvent sound;
		private float length;//length in seconds, used to determine when count down for next tune should start
		private int volume;// percentage, as integer 0 = 0%, 100=100%, 150=150%

		private SongEntry() {
			soundRegistryName = null;
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
			soundRegistryName = sound == null ? null : sound.getRegistryName();
			if (sound != null) {
				boolean isRecord = sound.getSoundName().getResourcePath().startsWith("records.") || ItemRecord.getBySound(sound) != null;
				if (isRecord && length() < 120) {
					setLength(120);
				}
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

		public Optional<SoundEvent> getSound() {
			return Optional.ofNullable(sound);
		}

		public float length() {
			return length;
		}

		public void readFromNBT(NBTTagCompound tag) {
			if (tag.hasKey("name")) {
				soundRegistryName = new ResourceLocation(tag.getString("name"));
				sound = ForgeRegistries.SOUND_EVENTS.getValue(soundRegistryName);
				if (BuildConfig.UNSTABLE && sound == null) {
					trySoundLookup();
				}
			}
			length = tag.getFloat("length");
			volume = tag.getInteger("volume");
		}

		private void trySoundLookup() {
			if (!soundRegistryName.getResourceDomain().startsWith(AncientWarfareCore.MOD_ID) || !soundRegistryName.getResourcePath().startsWith(AUTO_LOAD_PREFIX)) {
				return;
			}

			String soundDomain = soundRegistryName.getResourceDomain();
			String soundName = soundRegistryName.getResourcePath().substring(AUTO_LOAD_PREFIX.length());

			SoundEvent found = null;
			for (SoundEvent soundEvent : ForgeRegistries.SOUND_EVENTS) {
				ResourceLocation soundEventRegistryName = soundEvent.getRegistryName();
				//noinspection ConstantConditions
				String resPath = soundEventRegistryName.getResourcePath();
				if (soundEventRegistryName.getResourceDomain().equals(soundDomain)
						&& (resPath.equals(soundName) || resPath.endsWith("/" + soundName))) {
					found = soundEvent;
					if (!resPath.startsWith(AUTO_LOAD_PREFIX)) {
						break;
					}
				}
			}

			if (found != null) {
				AncientWarfareCore.LOG.info("Sound {} replaced with automatically found {}", soundRegistryName, found.getRegistryName());
				sound = found;
				soundRegistryName = found.getRegistryName();
			} else {
				AncientWarfareCore.LOG.error("Sound {} no longer exists in the sound registry and no replacement was automatically found.\n"
						+ "The sound name will be saved in case the sound is reregistered.", soundRegistryName);
			}
		}

		public NBTTagCompound writeToNBT(NBTTagCompound tag) {
			if (soundRegistryName != null) {
				tag.setString("name", soundRegistryName.toString());
			}
			tag.setFloat("length", length);
			tag.setInteger("volume", volume);
			return tag;
		}
	}
}