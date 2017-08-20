package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

public class ContainerSpawnerAdvancedBlock extends ContainerSpawnerAdvancedBase {

    private final TileAdvancedSpawner spawner;

    public ContainerSpawnerAdvancedBlock(EntityPlayer player, int x, int y, int z) {
        super(player);
        TileEntity te = player.world.getTileEntity(x, y, z);
        if (te instanceof TileAdvancedSpawner) {
            spawner = (TileAdvancedSpawner) te;
            settings = spawner.getSettings();
        } else {
            throw new IllegalArgumentException("Spawner not found");
        }
    }

    @Override
    public void sendInitData() {
        if (!spawner.getWorld().isRemote) {
            NetworkHandler.sendToPlayer((EntityPlayerMP) player, getSettingPacket());
        }
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("spawnerSettings")) {
            if (spawner.getWorld().isRemote) {
                settings.readFromNBT(tag.getCompoundTag("spawnerSettings"));
                this.refreshGui();
            } else {
                spawner.getSettings().readFromNBT(tag.getCompoundTag("spawnerSettings"));
                spawner.markDirty();
                spawner.getWorld().notifyBlockUpdate(spawner.xCoord, spawner.yCoord, spawner.zCoord);
            }
        }
    }
}
