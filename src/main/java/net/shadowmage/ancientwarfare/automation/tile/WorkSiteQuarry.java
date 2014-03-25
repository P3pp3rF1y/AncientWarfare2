package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.interfaces.IWorker;
import net.shadowmage.ancientwarfare.automation.inventory.InventorySided;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class WorkSiteQuarry extends TileWorksiteBase implements IInventory, ISidedInventory
{

boolean doneInitialScan = false;
boolean finished;
int currentY = 0;
LinkedList<BlockPosition> blocksToRemove = new LinkedList<BlockPosition>();

public WorkSiteQuarry()
  {
  this.maxWorkers = 4;
  canUpdate = true;
  this.inventory = new InventorySided(27 + 3 + 3);
  for(int i =0; i <27; i++)
    {
    this.inventory.addSidedMapping(0, i, true, true);
    }
  for(int i = 27; i<30; i++)
    {
    this.inventory.addSidedMapping(1, i, true, true);
    }
  for(int i = 30; i < 33; i++)
    {
    this.inventory.addSidedMapping(2, i, true, true);
    }
  }

@Override
public void doPlayerWork(EntityPlayer player)
  {
  
  }

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}
  if(!doneInitialScan)
    {
    setWorkBoundsMin(new BlockPosition(xCoord-10, yCoord-1, zCoord-10));
    setWorkBoundsMax(new BlockPosition(xCoord+10, yCoord-1, zCoord+10));
    doneInitialScan = true;
    if(currentY == 0)
      {
      currentY = yCoord-1;      
      }
    scanNextLevel();    
    }
  if(!finished && blocksToRemove.isEmpty())
    {
    scanNextLevel();
    }
  }

@Override
public boolean hasWork()
  {
  return !finished && !blocksToRemove.isEmpty();
  }

@Override
public void doWork(IWorker worker)
  {  
  BlockPosition target = blocksToRemove.pop();
  if(target!=null)
    {
    Block block = worldObj.getBlock(target.x, target.y, target.z);
    
    ArrayList<ItemStack> drops = block.getDrops(worldObj, target.x, target.y, target.z, worldObj.getBlockMetadata(target.x, target.y, target.z), 0);
    
    /**
     * TODO gather block drops
     */
    worldObj.setBlockToAir(target.x, target.y, target.z);
    }  
  if(blocksToRemove.isEmpty())
    {
    scanNextLevel();
    }
  }

private void scanNextLevel()
  {
  AWLog.logDebug("scanning quarry level. nextY: "+(currentY-1));
  if(currentY<=1)
    {
    finished = true;
    return;
    }
  currentY--;
  Block block;
  for(int x = getWorkBoundsMin().x; x<=getWorkBoundsMax().x; x++)
    {
    for(int z = getWorkBoundsMin().z; z<=getWorkBoundsMax().z; z++)
      {
      if(!worldObj.isAirBlock(x, currentY, z))
        {
        block = worldObj.getBlock(x, currentY+1, z);
        if(block==Blocks.lava || block==Blocks.flowing_lava)
          {
          continue;
          }        
        if(block==null || block.getBlockHardness(worldObj, x, currentY, z)<0)//skip unbreakable blocks
          {         
          continue;
          }        
        blocksToRemove.add(new BlockPosition(x, currentY, z));
        }
      }
    }  
  AWLog.logDebug("scanned quarry level. : "+(currentY) + " found :" +blocksToRemove.size() + " blocks to remove");
  if(blocksToRemove.isEmpty())
    {
    scanNextLevel();
    }
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.MINING;
  }

@Override
public void writeClientData(NBTTagCompound tag)
  {
  
  }

@Override
public void readClientData(NBTTagCompound tag)
  {
  
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  currentY = tag.getInteger("currentY")+1;
  finished = tag.getBoolean("finished");
  doneInitialScan = false;
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setInteger("currentY", currentY);
  tag.setBoolean("finished", finished);
  }


}
