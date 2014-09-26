package net.shadowmage.ancientwarfare.automation.tile.worksite;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class TileOreProcessor extends TileEntity implements IWorkSite, IInventory, ISidedInventory, ITorqueTile, IOwnable, IInteractableTile, IRotatableTile
{

private String owner = "";
private double maxEnergyStored = AWCoreStatics.energyPerWorkUnit*3;
private double maxInput = maxEnergyStored;
private double storedEnergy;
private double efficiencyBonusFactor = 0.f;
private ForgeDirection orientation = ForgeDirection.NORTH;
private InventoryBasic inventory;

public TileOreProcessor()
  {
  inventory = new InventoryBasic(2);//TODO override methods for oncallback for when items changed
  }

@Override
public void onBlockBroken()
  {
  // TODO Auto-generated method stub
  
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public void setPrimaryFacing(ForgeDirection face){orientation = face==null?ForgeDirection.NORTH : face;}

@Override
public void setOwnerName(String name){owner = name==null ? "" : name;}

@Override
public String getOwnerName(){return owner;}

@Override
public ForgeDirection getPrimaryFacing(){return orientation;}

@Override
public void setEnergy(double energy){storedEnergy = energy>maxEnergyStored? maxEnergyStored : energy;}

@Override
public double getMaxEnergy(){return maxEnergyStored;}

@Override
public double getEnergyStored(){return storedEnergy;}

@Override
public double addEnergy(ForgeDirection from, double energy)
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public double getEnergyDrainFactor(){return 0;}

@Override
public double getMaxOutput(){return 0;}

@Override
public double getMaxInput(){return AWAutomationStatics.low_transfer_max;}//TODO set from where?

@Override
public double getEnergyOutput(){return 0;}

@Override
public boolean canOutput(ForgeDirection towards){return false;}

@Override
public boolean canInput(ForgeDirection from){return true;}

@Override
public boolean cascadedInput(){return false;}

@Override
public TileEntity[] getNeighbors(){return null;}

@Override
public ITorqueTile[] getNeighborTorqueTiles(){return null;}

@Override
public double getClientOutputRotation(){return 0;}

@Override
public double getPrevClientOutputRotation(){return 0;}

@Override
public boolean useClientRotation(){return false;}

@Override
public int[] getAccessibleSlotsFromSide(int p_94128_1_)
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_,
    int p_102007_3_)
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_,
    int p_102008_3_)
  {
  // TODO Auto-generated method stub
  return false;
  }

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
public void openInventory(){}

@Override
public void closeInventory(){}

@Override
public boolean isItemValidForSlot(int slot, ItemStack stack){return true;}//TODO set from recipe list

@Override
public boolean hasWork()
  {
  return storedEnergy < maxEnergyStored;
  }

@Override
public void addEnergyFromWorker(IWorker worker)
  {
  // TODO Auto-generated method stub  
  }

@Override
public void addEnergyFromPlayer(EntityPlayer player)
  {
  // TODO Auto-generated method stub  
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.CRAFTING;
  }

@Override
public Team getTeam(){return worldObj.getScoreboard().getPlayersTeam(owner);}

@Override
public BlockPosition getWorkBoundsMin(){return null;}

@Override
public BlockPosition getWorkBoundsMax(){return null;}

@Override
public boolean userAdjustableBlocks(){return false;}

@Override
public boolean hasWorkBounds(){return false;}

@Override
public int getBoundsMaxWidth(){return 0;}

@Override
public int getBoundsMaxHeight(){return 0;}

@Override
public void setBounds(BlockPosition p1, BlockPosition p2){}//NOOP

@Override
public void setWorkBoundsMax(BlockPosition max){}//NOOP

@Override
public void setWorkBoundsMin(BlockPosition min){}//NOOP

@Override
public void onBoundsAdjusted(){}//NOOP

@Override
public EnumSet<WorksiteUpgrade> getUpgrades(){return EnumSet.noneOf(WorksiteUpgrade.class);}

@Override
public EnumSet<WorksiteUpgrade> getValidUpgrades(){return EnumSet.noneOf(WorksiteUpgrade.class);}

@Override
public void addUpgrade(WorksiteUpgrade upgrade){}

@Override
public void removeUpgrade(WorksiteUpgrade upgrade){}

}
