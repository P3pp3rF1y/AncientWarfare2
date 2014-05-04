package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedTile;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class WorkSiteWarehouse extends TileEntity implements IWorkSite, IInteractableTile, IBoundedTile, IOwnable
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

private Set<IWorker> workers = Collections.newSetFromMap( new WeakHashMap<IWorker, Boolean>());

private List<IWarehouseStorageTile> storageTiles = new ArrayList<IWarehouseStorageTile>();
private List<BlockPosition> inputBlocks = new ArrayList<BlockPosition>();

protected String owningPlayer;

private boolean init = false;

private boolean shouldRescan = false;

private boolean hasWork = false;

public WorkSiteWarehouse()
  {
  bbMin = new BlockPosition();
  bbMax = new BlockPosition();
  }

@Override
public void invalidate()
  {  
  super.invalidate();
  init = false;
  TileEntity te;
  IControlledTile ict;
  for(BlockPosition pos : this.inputBlocks)
    {
    te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
    if(te instanceof IControlledTile)
      {
      ict = (IControlledTile)te;
      ict.setControllerPosition(null);
      }
    }
  for(IWarehouseStorageTile tile : this.storageTiles)
    {
    if(tile instanceof IControlledTile)
      {
      ict = (IControlledTile)tile;
      ict.setControllerPosition(null);
      }
    }
  }

@Override
public void validate()
  {  
  super.validate();
  init = false;
  hasWork = false;
  }

public void addInputBlock(int x, int y, int z)
  {
  BlockPosition test = new BlockPosition(x,y,z);
  if(inputBlocks.contains(test)){return;}
  AWLog.logDebug("adding input block at: "+x+","+y+","+z);
  inputBlocks.add(test);
  AWLog.logDebug("input blocks now contains: "+inputBlocks);
  }

public void addStorageBlock(IWarehouseStorageTile tile)
  {
  if(!storageTiles.contains(tile))
    {
    AWLog.logDebug("adding storage tile of: "+tile);
    storageTiles.add(tile);
    AWLog.logDebug("storage blocks now contains: "+storageTiles);
    }
  }

public void removeInputBlock(int x, int y, int z)
  {
  BlockPosition test = new BlockPosition(x,y,z);
  inputBlocks.remove(test);
  AWLog.logDebug("removing input block at: "+test);
  AWLog.logDebug("input blocks now contains: "+inputBlocks);
  }

public void removeStorageBlock(IWarehouseStorageTile tile)
  {
  AWLog.logDebug("removing storage tile of: "+tile);
  storageTiles.remove(tile);
  AWLog.logDebug("storage blocks now contains: "+storageTiles);
  }

/**
 * should be called when tile is first loaded from disk, after world is set
 */
protected void scanInitialBlocks()
  {
  AWLog.logDebug("warehouse scanning initial blocks..."); 
  TileEntity te;
  for(int x = bbMin.x; x<=bbMax.x; x++)
    {
    for(int z = bbMin.z; z<=bbMax.z; z++)
      {
      for(int y = bbMin.y; y<=bbMax.y; y++)
        {
        if(!worldObj.blockExists(x, y, z)){continue;}
        te = worldObj.getTileEntity(x, y, z);
        if(te==null){continue;}
        else if(te instanceof IWarehouseStorageTile)
          {
          addStorageBlock((IWarehouseStorageTile) te);
          if(te instanceof IControlledTile)
            {
            ((IControlledTile) te).setControllerPosition(new BlockPosition(xCoord, yCoord, zCoord));
            }
          }
        else if(te instanceof TileWarehouseInput)
          {
          addInputBlock(te.xCoord, te.yCoord, te.zCoord);
          if(te instanceof IControlledTile)
            {
            ((IControlledTile) te).setControllerPosition(new BlockPosition(xCoord, yCoord, zCoord));
            }
          }
        }
      }
    }
  this.scanInputInventory();
  }

@Override
public void updateEntity()
  {
  if(!init)
    {
    init = true;
    if(!worldObj.isRemote)
      {
      scanInitialBlocks();      
      }
    }
  if(shouldRescan)
    {    
    shouldRescan = false;
    scanInputInventory();
    }
  }

@Override
public boolean hasWork()
  {
  return hasWork;
  }

@Override
public void doWork(IWorker worker)
  {
  if(hasWork)
    {
    processWork();    
    }
  }

@Override
public void doPlayerWork(EntityPlayer player)
  {
  if(hasWork)
    {
    processWork();    
    }
  }

private void processWork()
  {
  TileEntity te;
  TileWarehouseInput twi;
  ItemStack item;
  for(BlockPosition pos : inputBlocks)
    {
    te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
    if(te instanceof TileWarehouseInput)
      {
      twi = (TileWarehouseInput)te;
      for(int i = 0; i< twi.getSizeInventory(); i++)
        {
        item = twi.getStackInSlot(i);
        if(item==null){continue;}
        for(IWarehouseStorageTile tile : this.storageTiles)
          {
          if(tile.isItemValid(item))
            {
            item = InventoryTools.mergeItemStack((IInventory) tile, item, -1);
            if(item==null)
              {
              twi.setInventorySlotContents(i, null);
              return;
              }
            }
          }
        }
      }
    }  
  scanInputInventory();
  }

public void onInputInventoryUpdated()
  {
  shouldRescan = true;
  hasWork = false;
//  scanInputInventory();
  }

private void scanInputInventory()
  {
  shouldRescan = false;
  hasWork = false;
  AWLog.logDebug("rescanning controlled input inventories...");
  TileEntity te;
  TileWarehouseInput twi;
  ItemStack item;
  for(BlockPosition pos : inputBlocks)
    {
    te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
    if(te instanceof TileWarehouseInput)
      {
      twi = (TileWarehouseInput)te;
      for(int i = 0; i< twi.getSizeInventory(); i++)
        {
        item = twi.getStackInSlot(i);
        if(item==null){continue;}
        hasWork = true;
        return;
        }
      }
    }  
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.CRAFTING;
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_CONTROL, xCoord, yCoord, zCoord);    
    }
  return true;
  }

@Override
public final boolean addWorker(IWorker worker)
  {
  if(!worker.getWorkTypes().contains(getWorkType()) || worker.getTeam() != this.getTeam())
    {
    return false;
    }
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
  return true;
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

@Override
public final Team getTeam()
  {  
  if(owningPlayer!=null)
    {
    worldObj.getScoreboard().getPlayersTeam(owningPlayer);
    }
  return null;
  }

@Override
public List<BlockPosition> getWorkTargets()
  {
  return Collections.emptyList();
  }

public final void setOwnerName(String name)
  {
  this.owningPlayer = name;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  owningPlayer = tag.getString("owner");
  bbMin.read(tag.getCompoundTag("pos1"));
  bbMax.read(tag.getCompoundTag("pos2"));  
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setString("owner", owningPlayer);
  tag.setTag("pos1", bbMin.writeToNBT(new NBTTagCompound()));
  tag.setTag("pos2", bbMax.writeToNBT(new NBTTagCompound()));
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
  return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 3, tag);
  }

@Override
public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {
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
  }

@Override
public void setBounds(BlockPosition p1, BlockPosition p2)
  {
  bbMin = p1;
  bbMax = p2;
  }

/**
 * need to map the available storage inventories so as to know what tile(s) to query when attempting
 * an item insert.<br>
 * 
 */
/**
 * 
 * @author Shadowmage
 *
 */
private static final class WarehouseItemMap
{
List<TileWarehouseInput> warehouseInput;
List<IWarehouseStorageTile> generalStorage;
Map<Item, ItemEntry> filteredStorage;

}

private static final class ItemEntry
{

}

}
