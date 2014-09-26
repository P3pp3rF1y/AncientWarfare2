package net.shadowmage.ancientwarfare.automation.tile.worksite;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class TileOreProcessor extends TileEntity implements IWorkSite, IInventory, ISidedInventory, ITorqueTile, IOwnable, IInteractableTile, IRotatableTile
{

public TileOreProcessor()
  {
  
  }

@Override
public void setPrimaryFacing(ForgeDirection face)
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
public ForgeDirection getPrimaryFacing()
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public void setEnergy(double energy)
  {
  // TODO Auto-generated method stub
  
  }

@Override
public double getMaxEnergy()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public double getEnergyStored()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public double addEnergy(ForgeDirection from, double energy)
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public double getEnergyDrainFactor()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public double getMaxOutput()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public double getMaxInput()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public double getEnergyOutput()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public boolean canOutput(ForgeDirection towards)
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public boolean canInput(ForgeDirection from)
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public boolean cascadedInput()
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public TileEntity[] getNeighbors()
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public ITorqueTile[] getNeighborTorqueTiles()
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public double getClientOutputRotation()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public double getPrevClientOutputRotation()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public boolean useClientRotation()
  {
  // TODO Auto-generated method stub
  return false;
  }

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
public int getSizeInventory()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public ItemStack getStackInSlot(int p_70301_1_)
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_)
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public ItemStack getStackInSlotOnClosing(int p_70304_1_)
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_)
  {
  // TODO Auto-generated method stub
  
  }

@Override
public String getInventoryName()
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public boolean hasCustomInventoryName()
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public int getInventoryStackLimit()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public void openInventory()
  {
  // TODO Auto-generated method stub
  
  }

@Override
public void closeInventory()
  {
  // TODO Auto-generated method stub
  
  }

@Override
public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public boolean hasWork()
  {
  // TODO Auto-generated method stub
  return false;
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
  // TODO Auto-generated method stub
  return null;
  }

@Override
public Team getTeam()
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public BlockPosition getWorkBoundsMin()
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public BlockPosition getWorkBoundsMax()
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public boolean userAdjustableBlocks()
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public boolean hasWorkBounds()
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public int getBoundsMaxWidth()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public int getBoundsMaxHeight()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public void setBounds(BlockPosition p1, BlockPosition p2)
  {
  // TODO Auto-generated method stub
  
  }

@Override
public void setWorkBoundsMax(BlockPosition max)
  {
  // TODO Auto-generated method stub
  
  }

@Override
public void setWorkBoundsMin(BlockPosition min)
  {
  // TODO Auto-generated method stub
  
  }

@Override
public void onBoundsAdjusted()
  {
  // TODO Auto-generated method stub
  
  }

@Override
public EnumSet<WorksiteUpgrade> getUpgrades()
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public EnumSet<WorksiteUpgrade> getValidUpgrades()
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public void addUpgrade(WorksiteUpgrade upgrade)
  {
  // TODO Auto-generated method stub
  
  }

@Override
public void removeUpgrade(WorksiteUpgrade upgrade)
  {
  // TODO Auto-generated method stub
  
  }

@Override
public void onBlockBroken()
  {
  // TODO Auto-generated method stub
  
  }

}
