package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.tile.TileSoundBlock;
import net.shadowmage.ancientwarfare.structure.util.BlockSongPlayData;

public class ContainerSoundBlock extends ContainerTileBase<TileSoundBlock> {
	private static final String TUNE_DATA_TAG = "tuneData";
	public BlockSongPlayData data;

	public ContainerSoundBlock(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);
		data = tileEntity.getSongs();
	}

	@Override
	public void sendInitData() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag(TUNE_DATA_TAG, data.writeToNBT(new NBTTagCompound()));
		sendDataToClient(tag);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey(TUNE_DATA_TAG)) {
			tileEntity.getSongs().readFromNBT(tag.getCompoundTag(TUNE_DATA_TAG));
			data = tileEntity.getSongs();
		}
		if (!tileEntity.getWorld().isRemote) {
			tileEntity.resetStateValues();
			tileEntity.markDirty();
			BlockTools.notifyBlockUpdate(tileEntity);
		}
		refreshGui();
	}

	public void sendTuneDataToServer(EntityPlayer player) {
		if (player.world.isRemote)//handles sending new/updated/changed data back to server on GUI close.  the last GUI to close will be the one whose data 'sticks'
		{
			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag(TUNE_DATA_TAG, data.writeToNBT(new NBTTagCompound()));
			sendDataToServer(tag);
		}
	}
}
