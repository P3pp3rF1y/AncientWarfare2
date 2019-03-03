package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.interfaces.ISinger;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.network.PacketSoundBlockPlayerSpecValues;
import net.shadowmage.ancientwarfare.structure.util.BlockSongPlayData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TileSoundBlock extends TileUpdatable implements ISinger, ITickable {
	private static final long MIN_TIME_BETWEEN_ENTRY_PLAYS = 1200;
	private int currentDelay;
	private int tuneIndex = -1;
	private int playerCheckDelay;
	private BlockSongPlayData tuneData;
	private IBlockState disguiseState;

	private boolean stoppedForAll = false;
	private Map<UUID, PersistentValues> playerSpecificValues = new HashMap<>();

	public TileSoundBlock() {
		tuneData = new BlockSongPlayData();
	}

	@Override
	public void update() {
		if (!world.isRemote || isStopped() || validateAndGetPlaying() || !tuneData.getTimeOfDay().takesPlaceNow(world)) {
			return;
		}

		if (tuneData.getPlayOnPlayerEntry()) {
			processPlayerEntry();
		} else {
			if (tuneData.getWhenInRange() && !isPlayerInRange()) {
				return;
			}

			if (--currentDelay <= 0) {
				startSong();
				if (tuneData.getLimitedRepetitions() && incNumberOfTimesRepeated() >= tuneData.getRepetitions()) {
					setStopped();
				}
			}
		}
	}

	private int incNumberOfTimesRepeated() {
		PersistentValues values = getOrCreatePlayerSpecificValues();
		values.numberOfTimesRepeated++;
		syncValuesToServer(values);
		return values.numberOfTimesRepeated;
	}

	private void setStopped() {
		if (world.isRemote) {
			PersistentValues values = getOrCreatePlayerSpecificValues();
			values.stopped = true;
			syncValuesToServer(values);
		}
	}

	private void syncValuesToServer(PersistentValues values) {
		NetworkHandler.sendToServer(new PacketSoundBlockPlayerSpecValues(pos, values));
	}

	private boolean isStopped() {
		return stoppedForAll || world.isRemote && getPlayerSpecificValues().map(values -> values.stopped).orElse(false);
	}

	private PersistentValues getOrCreatePlayerSpecificValues() {
		return AncientWarfareStructure.proxy.getPlayer().map(player -> {
			if (!playerSpecificValues.containsKey(player.getUniqueID())) {
				playerSpecificValues.put(player.getUniqueID(), new PersistentValues());
			}
			return playerSpecificValues.get(player.getUniqueID());
		}).orElse(new PersistentValues());
	}

	private Optional<PersistentValues> getPlayerSpecificValues() {
		return AncientWarfareStructure.proxy.getPlayer().map(player -> Optional.ofNullable(playerSpecificValues.get(player.getUniqueID())))
				.orElse(Optional.empty());
	}

	public void turnOffByProtectionFlag() {
		if (tuneData.getProtectionFlagTurnOff()) {
			stoppedForAll = true;
			markDirty();
			BlockTools.notifyBlockUpdate(this);
		}
	}

	private void processPlayerEntry() {
		if (playerCheckDelay-- <= 0) {
			playerCheckDelay = 20;
			if (isPlayerInRange()) {
				if (getLastTimePlayerNear() < 0 || world.getTotalWorldTime() - getLastTimePlayerNear() > MIN_TIME_BETWEEN_ENTRY_PLAYS) {
					startSong();
					if (tuneData.getPlayOnce()) {
						setStopped();
					} else {
						setLastTimePlayerNear();
					}
				} else {
					setLastTimePlayerNear();
				}
				markDirty();
			}
		}
	}

	private void setLastTimePlayerNear() {
		PersistentValues values = getOrCreatePlayerSpecificValues();
		values.lastTimePlayerNear = world.getTotalWorldTime();
		syncValuesToServer(values);
	}

	private long getLastTimePlayerNear() {
		return getPlayerSpecificValues().map(PersistentValues::getLastTimePlayerNear).orElse(Long.MAX_VALUE);
	}

	private boolean isPlayerInRange() {
		return AncientWarfareStructure.proxy.getClientPlayerDistanceTo(pos) <= tuneData.getPlayerRange();
	}

	private void startSong() {
		if (tuneData.size() == 0) {
			return;
		}

		if (tuneData.getIsRandom()) {
			tuneIndex = 0;
			if (tuneData.size() > 0) {
				tuneIndex = world.rand.nextInt(tuneData.size());
				tuneData.get(tuneIndex).getSound().ifPresent(s -> AncientWarfareStructure.proxy.setSoundAt(pos, s, tuneData.getSoundRange() / 16f));
			}
		} else {
			tuneIndex = tuneIndex + 1 < tuneData.size() ? tuneIndex + 1 : 0;
			tuneData.get(tuneIndex).getSound().ifPresent(s -> AncientWarfareStructure.proxy.setSoundAt(pos, s, tuneData.getSoundRange() / 16f));
		}
		AncientWarfareStructure.proxy.playSoundAt(pos);
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		writeToNBT(tag);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		readFromNBT(tag);

		if (isStopped() && AncientWarfareStructure.proxy.isSoundPlayingAt(pos)) {
			AncientWarfareStructure.proxy.stopSoundAt(pos);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tuneData.readFromNBT(tag.getCompoundTag("tuneData"));
		tuneIndex = tag.getInteger("tuneIndex");
		if (tag.hasKey("range")) {
			tuneData.setPlayerRange(tag.getInteger("range"));
		}
		String id = tag.getString("block");
		if (!id.isEmpty()) {
			disguiseState = Block.getBlockFromName(id).getStateFromMeta(tag.getInteger("meta"));
		}
		stoppedForAll = tag.getBoolean("stoppedForAll");
		readPlayerSpecificValues(tag);
	}

	private void readPlayerSpecificValues(NBTTagCompound tag) {
		playerSpecificValues.clear();
		NBTTagList list = tag.getTagList("playerSpecificValues", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound pair = list.getCompoundTagAt(i);
			PersistentValues values = new PersistentValues();
			values.deserializeNBT(pair.getCompoundTag("values"));
			playerSpecificValues.put(pair.getUniqueId("playerId"), values);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("tuneData", tuneData.writeToNBT(new NBTTagCompound()));
		tag.setInteger("tuneIndex", tuneIndex);
		if (disguiseState != null) {
			tag.setString("block", disguiseState.getBlock().getRegistryName().toString());
			tag.setInteger("meta", disguiseState.getBlock().getMetaFromState(disguiseState));
		}
		tag.setBoolean("stoppedForAll", stoppedForAll);
		writePlayerSpecificValues(tag);
		return tag;
	}

	private void writePlayerSpecificValues(NBTTagCompound tag) {
		NBTTagList list = new NBTTagList();
		for (Map.Entry<UUID, PersistentValues> entry : playerSpecificValues.entrySet()) {
			NBTTagCompound pair = new NBTTagCompound();
			pair.setUniqueId("playerId", entry.getKey());
			pair.setTag("values", entry.getValue().serializeNBT());
			list.appendTag(pair);
		}
		tag.setTag("playerSpecificValues", list);
	}

	public BlockSongPlayData getSongs() {
		return tuneData;
	}

	public IBlockState getDisguiseState() {
		return disguiseState;
	}

	public void setDisguiseState(ItemStack itemStack) {
		Block block = Block.getBlockFromItem(itemStack.getItem());
		if (block != AWStructureBlocks.SOUND_BLOCK && block.isFullCube(null) && block.isOpaqueCube(null)) {
			disguiseState = block.getStateFromMeta(itemStack.getMetadata());
			BlockTools.notifyBlockUpdate(this);
			world.notifyNeighborsRespectDebug(pos, this.blockType, true);
			markDirty();
		}
	}

	private boolean validateAndGetPlaying() {
		if (!AncientWarfareStructure.proxy.hasSoundAt(pos)) {
			return false;
		}
		boolean isPlaying = AncientWarfareStructure.proxy.isSoundPlayingAt(pos);
		if (!isPlaying) {
			resetCurrentTune();
		}

		return isPlaying;
	}

	private void resetCurrentTune() {
		tuneIndex = -1;
		AncientWarfareStructure.proxy.resetSoundAt(pos);
		if (!tuneData.getPlayOnPlayerEntry()) {
			int diff = Math.abs(tuneData.getMaxDelay() - tuneData.getMinDelay()) * 20;
			currentDelay = tuneData.getMinDelay() * 20 + (diff > 0 ? world.rand.nextInt(diff) : 0);
		}
	}

	@Override
	public void invalidate() {
		AncientWarfareStructure.proxy.stopSoundAt(pos);
		super.invalidate();
	}

	public void updatePlayerSpecValues(UUID playerId, boolean stopped, long lastTimePlayerNear, int numberOfTimesRepeated) {
		if (!playerSpecificValues.containsKey(playerId)) {
			playerSpecificValues.put(playerId, new PersistentValues());
		}
		PersistentValues values = playerSpecificValues.get(playerId);
		values.stopped = stopped;
		values.lastTimePlayerNear = lastTimePlayerNear;
		values.numberOfTimesRepeated = numberOfTimesRepeated;
		markDirty();
	}

	public void resetStateValues() {
		stoppedForAll = false;
		playerSpecificValues.clear();
		BlockTools.notifyBlockUpdate(this);
	}

	public class PersistentValues implements INBTSerializable<NBTTagCompound> {
		private long lastTimePlayerNear = -1;
		private boolean stopped = false;
		private int numberOfTimesRepeated = 0;

		public boolean isStopped() {
			return stopped;
		}

		public int getNumberOfTimesRepeated() {
			return numberOfTimesRepeated;
		}

		public long getLastTimePlayerNear() {
			return lastTimePlayerNear;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setLong("lastTimePlayerNear", lastTimePlayerNear);
			tag.setBoolean("stopped", stopped);
			tag.setInteger("numberOfTimesRepeated", numberOfTimesRepeated);
			return tag;
		}

		@Override
		public void deserializeNBT(NBTTagCompound tag) {
			lastTimePlayerNear = tag.getLong("lastTimePlayerNear");
			stopped = tag.getBoolean("stopped");
			numberOfTimesRepeated = tag.getInteger("numberOfTimesRepeated");
		}
	}
}
