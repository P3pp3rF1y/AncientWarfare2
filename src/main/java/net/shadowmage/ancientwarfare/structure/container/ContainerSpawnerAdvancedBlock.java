package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

public class ContainerSpawnerAdvancedBlock extends ContainerSpawnerAdvancedBase {

    TileAdvancedSpawner spawner;

    public ContainerSpawnerAdvancedBlock(EntityPlayer player, int x, int y, int z) {
        super(player);
        TileEntity te = player.worldObj.getTileEntity(x, y, z);
        if (te instanceof TileAdvancedSpawner) {
            spawner = (TileAdvancedSpawner) te;
            settings = spawner.getSettings();
        } else {
            throw new IllegalArgumentException("Spawner not found");
        }
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
                spawner.getSettings().readFromNBT(tag.getCompoundTag("spawnerSettings"));
                spawner.markDirty();
                player.worldObj.markBlockForUpdate(spawner.xCoord, spawner.yCoord, spawner.zCoord);
            }
        }
    }
}
