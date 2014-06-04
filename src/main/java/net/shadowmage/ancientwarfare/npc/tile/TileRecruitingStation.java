package net.shadowmage.ancientwarfare.npc.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;

public class TileRecruitingStation extends TileEntity implements IOwnable, IInteractableTile
{

String ownerName = "";

public TileRecruitingStation()
  {
  
  }

@Override
public void setOwnerName(String name)
  {
  if(name==null){name="";}
  ownerName=name;
  }

@Override
public String getOwnerName()
  {
  return ownerName;
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  // TODO Auto-generated method stub
  return false;
  }

}
