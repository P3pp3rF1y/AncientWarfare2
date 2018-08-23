package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.interfaces.ISinger;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;

import javax.annotation.Nullable;
import java.util.Optional;

public class TileSoundBlock extends TileUpdatable implements ISinger, ITickable {
	private int currentDelay;
	private int tuneIndex = -1;
	private PositionedSoundRecord currentTune = null;
	private int playerCheckDelay;
	private int playerRange = 20;
	private SongPlayData tuneData;
	private IBlockState disguiseState;

	public TileSoundBlock() {
		tuneData = new SongPlayData();
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			return;
		}

		if (!validateAndGetPlaying() && tuneData.size() > 0 && currentDelay-- <= 0) {
			if (tuneData.getPlayOnPlayerEntry()) {
				if (playerCheckDelay-- <= 0) {
					playerCheckDelay = 20;
					if (Minecraft.getMinecraft().player.getDistance(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= playerRange) {
						startSong();
					}
				}
			} else {
				startSong();
			}
		}
	}

	private Optional<PositionedSoundRecord> getCurrentTune() {
		return Optional.ofNullable(currentTune);
	}

	@SideOnly(Side.CLIENT)
	private void startSong() {
		if (tuneData.getIsRandom()) {
			tuneIndex = 0;
			if (tuneData.size() > 0) {
				tuneIndex = world.rand.nextInt(tuneData.size());

				setCurrentTune(tuneData.get(tuneIndex).getSound());
			}
		} else {
			tuneIndex = tuneIndex + 1 < tuneData.size() ? tuneIndex + 1 : 0;
			setCurrentTune(tuneData.get(tuneIndex).getSound());
		}
		getCurrentTune().ifPresent(t -> Minecraft.getMinecraft().getSoundHandler().playSound(t));
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
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tuneData.readFromNBT(tag.getCompoundTag("tuneData"));
		tuneIndex = tag.getInteger("tuneIndex");
		playerRange = tag.getInteger("range");
		String id = tag.getString("block");
		if (!id.isEmpty()) {
			disguiseState = Block.getBlockFromName(id).getStateFromMeta(tag.getInteger("meta"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("tuneData", tuneData.writeToNBT(new NBTTagCompound()));
		tag.setInteger("tuneIndex", tuneIndex);
		tag.setInteger("range", playerRange);
		if (disguiseState != null) {
			tag.setString("block", disguiseState.getBlock().getRegistryName().toString());
			tag.setInteger("meta", disguiseState.getBlock().getMetaFromState(disguiseState));
		}

		return tag;
	}

	public SongPlayData getSongs() {
		return tuneData;
	}

	public void setPlayerRange(int value) {
		playerRange = value;
	}

	public int getPlayerRange() {
		return playerRange;
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

	@SideOnly(Side.CLIENT)
	@SuppressWarnings("squid:S3655") //exiting early if tune not present just Sonar is not able to figure that out
	private boolean validateAndGetPlaying() {
		if (!getCurrentTune().isPresent()) {
			return false;
		}
		ISound positionedsoundrecord = getCurrentTune().get();
		boolean isPlaying = Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(positionedsoundrecord);
		if (!isPlaying) {
			resetCurrentTune();
		}

		return isPlaying;
	}

	private void resetCurrentTune() {
		tuneIndex = -1;
		currentTune = null;
		int diff = Math.abs(tuneData.getMaxDelay() - tuneData.getMinDelay());
		currentDelay = tuneData.getMinDelay() + diff > 0 ? world.rand.nextInt(diff) : 0;
	}

	@SideOnly(Side.CLIENT)
	private void setCurrentTune(@Nullable SoundEvent currentTune) {
		if (currentTune != null) {
			this.currentTune = PositionedSoundRecord.getRecordSoundRecord(currentTune, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
		}
	}

	@Override
	public void invalidate() {
		if (world.isRemote) {
			getCurrentTune().ifPresent(t -> Minecraft.getMinecraft().getSoundHandler().stopSound(t));
		}
		super.invalidate();
	}
}
