package net.shadowmage.ancientwarfare.automation.tile.worksite;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class TileOreProcessor extends TileEntity implements IWorkSite, IInventory, ISidedInventory, ITorqueTile, IOwnable, IInteractableTile, IRotatableTile
{

private String owner = "";
private double maxEnergyStored = AWCoreStatics.energyPerWorkUnit*3;
private double maxInput = maxEnergyStored;
private double storedEnergy;
private ForgeDirection orientation = ForgeDirection.NORTH;
private InventoryBasic inventory;

public TileOreProcessor()
  {
  inventory = new InventoryBasic(2)
    {
    @Override
    public void setInventorySlotContents(int var1, ItemStack var2)
      {
      if(worldObj!=null && !worldObj.isRemote)
        {
        onInventoryUpdated();
        }
      super.setInventorySlotContents(var1, var2);
      }
    };//TODO override methods for callback for when items changed
  }

public void onInventoryUpdated()
  {
  
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  // TODO implement GUI
  return true;
  }

@Override
public int[] getAccessibleSlotsFromSide(int side)
  {
  // TODO implement re-mappable relative block sides
  return null;
  }

@Override
public boolean isItemValidForSlot(int slot, ItemStack stack)
  {
  //TODO set from recipe list
  return true;
  }

@Override
public boolean canInsertItem(int slot, ItemStack stack, int side){return slot==0 && isItemValidForSlot(slot, stack);}

@Override
public boolean canExtractItem(int slot, ItemStack stack, int side){return true;}

@Override
public void onBlockBroken()
  {
  if(!worldObj.isRemote){InventoryTools.dropInventoryInWorld(worldObj, inventory, xCoord, yCoord, zCoord);}
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  inventory.readFromNBT(tag.getCompoundTag("inventory"));
  orientation = ForgeDirection.getOrientation(tag.getInteger("orientation"));
  storedEnergy = tag.getDouble("storedEnergy");
  owner = tag.getString("owner");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setTag("inventory", inventory.writeToNBT(new NBTTagCompound()));
  tag.setInteger("orientation", orientation.ordinal());
  tag.setDouble("storedEnergy", storedEnergy);
  tag.setString("owner", owner);
  }

@Override
public final Packet getDescriptionPacket()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setInteger("orientation", orientation.ordinal());  
  return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {
  super.onDataPacket(net, pkt);
  NBTTagCompound tag = pkt.func_148857_g();
  orientation = ForgeDirection.getOrientation(tag.getInteger("orientation"));
  }

/************************************** BRIDGE/TEMPLATE/ACCESSOR METHODS ****************************************/

@Override
public WorkType getWorkType(){return WorkType.CRAFTING;}

@Override
public void setPrimaryFacing(ForgeDirection face){orientation = face==null?ForgeDirection.NORTH : face;}

@Override
public void setOwnerName(String name){owner = name==null ? "" : name;}

@Override
public String getOwnerName(){return owner;}

@Override
public ForgeDirection getPrimaryFacing(){return orientation;}

@Override
public double getMaxTorque(ForgeDirection from){return maxEnergyStored;}

@Override
public double getTorqueStored(ForgeDirection from){return storedEnergy;}

@Override
public double getMaxTorqueOutput(ForgeDirection from){return 0;}//NOOP

@Override
public double getMaxTorqueInput(ForgeDirection from){return maxInput;}

@Override
public boolean canOutputTorque(ForgeDirection towards){return false;}

@Override
public boolean canInputTorque(ForgeDirection from){return true;}

@Override
public double getClientOutputRotation(ForgeDirection from){return 0;}//NOOP

@Override
public double getPrevClientOutputRotation(ForgeDirection from){return 0;}//NOOP

@Override
public boolean useOutputRotation(ForgeDirection from){return false;}//NOOP

@Override
public int getSizeInventory(){return inventory.getSizeInventory();}

@Override
public ItemStack getStackInSlot(int slot){return inventory.getStackInSlot(slot);}

@Override
public ItemStack decrStackSize(int slot, int amt){return inventory.decrStackSize(slot, amt);}

@Override
public ItemStack getStackInSlotOnClosing(int slot){return inventory.getStackInSlotOnClosing(slot);}

@Override
public void setInventorySlotContents(int slot, ItemStack stack){inventory.setInventorySlotContents(slot, stack);}

@Override
public String getInventoryName(){return inventory.getInventoryName();}

@Override
public boolean hasCustomInventoryName(){return inventory.hasCustomInventoryName();}

@Override
public int getInventoryStackLimit(){return inventory.getInventoryStackLimit();}

@Override
public boolean isUseableByPlayer(EntityPlayer player){return inventory.isUseableByPlayer(player);}

@Override
public void openInventory(){}//NOOP

@Override
public void closeInventory(){}//NOOP

@Override
public boolean hasWork(){return storedEnergy < maxEnergyStored;}

@Override
public void addEnergyFromWorker(IWorker worker){IWorkSite.WorksiteImplementation.addEnergyFromWorker(this, worker);}

@Override
public void addEnergyFromPlayer(EntityPlayer player){IWorkSite.WorksiteImplementation.addEnergyFromPlayer(this, player);}

@Override
public Team getTeam(){return worldObj.getScoreboard().getPlayersTeam(owner);}

@Override
public BlockPosition getWorkBoundsMin(){return null;}//NOOP

@Override
public BlockPosition getWorkBoundsMax(){return null;}//NOOP

@Override
public boolean userAdjustableBlocks(){return false;}//NOOP

@Override
public boolean hasWorkBounds(){return false;}//NOOP

@Override
public int getBoundsMaxWidth(){return 0;}//NOOP

@Override
public int getBoundsMaxHeight(){return 0;}//NOOP

@Override
public void setBounds(BlockPosition p1, BlockPosition p2){}//NOOP

@Override
public void setWorkBoundsMax(BlockPosition max){}//NOOP

@Override
public void setWorkBoundsMin(BlockPosition min){}//NOOP

@Override
public void onBoundsAdjusted(){}//NOOP

@Override
public EnumSet<WorksiteUpgrade> getUpgrades(){return EnumSet.noneOf(WorksiteUpgrade.class);}//NOOP

@Override
public EnumSet<WorksiteUpgrade> getValidUpgrades(){return EnumSet.noneOf(WorksiteUpgrade.class);}//NOOP

@Override
public void addUpgrade(WorksiteUpgrade upgrade){}//NOOP

@Override
public void removeUpgrade(WorksiteUpgrade upgrade){}//NOOP

}
