package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteFishFarm;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class ContainerWorksiteFishControl extends ContainerBase
{


public boolean harvestFish;
public boolean harvestInk;
public WorkSiteFishFarm worksite;
public ContainerWorksiteFishControl(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  worksite = (WorkSiteFishFarm) player.worldObj.getTileEntity(x, y, z);
  this.harvestFish = worksite.harvestFish;
  this.harvestInk = worksite.harvestInk;
  }

@Override
public void sendInitData()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setBoolean("fish", harvestFish);
  tag.setBoolean("ink", harvestInk);
  this.sendDataToClient(tag);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {  
  if(tag.hasKey("fish"))
    {
    harvestFish = tag.getBoolean("fish");
    worksite.harvestFish = harvestFish;
    }
  if(tag.hasKey("ink"))
    {
    harvestInk = tag.getBoolean("ink");
    worksite.harvestInk = harvestInk;
    }
  refreshGui();
  }

@Override
public void detectAndSendChanges()
  {
  super.detectAndSendChanges();
  if(harvestFish!= worksite.harvestFish || harvestInk!=worksite.harvestInk)
    {
    sendInitData();
    }  
  }

public void sendSettingsToServer()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setBoolean("fish", harvestFish);
  tag.setBoolean("ink", harvestInk);
  this.sendDataToServer(tag);
  }

}
