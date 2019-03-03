package net.shadowmage.ancientwarfare.structure.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;

import java.util.HashMap;
import java.util.Map;

public class BlockSongPlayData extends SongPlayData {
	public static final String SOUND_RANGE_TAG = "soundRange";
	private boolean playOnce = false;
	private int playerRange = 20;
	private boolean limitedRepetitions = false;
	private int repetitions = 1;
	private boolean whenInRange = false;
	private TimeOfDay timeOfDay = TimeOfDay.ANY;
	private boolean protectionFlagTurnOff = false;
	private int soundRange = 16;

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		playOnce = tag.getBoolean("playOnce");
		playerRange = tag.getInteger("playerRange");
		limitedRepetitions = tag.getBoolean("limitedRepetitions");
		repetitions = tag.getInteger("repetitions");
		whenInRange = tag.getBoolean("whenInRange");
		timeOfDay = TimeOfDay.getById(tag.getInteger("timeOfDay"));
		protectionFlagTurnOff = tag.getBoolean("protectionFlagTurnOff");
		setSoundRange(tag.hasKey(SOUND_RANGE_TAG) ? tag.getInteger(SOUND_RANGE_TAG) : 64);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setBoolean("playOnce", playOnce);
		tag.setInteger("playerRange", playerRange);
		tag.setBoolean("limitedRepetitions", limitedRepetitions);
		tag.setInteger("repetitions", repetitions);
		tag.setBoolean("whenInRange", whenInRange);
		tag.setInteger("timeOfDay", timeOfDay.getId());
		tag.setBoolean("protectionFlagTurnOff", protectionFlagTurnOff);
		tag.setInteger(SOUND_RANGE_TAG, soundRange);
		return tag;
	}

	public void setPlayerRange(int playerRange) {
		this.playerRange = playerRange;
	}

	public int getPlayerRange() {
		return playerRange;
	}

	public void setPlayOnce(boolean playOnce) {
		this.playOnce = playOnce;
	}

	public boolean getPlayOnce() {
		return playOnce;
	}

	public void setLimitedRepetitions(boolean limitedRepetitions) {
		this.limitedRepetitions = limitedRepetitions;
	}

	public boolean getLimitedRepetitions() {
		return limitedRepetitions;
	}

	public int getRepetitions() {
		return repetitions;
	}

	public void setRepetitions(int repetitions) {
		this.repetitions = repetitions;
	}

	public boolean getWhenInRange() {
		return whenInRange;
	}

	public void setWhenInRange(boolean whenInRange) {
		this.whenInRange = whenInRange;
	}

	public void setTimeOfDay(TimeOfDay timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	public TimeOfDay getTimeOfDay() {
		return timeOfDay;
	}

	public void setProtectionFlagTurnOff(boolean protectionFlagTurnOff) {
		this.protectionFlagTurnOff = protectionFlagTurnOff;
	}

	public boolean getProtectionFlagTurnOff() {
		return protectionFlagTurnOff;
	}

	public void setSoundRange(int value) {
		soundRange = Math.max(16, value);
	}

	public int getSoundRange() {
		return soundRange;
	}

	public enum TimeOfDay {
		ANY(0),
		DAY(1) {
			@Override
			public boolean takesPlaceNow(World world) {
				return isDayTimeClient(world);
			}
		},
		NIGHT(2) {
			@Override
			public boolean takesPlaceNow(World world) {
				return !isDayTimeClient(world);
			}
		};

		private int id;

		TimeOfDay(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		private static final Map<Integer, TimeOfDay> VALUES = new HashMap<>();

		static {
			for (TimeOfDay value : values()) {
				VALUES.put(value.getId(), value);
			}
		}

		public static TimeOfDay getById(int id) {
			return VALUES.get(id);
		}

		@SuppressWarnings("squid:S1172") // used in overrides
		public boolean takesPlaceNow(World world) {
			return true;
		}

		private static boolean isDayTimeClient(World world) {
			long time = world.getWorldTime();
			return time >= 23500 || time <= 12500;
		}
	}
}
