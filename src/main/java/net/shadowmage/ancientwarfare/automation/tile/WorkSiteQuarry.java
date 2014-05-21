package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class WorkSiteQuarry extends TileWorksiteBounded
{

boolean finished;
int currentX, currentY, currentZ;//position within bounds that is the 'active' position
BlockPosition nextPosition = new BlockPosition();

public WorkSiteQuarry()
  {
  this.inventory = new InventorySided(this, RotationType.FOUR_WAY, 27);
  int[] topIndices = InventoryTools.getIndiceArrayForSpread(0, 27);
  this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
  }

private boolean hasDoneInit = false;

@Override
protected boolean processWork()
  {
  if(!hasDoneInit)
    {
    initWorkSite();
    hasDoneInit = true;
    }
  if(finished){return false;}
  if(validatePosition()!=currentY)
    {
    scanNextPosition();
    }  
  BlockPosition target = nextPosition;
  Block block = worldObj.getBlock(target.x, target.y, target.z);  
  ArrayList<ItemStack> drops = block.getDrops(worldObj, target.x, target.y, target.z, worldObj.getBlockMetadata(target.x, target.y, target.z), 0);
  for(ItemStack stack : drops)
    {
    addStackToInventory(stack, RelativeSide.TOP);    
    }  
  worldObj.setBlockToAir(target.x, target.y, target.z); 
  scanNextPosition();
  return true;
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
      if(currentY<=0)
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

public void initWorkSite()
  {
  this.getWorkBoundsMin().y = 1;
  this.currentY = this.getWorkBoundsMax().y;
  this.currentX = this.getWorkBoundsMin().x;
  this.currentZ = this.getWorkBoundsMin().z;
  this.nextPosition.reassign(currentX, currentY, currentZ);
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.MINING;
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
  hasDoneInit = tag.getBoolean("init");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setInteger("currentY", currentY);
  tag.setInteger("currentX", currentX);
  tag.setInteger("currentZ", currentZ);
  tag.setBoolean("finished", finished);
  tag.setBoolean("init", hasDoneInit);
  NBTTagCompound posTag = new NBTTagCompound();
  nextPosition.writeToNBT(posTag);
  tag.setTag("nextPos", posTag);
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_QUARRY, xCoord, yCoord, zCoord);
    return true;
    }
  return false;
  }

@Override
protected boolean hasWorksiteWork()
  {
  return !finished;
  }

}
