package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerAdvanced;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItemLoader;

public class TileAdvancedSpawner extends TileEntity
{

private SpawnerSettings settings = new SpawnerSettings();

public TileAdvancedSpawner()
  {
  
  }

@Override
public boolean canUpdate()
  {
  return true;
  }

@Override
public void setWorldObj(World world)
  {
  super.setWorldObj(world);
  settings.setWorld(world, xCoord, yCoord, zCoord);
  }

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}
  if(settings.worldObj==null)
    {
    settings.setWorld(worldObj, xCoord, yCoord, zCoord);
    }
  settings.onUpdate();
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  NBTTagCompound ntag = new NBTTagCompound();  
  settings.writeToNBT(ntag);
  tag.setTag("spawnerSettings", ntag);
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  settings.readFromNBT(tag.getCompoundTag("spawnerSettings"));
  }

@Override
public Packet getDescriptionPacket()
  {
  NBTTagCompound tag = new NBTTagCompound();
  settings.writeToNBT(tag);
  S35PacketUpdateTileEntity pkt = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);  
  return pkt;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {
  settings.readFromNBT(pkt.func_148857_g());
  super.onDataPacket(net, pkt);
  worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
  worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  }

public SpawnerSettings getSettings()
  {
  return settings;
  }

public void setSettings(SpawnerSettings settings)
  {
  this.settings = settings;
  this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  }

public float getBlockHardness()
  {
  return settings.blockHardness;
  }

public void onBlockBroken()
  {
  if(worldObj.isRemote){return;}
  int xp = settings.getXpToDrop();
  while (xp > 0)
    {
    int j = EntityXPOrb.getXPSplit(xp);
    xp -= j;
    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.xCoord+0.5d, this.yCoord, this.zCoord+0.5d, j));
    }
  InventoryBasic inv = settings.getInventory();
  ItemStack item;
  for(int i = 0; i < inv.getSizeInventory(); i++)
    {
    item = inv.getStackInSlot(i);
    if(item == null){continue;}
    InventoryTools.dropItemInWorld(worldObj, item, xCoord, yCoord, zCoord);
    }
  }

public void handleClientEvent(int a, int b)
  {
  
  }

}
