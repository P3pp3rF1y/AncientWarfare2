package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

public class ContainerSpawnerAdvancedInventoryBlock extends ContainerSpawnerAdvancedInventoryBase {

    private TileAdvancedSpawner spawner;

    public ContainerSpawnerAdvancedInventoryBlock(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);

        TileEntity te = player.worldObj.getTileEntity(x, y, z);
        if (!player.worldObj.isRemote && te instanceof TileAdvancedSpawner) {
            spawner = (TileAdvancedSpawner) te;
            settings = spawner.getSettings();
        } else {
            settings = SpawnerSettings.getDefaultSettings();
        }
        inventory = settings.getInventory();
        this.addSettingsInventorySlots();
        this.addPlayerSlots(8, 70, 8);
    }

    @Override
    public boolean canInteractWith(EntityPlayer var1){
        return spawner.getDistanceFrom(var1.posX, var1.posY, var1.posZ) <= 64D;
    }

    @Override
    public void sendInitData() {
        if (!player.worldObj.isRemote) {
            sendSettingsToClient();
        }
    }

    private void sendSettingsToClient() {
        NBTTagCompound tag = new NBTTagCompound();
        settings.writeToNBT(tag);

        PacketGui pkt = new PacketGui();
        pkt.setTag("spawnerSettings", tag);
        NetworkHandler.sendToPlayer((EntityPlayerMP) player, pkt);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("spawnerSettings")) {
            if (player.worldObj.isRemote) {
                settings.readFromNBT(tag.getCompoundTag("spawnerSettings"));
                this.refreshGui();
            } else {
                spawner.readFromNBT(tag);
                player.worldObj.markBlockForUpdate(spawner.xCoord, spawner.yCoord, spawner.zCoord);
            }
        }
    }

}
