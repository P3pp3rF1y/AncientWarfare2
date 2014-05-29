package net.shadowmage.ancientwarfare.npc.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;

public class TileTownHall extends TileEntity implements IOwnable
{

public TileTownHall()
  {
  // TODO Auto-generated constructor stub
  }

@Override
  public boolean canUpdate()
    {
    // TODO Auto-generated method stub
    return super.canUpdate();
    }

@Override
  public void updateEntity()
    {
    // TODO Auto-generated method stub
    super.updateEntity();
    }

@Override
public void setOwnerName(String name)
  {
  // TODO Auto-generated method stub

  }

@Override
public String getOwnerName()
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
  public void readFromNBT(NBTTagCompound p_145839_1_)
    {
    // TODO Auto-generated method stub
    super.readFromNBT(p_145839_1_);
    }

@Override
  public void writeToNBT(NBTTagCompound p_145841_1_)
    {
    // TODO Auto-generated method stub
    super.writeToNBT(p_145841_1_);
    }


}
