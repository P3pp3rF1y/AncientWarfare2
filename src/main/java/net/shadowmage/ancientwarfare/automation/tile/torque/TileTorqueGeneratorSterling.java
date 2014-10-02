package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class TileTorqueGeneratorSterling extends TileTorqueGeneratorBase implements IInventory
{

InventoryBasic fuelInventory = new InventoryBasic(1)
  {
  public boolean isItemValidForSlot(int var1, net.minecraft.item.ItemStack var2) 
    {
    return TileEntityFurnace.getItemBurnTime(var2)>0;
    }
  }; 
  
TorqueCell torqueCell;
  
int burnTime = 0;
int burnTimeBase = 0;

/**
 * client side this == 0.0 -> 1.0
 */
double clientEnergyState;

/**
 * server side this == 0 -> 100 (integer percent)
 * client side this == 0.0 -> 1.0 (actual percent)
 */
double clientDestEnergyState;

/**
 * used client side for rendering
 */
double rotation, prevRotation;

public TileTorqueGeneratorSterling()
  {
  torqueCell = new TorqueCell(0, 4, 1600, 1.f);
  }

@Override
public void updateEntity()
  {  
  super.updateEntity();
  if(!worldObj.isRemote)
    {
    if(burnTime <= 0 && torqueCell.getEnergy() < torqueCell.getMaxEnergy())
      {
      if(fuelInventory.getStackInSlot(0)!=null)
        {
        //if fueled, consume one, set burn-ticks to fuel value
        int ticks = TileEntityFurnace.getItemBurnTime(fuelInventory.getStackInSlot(0));
        if(ticks>0)
          {
          fuelInventory.decrStackSize(0, 1);
          burnTime = ticks;
          burnTimeBase = ticks;
          }
        }
      }
    else if(burnTime>0)
      {
      torqueCell.setEnergy(torqueCell.getEnergy() + 1.d * AWAutomationStatics.sterling_generator_output_factor);
      burnTime--;
      }  
    serverNetworkUpdate();    
    torqueIn = torqueCell.getEnergy() - prevEnergy;
    torqueOut = transferPowerTo(getPrimaryFacing());
    prevEnergy = torqueCell.getEnergy();
    }
  else
    {
    clientNetworkUpdate();
    updateRotation();
    }
  }

@Override
protected void serverNetworkSynch()
  {
  int percent = (int)(torqueCell.getPercentFull()*100.d);
  int percent2 = (int)((torqueOut / torqueCell.getMaxOutput())*100.d);
  percent = Math.max(percent, percent2);  
  if(percent != clientDestEnergyState)
    {
    clientDestEnergyState = percent;
    sendSideRotation(getPrimaryFacing(), percent);    
    }
  }

@Override
protected void updateRotation()
  {
  prevRotation = rotation;
  if(clientEnergyState > 0)
    {
    double r = AWAutomationStatics.low_rpt * clientEnergyState;
    rotation += r;
    }
  }

@Override
protected void clientNetworkUpdate()
  {
  if(clientEnergyState != clientDestEnergyState)
    {
    if(networkUpdateTicks>=0)
      {
      clientEnergyState += (clientDestEnergyState - clientEnergyState) / ((double)networkUpdateTicks+1.d);
      networkUpdateTicks--;
      }
    else
      {
      clientEnergyState = clientDestEnergyState;
      }
    }
  }

@Override
protected void handleClientRotationData(ForgeDirection side, int value)
  {
  clientDestEnergyState = ((double)value) * 0.01d;
  }

@Override
public boolean canOutputTorque(ForgeDirection towards)
  {
  return towards==orientation;
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_TORQUE_GENERATOR_STERLING, xCoord, yCoord, zCoord);
    }
  return true;
  }

public int getBurnTime()
  {
  return burnTime;
  }

public int getBurnTimeBase()
  {
  return burnTimeBase;
  }

@Override
public int getSizeInventory()
  {
  return fuelInventory.getSizeInventory();
  }

@Override
public ItemStack getStackInSlot(int var1)
  {
  return fuelInventory.getStackInSlot(var1);
  }

@Override
public ItemStack decrStackSize(int var1, int var2)
  {
  return fuelInventory.decrStackSize(var1, var2);
  }

@Override
public ItemStack getStackInSlotOnClosing(int var1)
  {
  return fuelInventory.getStackInSlotOnClosing(var1);
  }

@Override
public void setInventorySlotContents(int var1, ItemStack var2)
  {
  fuelInventory.setInventorySlotContents(var1, var2);
  }

@Override
public String getInventoryName()
  {
  return fuelInventory.getInventoryName();
  }

@Override
public boolean hasCustomInventoryName()
  {
  return fuelInventory.hasCustomInventoryName();
  }

@Override
public int getInventoryStackLimit()
  {
  return fuelInventory.getInventoryStackLimit();
  }

@Override
public boolean isUseableByPlayer(EntityPlayer var1)
  {
  return fuelInventory.isUseableByPlayer(var1);
  }

@Override
public void openInventory()
  {
  fuelInventory.openInventory();
  }

@Override
public void closeInventory()
  {
  fuelInventory.closeInventory();
  }

@Override
public boolean isItemValidForSlot(int var1, ItemStack var2)
  {
  return fuelInventory.isItemValidForSlot(var1, var2);
  }

@Override
public NBTTagCompound getDescriptionTag()
  {
  NBTTagCompound tag = super.getDescriptionTag();
  tag.setInteger("clientEnergy", (int)clientDestEnergyState);
  return tag;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  super.onDataPacket(net, pkt);
  NBTTagCompound tag = pkt.func_148857_g();
  clientDestEnergyState = ((double)tag.getInteger("clientEnergy")) / 100.d;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  burnTime = tag.getInteger("burnTicks");
  burnTimeBase = tag.getInteger("burnTicksBase");
  if(tag.hasKey("inventory"))
    {
    fuelInventory.readFromNBT(tag.getCompoundTag("inventory"));
    }
  torqueCell.setEnergy(tag.getDouble("torqueEnergy"));
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  tag.setInteger("burnTicks", burnTime);
  tag.setInteger("burnTicksBase", burnTimeBase);
  tag.setTag("inventory", fuelInventory.writeToNBT(new NBTTagCompound()));
  tag.setDouble("torqueEnergy", torqueCell.getEnergy());
  }

@Override
public double getMaxTorque(ForgeDirection from)
  {
  return torqueCell.getMaxEnergy();
  }

@Override
public double getTorqueStored(ForgeDirection from)
  {
  return torqueCell.getEnergy();
  }

@Override
public double addTorque(ForgeDirection from, double energy)
  {
  return torqueCell.addEnergy(energy);
  }

@Override
public double drainTorque(ForgeDirection from, double energy)
  {
  return torqueCell.drainEnergy(energy);
  }

@Override
public double getMaxTorqueOutput(ForgeDirection from)
  {
  return torqueCell.getMaxTickOutput();
  }

@Override
public double getMaxTorqueInput(ForgeDirection from)
  {
  return torqueCell.getMaxTickInput();
  }

@Override
public boolean useOutputRotation(ForgeDirection from)
  {
  return true;
  }

@Override
public float getClientOutputRotation(ForgeDirection from, float delta)
  {
  return getRotation(rotation, prevRotation, delta);
  }

@Override
protected double getTotalTorque()
  {
  return torqueCell.getEnergy();
  }

}
