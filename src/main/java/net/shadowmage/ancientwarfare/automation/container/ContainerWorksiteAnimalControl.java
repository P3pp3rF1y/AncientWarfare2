package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteAnimalFarm;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;

public class ContainerWorksiteAnimalControl extends ContainerTileBase<WorkSiteAnimalFarm> {

	public int maxPigs;
	public int maxSheep;
	public int maxCows;
	public int maxChickens;

	public ContainerWorksiteAnimalControl(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);
		maxPigs = tileEntity.maxPigCount;
		maxSheep = tileEntity.maxSheepCount;
		maxCows = tileEntity.maxCowCount;
		maxChickens = tileEntity.maxChickenCount;
	}

	@Override
	public void sendInitData() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("pigs", maxPigs);
		tag.setInteger("cows", maxCows);
		tag.setInteger("sheep", maxSheep);
		tag.setInteger("chickens", maxChickens);
		sendDataToClient(tag);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		maxCows = tag.getInteger("cows");
		maxPigs = tag.getInteger("pigs");
		maxChickens = tag.getInteger("chickens");
		maxSheep = tag.getInteger("sheep");
		if (!player.world.isRemote) {
			tileEntity.maxCowCount = maxCows;
			tileEntity.maxPigCount = maxPigs;
			tileEntity.maxChickenCount = maxChickens;
			tileEntity.maxSheepCount = maxSheep;
			tileEntity.markDirty();//mark dirty so it get saved to nbt
		}
		refreshGui();
	}

	public void sendSettingsToServer() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("cows", maxCows);
		tag.setInteger("pigs", maxPigs);
		tag.setInteger("chickens", maxChickens);
		tag.setInteger("sheep", maxSheep);
		sendDataToServer(tag);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		boolean send = false;
		if (maxPigs != tileEntity.maxPigCount) {
			maxPigs = tileEntity.maxPigCount;
			send = true;
		}
		if (maxChickens != tileEntity.maxChickenCount) {
			maxChickens = tileEntity.maxChickenCount;
			send = true;
		}
		if (maxSheep != tileEntity.maxSheepCount) {
			maxSheep = tileEntity.maxSheepCount;
			send = true;
		}
		if (maxCows != tileEntity.maxCowCount) {
			maxCows = tileEntity.maxCowCount;
			send = true;
		}

		if (send) {
			sendInitData();
		}
	}

}
