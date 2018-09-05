package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;
import net.shadowmage.ancientwarfare.structure.tile.TileSoundBlock;

public class ContainerSoundBlock extends ContainerTileBase<TileSoundBlock> {
	private static final String TUNE_DATA_TAG = "tuneData";
	private static final String RANGE_TAG = "range";
	public SongPlayData data;
	public int range;

	public ContainerSoundBlock(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);
		data = tileEntity.getSongs();
		range = tileEntity.getPlayerRange();
	}

	@Override
	public void sendInitData() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag(TUNE_DATA_TAG, data.writeToNBT(new NBTTagCompound()));
		tag.setInteger(RANGE_TAG, range);
		sendDataToClient(tag);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey(TUNE_DATA_TAG)) {
			tileEntity.getSongs().readFromNBT(tag.getCompoundTag(TUNE_DATA_TAG));
			data = tileEntity.getSongs();
		}
		range = tag.getInteger(RANGE_TAG);
		tileEntity.setPlayerRange(range);
		if (!tileEntity.getWorld().isRemote) {
			tileEntity.markDirty();
		}
		refreshGui();
	}

	public void sendTuneDataToServer(EntityPlayer player) {
		if (player.world.isRemote)//handles sending new/updated/changed data back to server on GUI close.  the last GUI to close will be the one whose data 'sticks'
		{
			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag(TUNE_DATA_TAG, data.writeToNBT(new NBTTagCompound()));
			tag.setInteger(RANGE_TAG, range);
			sendDataToServer(tag);
		}
	}
}
