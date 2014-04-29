package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class ContainerMailbox extends ContainerBase
{

public ContainerMailbox(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  }

@Override
public void sendInitData()
  {
  super.sendInitData();
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  super.handlePacketData(tag);
  }

@Override
public void detectAndSendChanges()
  {
  super.detectAndSendChanges();
  }

}
