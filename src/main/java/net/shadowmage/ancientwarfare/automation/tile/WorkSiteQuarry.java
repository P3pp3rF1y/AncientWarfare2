package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.shadowmage.ancientwarfare.automation.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class WorkSiteQuarry extends TileWorksiteBase
{

boolean finished;
int currentX, currentY, currentZ;//position within bounds that is the 'active' position
BlockPosition nextPosition = new BlockPosition();

public WorkSiteQuarry()
  {
  this.maxWorkers = 4;
  this.canUserSetBlocks = true;
  canUpdate = true;//purely event-driven, no polling
  this.inventory = new InventorySided(27 + 3 + 3, this);
  this.inventory.addSlotViewMap(InventorySide.TOP, 8, 8, "guistrings.inventory.side.top");
  this.inventory.addSlotViewMap(InventorySide.FRONT, 8, (3*18)+12+8, "guistrings.inventory.side.front");
  this.inventory.addSlotViewMap(InventorySide.REAR, 8, (3*18)+18+12+8+12, "guistrings.inventory.side.rear");
  for(int i =0; i <27; i++)
    {
    this.inventory.addSidedMapping(RelativeSide.TOP, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.TOP, i, (i%9)*18, (i/9)*18);
    }
  for(int i = 27, k = 0; i<30; i++, k++)
    {
    this.inventory.addSidedMapping(RelativeSide.LEFT, i, true, true);
    this.inventory.addSidedMapping(RelativeSide.RIGHT, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.FRONT, i, (k%9)*18, (k/9)*18);
    }
  for(int i = 30, k = 0; i < 33; i++, k++)
    {
    this.inventory.addSidedMapping(RelativeSide.REAR, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.REAR, i, (k%9)*18, (k/9)*18);
    }  
 
  }

@Override
public void doPlayerWork(EntityPlayer player)
  {
  
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(worldObj.isRemote){return;}
  }

@Override
public boolean hasWork()
  {
  return !finished;
  }

@Override
public void doWork(IWorker worker)
  {  
  if(validatePosition()!=currentY)
    {
    scanNextPosition();
    if(finished){return;}
    }
  
  BlockPosition target = nextPosition;
  Block block = worldObj.getBlock(target.x, target.y, target.z);  
  ArrayList<ItemStack> drops = block.getDrops(worldObj, target.x, target.y, target.z, worldObj.getBlockMetadata(target.x, target.y, target.z), 0);

  worldObj.setBlockToAir(target.x, target.y, target.z);
  
  
  scanNextPosition();
  }

/**
 * 
 * @return the Y level of the first block to be mined at currentX / currentZ
 * -1 for invalid / below currentY (will force increment to next block)
 */
private int validatePosition()
  {
  for(int y = getWorkBoundsMax().y; y>=currentY; y--)
    {
    if(!worldObj.isAirBlock(currentX, y, currentZ) && canHarvest(worldObj.getBlock(currentX, y, currentZ)))
      {
      return y;
      }
    }
  return -1;
  }

private void incrementPosition()
  {
  if(finished){return;}
  currentX++;
  if(currentX>getWorkBoundsMax().x)
    {
    currentX = getWorkBoundsMin().x;
    currentZ++;
    if(currentZ>getWorkBoundsMax().z)
      {
      currentZ = getWorkBoundsMin().z;
      currentY--;
      if(currentY<0)
        {
        this.finished = true;
        }
      }
    }
  }

private void scanNextPosition()
  {
  if(finished){return;}
  int validY = -1;
  while(validY<=0 && !finished)
    {
    incrementPosition();
    validY = validatePosition();
    }
  if(!finished)
    {
    nextPosition.reassign(currentX, currentY, currentZ);
    }
  }

private boolean canHarvest(Block block)
  {
  return block!=Blocks.flowing_lava && block!=Blocks.lava && block.getBlockHardness(worldObj, currentX, currentY, currentZ)>=0;
  }

@Override
public void initWorkSite()
  {
  this.getWorkBoundsMin().y = 1;
  this.currentY = this.getWorkBoundsMax().y;
  this.currentX = this.getWorkBoundsMin().x;
  this.currentZ = this.getWorkBoundsMin().z;
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
  AWLog.logDebug("reading client data for quarry...");
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);  
  currentY = tag.getInteger("currentY");
  currentX = tag.getInteger("currentX");
  currentZ = tag.getInteger("currentZ");  
  finished = tag.getBoolean("finished");
  nextPosition.read(tag.getCompoundTag("nextPos"));
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setInteger("currentY", currentY);
  tag.setInteger("currentX", currentX);
  tag.setInteger("currentZ", currentZ);
  tag.setBoolean("finished", finished);
  NBTTagCompound posTag = new NBTTagCompound();
  nextPosition.writeToNBT(posTag);
  tag.setTag("nextPos", posTag);
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  AWLog.logDebug("worksite clicked by : "+player + " client: "+player.worldObj.isRemote);
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_INVENTORY, xCoord, yCoord, zCoord);
    return true;
    }
  return false;
  }


}
