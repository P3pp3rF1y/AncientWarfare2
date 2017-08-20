package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class TileAdvancedSpawner extends TileEntity {

    private SpawnerSettings settings = new SpawnerSettings();

    public TileAdvancedSpawner() {

    }

    @Override
    public void setWorldObj(World world) {
        super.setWorldObj(world);
        settings.setWorld(world, xCoord, yCoord, zCoord);
    }

    @Override
    public void updateEntity() {
        if (!hasWorld() || world.isRemote) {
            return;
        }
        if (settings.world == null) {
            settings.setWorld(world, xCoord, yCoord, zCoord);
        }
        settings.onUpdate();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagCompound ntag = new NBTTagCompound();
        settings.writeToNBT(ntag);
        tag.setTag("spawnerSettings", ntag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        settings.readFromNBT(tag.getCompoundTag("spawnerSettings"));
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        settings.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        settings.readFromNBT(pkt.func_148857_g());
        world.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
        world.notifyBlockUpdate(xCoord, yCoord, zCoord);
    }

    public SpawnerSettings getSettings() {
        return settings;
    }

    public void setSettings(SpawnerSettings settings) {
        this.settings = settings;
        this.world.notifyBlockUpdate(xCoord, yCoord, zCoord);
    }

    public float getBlockHardness() {
        return settings.blockHardness;
    }

    public void onBlockBroken() {
        if (world.isRemote) {
            return;
        }
        int xp = settings.getXpToDrop();
        while (xp > 0) {
            int j = EntityXPOrb.getXPSplit(xp);
            xp -= j;
            this.world.spawnEntityInWorld(new EntityXPOrb(this.world, this.xCoord + 0.5d, this.yCoord, this.zCoord + 0.5d, j));
        }
        InventoryBasic inv = settings.getInventory();
        ItemStack item;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            item = inv.getStackInSlot(i);
            InventoryTools.dropItemInWorld(world, item, xCoord, yCoord, zCoord);
        }
    }

    public void handleClientEvent(int a, int b) {

    }

}
