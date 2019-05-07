package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteQuarry;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedSite;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

public class ContainerWorksiteQuarryBounds extends ContainerTileBase<WorkSiteQuarry> {

	public int maxHeight;

	public ContainerWorksiteQuarryBounds(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);
		maxHeight = tileEntity.height;
	}

	@Override
	public void sendInitData() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("height", maxHeight);
		sendDataToClient(tag);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		maxHeight = tag.getInteger("height");
		if (tag.hasKey("guiClosed")) {
			getWorksite().onBoundsAdjusted();
			getWorksite().onPostBoundsAdjusted();
			BlockTools.notifyBlockUpdate(player.world, getPos());
		}
		if (!player.world.isRemote) {
			tileEntity.height = maxHeight;
			tileEntity.markDirty();//mark dirty so it get saved to nbt
		}
		refreshGui();
	}

	public void sendSettingsToServer() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("height", maxHeight);
		sendDataToServer(tag);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		boolean send = false;
		if (maxHeight != tileEntity.height) {
			maxHeight = tileEntity.height;
			send = true;
		}
		if (send) {
			sendInitData();
		}
	}

	public void onClose(boolean boundsAdjusted) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean("guiClosed", true);
		if (boundsAdjusted) {
			tag.setInteger("height", maxHeight);
		}
		sendDataToServer(tag);
	}

	public BlockPos getPos() {
		return tileEntity.getPos();
	}

	public int getX() {
		return tileEntity.getPos().getX();
	}

	public int getY() {
		return tileEntity.getPos().getY();
	}

	public int getZ() {
		return tileEntity.getPos().getZ();
	}

	public IBoundedSite getWorksite() {
		return tileEntity;
	}
}
