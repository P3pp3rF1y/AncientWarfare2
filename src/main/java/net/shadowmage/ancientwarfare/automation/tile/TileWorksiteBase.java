package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.automation.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.inventory.ISidedTile;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

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
public abstract class TileWorksiteBase extends TileEntity implements IWorkSite, IInventory, ISidedInventory, ISidedTile
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
 * maximum number of workers for this work-site
 * should be set in constructor of implementing classes
 */
int maxWorkers;

/**
 * should updateEntity be called for this tile?
 */
protected boolean canUpdate;

protected boolean canUserSetBlocks;

protected boolean shouldSendWorkTargets;

private Set<BlockPosition> userTargetBlocks = new HashSet<BlockPosition>();

private Set<IWorker> workers = Collections.newSetFromMap( new WeakHashMap<IWorker, Boolean>());

protected String owningPlayer;

public InventorySided inventory;

private ArrayList<BlockPosition> clientWorkTargets = new ArrayList<BlockPosition>();

public TileWorksiteBase()
  {
  
  }

@Override
public void markDirty()
  {
  super.markDirty();
  if(!worldObj.isRemote)
    {
    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
  }

@Override
public int getTileMeta()
  {
  return getBlockMetadata();
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
    if(!this.worldObj.isRemote)
      {
      this.markDirty();    
      }
    }
  }

public void addUserBlock(BlockPosition pos)
  {
  userTargetBlocks.add(pos);
  if(!this.worldObj.isRemote)
    {
    this.markDirty();    
    }
  }

public void removeUserBlock(BlockPosition pos)
  {
  this.userTargetBlocks.remove(pos);
  }

@Override
public final boolean canHaveWorker(IWorker worker)
  {
  if(!worker.getWorkTypes().contains(getWorkType()) || worker.getTeam() != this.getTeam())
    {
    return false;
    }
  if(workers.contains(worker))
    {
    return true;
    }
  return workers.size()<maxWorkers;
  }

@Override
public final boolean addWorker(IWorker worker)
  {
  if(workers.size()<maxWorkers || workers.contains(worker))
    {
    workers.add(worker);
    return true;
    }
  return false;
  }

@Override
public final void removeWorker(IWorker worker)
  {
  workers.remove(worker);
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

public abstract void initWorkSite();

public abstract boolean onBlockClicked(EntityPlayer player);

/**
 * subclasses should add any work-targets they want sent to clients to this list.
 * targets will only be sent to clients if the config option sendWorkToClients==true
 * can add original/primary reference, no need to copy -- input is not changed or 
 * cached in any way only used to write out to nbt
 * @param targets
 */
public abstract void addWorkTargets(List<BlockPosition> targets);

public final void setWorkBoundsMin(BlockPosition min)
  {
  bbMin = min;
  }

public final void setWorkBoundsMax(BlockPosition max)
  {
  bbMax = max;
  }

public final void setWorkBounds(BlockPosition min, BlockPosition max)
  {  
  setWorkBoundsMin(min);
  setWorkBoundsMax(max);
  }

public final String getOwningPlayer()
  {
  return owningPlayer;
  }

public final void setOwningPlayer(String name)
  {
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
  }

@Override
public List<BlockPosition> getWorkTargets()
  {
  return clientWorkTargets;
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
  if(shouldSendWorkTargets && AWAutomationStatics.sendWorkToClients)
    {
    ArrayList<BlockPosition> blockList = new ArrayList<BlockPosition>();
    addWorkTargets(blockList);
    if(!blockList.isEmpty())
      {
      NBTTagList list = new NBTTagList();
      NBTTagCompound posTag;
      for(BlockPosition pos : blockList)
        {
        posTag = new NBTTagCompound();
        pos.writeToNBT(posTag);
        list.appendTag(posTag);
        }    
      tag.setTag("workBlocks", list);      
      }
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
  if(tag.hasKey("userBlocks"))
    {
    userTargetBlocks.clear();
    NBTTagList list = tag.getTagList("userBlocks", Constants.NBT.TAG_COMPOUND);
    BlockPosition pos;
    for(int i = 0; i < list.tagCount(); i++)
      {
      pos = new BlockPosition(list.getCompoundTagAt(i));
      userTargetBlocks.add(pos);
      }
    }
  if(tag.hasKey("workBlocks"))
    {
    clientWorkTargets.clear();
    NBTTagList list = tag.getTagList("workBlocks", Constants.NBT.TAG_COMPOUND);
    BlockPosition pos;
    for(int i = 0; i < list.tagCount(); i++)
      {
      pos = new BlockPosition(list.getCompoundTagAt(i));
      clientWorkTargets.add(pos);
      }
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
