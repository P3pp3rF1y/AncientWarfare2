package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedTile;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

/**
 * abstract base class for worksite based tile-entities (or at least a template to copy from)
 * 
 * handles the management of worker references and work-bounds, as well as inventory bridge methods.
 * 
 * All implementing classes must initialize the inventory field in their constructor, or things
 * will go very crashy when the block is placed in the world.
 *  
 * @author Shadowmage
 *
 */
public abstract class TileWorksiteBase extends TileEntity implements IWorkSite, IInventory, ISidedInventory, IInteractableTile, IBoundedTile, IOwnable
{

/**
 * minimum position of the work area bounding box, or a single block position if bbMax is not set
 * must not be null if this block has a work-area
 */
BlockPosition bbMin;

/**
 * maximum position of the work bounding box.  May be null
 */
BlockPosition bbMax;

/**
 * should updateEntity be called for this tile?
 */
protected boolean canUpdate;

protected boolean canUserSetBlocks;

private Set<BlockPosition> userTargetBlocks = new HashSet<BlockPosition>();

protected String owningPlayer = "";

public InventorySided inventory;

private ArrayList<BlockPosition> clientWorkTargets = new ArrayList<BlockPosition>();

private ArrayList<ItemStack> inventoryOverflow = new ArrayList<ItemStack>();

List<BlockPosition> blocksToUpdate = new ArrayList<BlockPosition>();

double maxEnergyStored = AWAutomationStatics.energyPerWorkUnit;
double maxInput = maxEnergyStored;
private double storedEnergy;

public TileWorksiteBase()
  {
  
  }

protected abstract boolean processWork();

protected abstract void fillBlocksToProcess();

protected abstract void scanBlockPosition(BlockPosition pos);

@Override
public void setEnergy(double energy)
  {
  this.storedEnergy = energy;
  }

@Override
public double addEnergy(ForgeDirection from, double energy)
  {
  if(canInput(from))
    {
    if(energy+getEnergyStored()>getMaxEnergy())
      {
      energy = getMaxEnergy()-getEnergyStored();
      }
    if(energy>getMaxInput())
      {
      energy = getMaxInput();
      }
    storedEnergy+=energy;
    return energy;    
    }
  return 0;
  }

@Override
public String toString()
  {
  return "Worksite Base["+storedEnergy+"]";
  }

@Override
public double getMaxEnergy()
  {
  return maxEnergyStored;
  }

@Override
public double getEnergyStored()
  {
  return storedEnergy;
  }

@Override
public double getMaxInput()
  {
  return maxInput;
  }

@Override
public boolean canInput(ForgeDirection from)
  {
  return true;
  }

@Override
public final boolean hasWork()
  {
  return storedEnergy<maxEnergyStored && !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);  
  }

@Override
public String getOwnerName()
  {  
  return owningPlayer;
  }

public boolean hasAltSetupGui()
  {
  return false;
  }

public void openAltGui(EntityPlayer player)
  {
  //noop, must be implemented by individual tiles, if they have an alt-control gui
  }

public boolean hasUserSetTargets()
  {
  return canUserSetBlocks;
  }

public Set<BlockPosition> getUserSetTargets()
  {
  return userTargetBlocks;
  }

public void setUserSetTargets(Set<BlockPosition> targets)
  {
  if(canUserSetBlocks)
    {
    userTargetBlocks.clear();
    userTargetBlocks.addAll(targets);
    }
  }

public void addUserBlock(BlockPosition pos)
  {
  userTargetBlocks.add(pos);
  }

public void removeUserBlock(BlockPosition pos)
  {
  this.userTargetBlocks.remove(pos);
  }

@Override
public final boolean canUpdate()
  {
  return canUpdate;
  }

@Override
public final boolean hasWorkBounds()
  {
  return bbMin !=null || (bbMin!=null && bbMax!=null);
  }

@Override
public final BlockPosition getWorkBoundsMin()
  {
  return bbMin;
  }

@Override
public final BlockPosition getWorkBoundsMax()
  {
  return bbMax;
  }

public final void addStackToInventory(ItemStack stack, RelativeSide... sides)
  {
  int mcSide;
  for(RelativeSide side: sides)
    {
    mcSide = inventory.getAccessDirectionFor(side);
    stack = InventoryTools.mergeItemStack(inventory, stack, mcSide);
    if(stack==null)
      {
      break;
      }
    }
  if(stack!=null)
    {
    inventoryOverflow.add(stack);  
    }
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(worldObj.isRemote){return;}
  worldObj.theProfiler.startSection("AWWorksite");
  if(!inventoryOverflow.isEmpty())
    {
    updateOverflowInventory();
    } 
  incrementalScan();
  if(getEnergyStored()>=getMaxEnergy() && inventoryOverflow.isEmpty())
    {
    if(processWork())
      {
      storedEnergy -= AWAutomationStatics.energyPerWorkUnit;
      if(storedEnergy<0){storedEnergy = 0.d;}
      }    
    }
  worldObj.theProfiler.endSection();
  }

protected void incrementalScan()
  {
  if(blocksToUpdate.isEmpty())
    {
    fillBlocksToProcess();
    }
  if(!blocksToUpdate.isEmpty())
    {
    int rand = worldObj.rand.nextInt(blocksToUpdate.size());
    BlockPosition pos = blocksToUpdate.remove(rand);
    scanBlockPosition(pos);
    }
  }

private void updateOverflowInventory()
  {
  List<ItemStack> notMerged = new ArrayList<ItemStack>();
  Iterator<ItemStack> it = inventoryOverflow.iterator();
  ItemStack stack;
  while(it.hasNext() && (stack=it.next())!=null)
    {
    it.remove();
    stack = InventoryTools.mergeItemStack(inventory, stack, inventory.getAccessDirectionFor(RelativeSide.TOP));
    if(stack!=null)
      {
      notMerged.add(stack);
      }      
    }
  if(!notMerged.isEmpty())
    {
    inventoryOverflow.addAll(notMerged);    
    }
  }

public final boolean canWork()
  {
  return inventoryOverflow.isEmpty();
  }

@Override
public abstract boolean onBlockClicked(EntityPlayer player);

private final void setWorkBoundsMin(BlockPosition min)
  {
  bbMin = min;
  }

private final void setWorkBoundsMax(BlockPosition max)
  {
  bbMax = max;
  }

@Override
public final void setBounds(BlockPosition min, BlockPosition max)
  {  
  setWorkBoundsMin(min);
  setWorkBoundsMax(max);
  }

public final String getOwningPlayer()
  {
  return owningPlayer;
  }

@Override
public void setOwnerName(String name)
  {
  if(name==null){name="";}
  this.owningPlayer = name;  
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  if(bbMin!=null)
    {
    NBTTagCompound innerTag = new NBTTagCompound();
    bbMin.writeToNBT(innerTag);
    tag.setTag("bbMin", innerTag);
    }
  if(bbMax!=null)
    {
    NBTTagCompound innerTag = new NBTTagCompound();
    bbMax.writeToNBT(innerTag);
    tag.setTag("bbMax", innerTag);
    }
  if(owningPlayer!=null)
    {
    tag.setString("owner", owningPlayer);
    }
  if(inventory!=null)
    {
    NBTTagCompound invTag = new NBTTagCompound();
    inventory.writeToNBT(invTag);
    tag.setTag("inventory", invTag);    
    }
  if(!userTargetBlocks.isEmpty())
    {
    NBTTagList list = new NBTTagList();
    NBTTagCompound posTag;
    for(BlockPosition pos : userTargetBlocks)
      {
      posTag = new NBTTagCompound();
      pos.writeToNBT(posTag);
      list.appendTag(posTag);
      }    
    tag.setTag("userBlocks", list);
    }
  if(!inventoryOverflow.isEmpty())
    {
    NBTTagList list = new NBTTagList();
    NBTTagCompound stackTag;
    for(ItemStack item : inventoryOverflow)
      {
      stackTag = new NBTTagCompound();
      stackTag = item.writeToNBT(stackTag);
      list.appendTag(stackTag);
      }
    tag.setTag("inventoryOverflow", list);
    }
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  if(tag.hasKey("bbMin"))
    {
    bbMin = new BlockPosition();
    bbMin.read(tag.getCompoundTag("bbMin"));
    }
  if(tag.hasKey("bbMax"))
    {
    bbMax = new BlockPosition();
    bbMax.read(tag.getCompoundTag("bbMax"));
    }
  if(tag.hasKey("owner"))
    {
    owningPlayer = tag.getString("owner");
    }
  if(tag.hasKey("inventory") && inventory!=null)
    {
    inventory.readFromNBT(tag.getCompoundTag("inventory"));
    }
  if(tag.hasKey("userBlocks"))
    {
    NBTTagList list = tag.getTagList("userBlocks", Constants.NBT.TAG_COMPOUND);
    BlockPosition pos;
    for(int i = 0; i < list.tagCount(); i++)
      {
      pos = new BlockPosition(list.getCompoundTagAt(i));
      userTargetBlocks.add(pos);
      }
    }
  if(tag.hasKey("inventoryOverflow"))
    {
    NBTTagList list = tag.getTagList("inventoryOverflow", Constants.NBT.TAG_COMPOUND);
    NBTTagCompound itemTag;
    ItemStack stack;
    for(int i = 0; i < list.tagCount(); i++)
      {
      itemTag = list.getCompoundTagAt(i);
      stack = ItemStack.loadItemStackFromNBT(itemTag);
      if(stack!=null)
        {
        inventoryOverflow.add(stack);
        }
      }
    }
  }

@Override
public final Packet getDescriptionPacket()
  {
  NBTTagCompound tag = new NBTTagCompound();
  if(bbMin!=null)
    {
    NBTTagCompound innerTag = new NBTTagCompound();
    bbMin.writeToNBT(innerTag);
    tag.setTag("bbMin", innerTag);
    }
  if(bbMax!=null)
    {
    NBTTagCompound innerTag = new NBTTagCompound();
    bbMax.writeToNBT(innerTag);
    tag.setTag("bbMax", innerTag);
    }
  if(owningPlayer!=null)
    {
    tag.setString("owner", owningPlayer);
    }
  writeClientData(tag);
  return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 3, tag);
  }

@Override
public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {
  super.onDataPacket(net, pkt);
  NBTTagCompound tag = pkt.func_148857_g();
  if(tag.hasKey("bbMin"))
    {
    bbMin = new BlockPosition();
    bbMin.read(tag.getCompoundTag("bbMin"));
    }
  if(tag.hasKey("bbMax"))
    {
    bbMax = new BlockPosition();
    bbMax.read(tag.getCompoundTag("bbMax"));
    }
  readClientData(tag);
  }

@Override
public final Team getTeam()
  {  
  if(owningPlayer!=null)
    {
    worldObj.getScoreboard().getPlayersTeam(owningPlayer);
    }
  return null;
  }

/**
 * called when tile is sending an update to client.  implementations should
 * write any client-side data that needs updating to the tag
 * @param tag
 */
public abstract void writeClientData(NBTTagCompound tag);

/**
 * called from client-side tile to read client-data from the tag written by
 * {@link #writeClientData(NBTTagCompound)}
 * @param tag
 */
public abstract void readClientData(NBTTagCompound tag);

@Override
public int getSizeInventory()
  {
  return inventory.getSizeInventory();
  }

@Override
public ItemStack getStackInSlot(int var1)
  {
  return inventory.getStackInSlot(var1);
  }

@Override
public ItemStack decrStackSize(int var1, int var2)
  {
  return inventory.decrStackSize(var1, var2);
  }

@Override
public ItemStack getStackInSlotOnClosing(int var1)
  {
  return inventory.getStackInSlotOnClosing(var1);
  }

@Override
public void setInventorySlotContents(int var1, ItemStack var2)
  {
  inventory.setInventorySlotContents(var1, var2);
  }

@Override
public String getInventoryName()
  {
  return inventory.getInventoryName();
  }

@Override
public boolean hasCustomInventoryName()
  {
  return inventory.hasCustomInventoryName();
  }

@Override
public int getInventoryStackLimit()
  {
  return inventory.getInventoryStackLimit();
  }

@Override
public boolean isUseableByPlayer(EntityPlayer var1)
  {
  return inventory.isUseableByPlayer(var1);
  }

@Override
public void openInventory()
  {
  inventory.openInventory();
  }

@Override
public void closeInventory()
  {
  inventory.closeInventory();
  }

@Override
public boolean isItemValidForSlot(int var1, ItemStack var2)
  {
  return inventory.isItemValidForSlot(var1, var2);
  }

@Override
public int[] getAccessibleSlotsFromSide(int var1)
  {
  return inventory.getAccessibleSlotsFromSide(var1);
  }

@Override
public boolean canInsertItem(int var1, ItemStack var2, int var3)
  {
  return inventory.canInsertItem(var1, var2, var3);
  }

@Override
public boolean canExtractItem(int var1, ItemStack var2, int var3)
  {
  return inventory.canExtractItem(var1, var2, var3);
  }

}
