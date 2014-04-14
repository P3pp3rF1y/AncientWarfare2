package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;

public class ContainerGateControl extends ContainerBase
{

EntityGate gate;

public ContainerGateControl(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  gate = (EntityGate) player.worldObj.getEntityByID(x);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("repack"))
    {
    gate.repackEntity();
    }  
  } 

public void repackGate()
  {
  
  }

}
