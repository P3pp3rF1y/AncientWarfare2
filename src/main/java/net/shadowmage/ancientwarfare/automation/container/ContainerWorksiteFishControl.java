package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteFishFarm;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;

public class ContainerWorksiteFishControl extends ContainerTileBase<WorkSiteFishFarm> {

    public boolean harvestFish;
    public boolean harvestInk;

    public ContainerWorksiteFishControl(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        this.harvestFish = tileEntity.harvestFish();
        this.harvestInk = tileEntity.harvestInk();
    }

    @Override
    public void sendInitData() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("fish", harvestFish);
        tag.setBoolean("ink", harvestInk);
        this.sendDataToClient(tag);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("fish") && tag.hasKey("ink")) {
            harvestFish = tag.getBoolean("fish");
            harvestInk = tag.getBoolean("ink");
            tileEntity.setHarvest(harvestFish, harvestInk);
        }
        refreshGui();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (harvestFish != tileEntity.harvestFish() || harvestInk != tileEntity.harvestInk()) {
            sendInitData();
        }
    }

    public void sendSettingsToServer() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("fish", harvestFish);
        tag.setBoolean("ink", harvestInk);
        this.sendDataToServer(tag);
    }

}
