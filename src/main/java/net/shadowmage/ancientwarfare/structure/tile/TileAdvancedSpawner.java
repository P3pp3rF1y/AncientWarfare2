package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
  getSettings().setWorld(world, xCoord, yCoord, zCoord);
  }

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}
  if(settings.worldObj==null)
    {
    settings.setWorld(worldObj, xCoord, yCoord, zCoord);
    }
  getSettings().onUpdate();
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  getSettings().writeToNBT(tag);
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  getSettings().readFromNBT(tag);
  }

public SpawnerSettings getSettings()
  {
  return settings;
  }

public void setSettings(SpawnerSettings settings)
  {
  this.settings = settings;
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
  }

}
