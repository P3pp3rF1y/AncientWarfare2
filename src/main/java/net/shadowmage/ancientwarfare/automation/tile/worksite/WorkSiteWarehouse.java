package net.shadowmage.ancientwarfare.automation.tile.worksite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseControl;
import net.shadowmage.ancientwarfare.automation.tile.IControlledTile;
import net.shadowmage.ancientwarfare.automation.tile.IWarehouseStorageTile;
import net.shadowmage.ancientwarfare.automation.tile.WarehouseItemFilter;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedTile;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.ItemQuantityMap;

public class WorkSiteWarehouse extends TileWorksiteBase implements IWorkSite, IInteractableTile, IBoundedTile, IOwnable
{

/**************************WORKSITE FIELDS******************************/
private BlockPosition bbMin;
private BlockPosition bbMax;
private String owningPlayer;

/**************************WAREHOUSE FIELDS******************************/
private boolean init = false;
private boolean warehouseInventoryUpdated = false;
private List<IWarehouseStorageTile> storageTiles = new ArrayList<IWarehouseStorageTile>();
private List<TileWarehouseInput> inputTiles = new ArrayList<TileWarehouseInput>();
private List<TileWarehouseOutput> outputTiles = new ArrayList<TileWarehouseOutput>();
private Set<TileEntity> tilesToUpdate = new HashSet<TileEntity>();
private List<ContainerWarehouseControl> viewers = new ArrayList<ContainerWarehouseControl>();
public InventoryBasic inventory = new InventoryBasic(9);//manual input/output inventory
public ItemQuantityMap inventoryMap = new ItemQuantityMap();//TODO make this private, wrap whatever is needed for the container in access methods
/**************************WORK QUEUES******************************/
private List<TileWarehouseOutput> outputToCheck = new ArrayList<TileWarehouseOutput>();
private List<TileWarehouseOutput> outputToFill = new ArrayList<TileWarehouseOutput>();
private List<TileWarehouseInput> inputToEmpty = new ArrayList<TileWarehouseInput>();

int currentItemCount;//used slots--calced from item quantity map
int currentMaxItemCount;//max number of slots -- calced from storage blocks

double maxEnergyStored = 1600;
double maxInput = 100;
private double storedEnergy;

public WorkSiteWarehouse()
  {
  bbMin = new BlockPosition();
  bbMax = new BlockPosition();
  }

@Override
public boolean canInput(ForgeDirection from)
  {
  return true;
  }

@Override
public void addEnergyFromWorker(IWorker worker)
  {
  storedEnergy += AWCoreStatics.energyPerWorkUnit * worker.getWorkEffectiveness(getWorkType());
  }

@Override
public void updateEntity()
  {
  worldObj.theProfiler.startSection("AWWorksite");
  worldObj.theProfiler.startSection("Warehouse");
  if(!init)
    {
    init = true;
    scanInitialBlocks();  
    }
  if(!worldObj.isRemote)
    {
    if(warehouseInventoryUpdated)
      {
      warehouseInventoryUpdated = false;
      recheckOutputTiles();
      informStorageTilesOfUpdate();      
      }
    if(!tilesToUpdate.isEmpty())
      {
      long t1 = System.nanoTime();
      updateTiles();
      long t2 = System.nanoTime();
      long t3 = (t2-t1);
      float f1 = (float)((double)t3 / 1000000.d);
//      AWLog.logDebug("tilesToUpdate update time: "+(t2-t1)+"ns ("+f1+"ms)");
      }
    if(hasWork() && storedEnergy==maxEnergyStored)
      {
      processWork();
      storedEnergy-=maxEnergyStored;
      }
    }  
  worldObj.theProfiler.endSection();
  worldObj.theProfiler.endSection();
  }

private void updateTiles()
  {
  for(TileEntity te : tilesToUpdate)
    {
    if(te instanceof TileWarehouseInput)
      {
      updateInputTile((TileWarehouseInput) te);
      }
    else if(te instanceof TileWarehouseOutput)
      {
      updateOutputTile((TileWarehouseOutput)te);
      }
    }
  tilesToUpdate.clear();
  }


/************************************************ MULTIBLOCK SYNCH METHODS *************************************************/

public void addInputBlock(TileWarehouseInput input)
  {
  if(!inputTiles.contains(input))
    {
    inputTiles.add(input);
    tilesToUpdate.add(input);
    }
  }

public void removeInputBlock(TileWarehouseInput input)
  {
  while(inputTiles.contains(input))
    {
    inputTiles.remove(input);
    }
  while(inputToEmpty.contains(input))
    {
    inputToEmpty.remove(input);
    }
  tilesToUpdate.remove(input);
  }

public List<TileWarehouseInput> getInputTiles()
  {
  return inputTiles;
  }

public void addStorageBlock(IWarehouseStorageTile tile)
  {
  if(!storageTiles.contains(tile))
    {
    storageTiles.add(tile);  
    currentMaxItemCount+=tile.getStorageAdditionSize();
//    AWLog.logDebug("updated warehouse storage size to: "+currentMaxItemCount);
    tile.onWarehouseInventoryUpdated(this);
    }
  }

public void removeStorageBlock(IWarehouseStorageTile tile)
  {
  while(storageTiles.contains(tile))
    {
    storageTiles.remove(tile);    
    currentMaxItemCount-=tile.getStorageAdditionSize();
//    AWLog.logDebug("updated warehouse storage size to: "+currentMaxItemCount);
    }
  }

public List<IWarehouseStorageTile> getStorageTiles()
  {
  return storageTiles;
  }

public void addOutputBlock(TileWarehouseOutput te)
  {
  if(!outputTiles.contains(te))
    {
    outputTiles.add(te);
    tilesToUpdate.add(te);
    }
  }

public void removeOutputBlock(TileWarehouseOutput te)
  {
  while(outputTiles.contains(te))
    {
    outputTiles.remove(te);
    }
  while(outputToFill.contains(te))
    {
    outputToFill.remove(te);
    }
  while(outputToCheck.contains(te))
    {
    outputToFill.remove(te);
    }
  tilesToUpdate.remove(te);
  }

public List<TileWarehouseOutput> getOutputTiles()
  {
  return outputTiles;
  }

@Override
public void invalidate()
  {  
  super.invalidate();
  init = false;
  IControlledTile ict;
  for(TileWarehouseInput tile : this.inputTiles)
    {
    tile.setControllerPosition(null);
    }
  for(TileWarehouseOutput tile : this.outputTiles)
    {
    tile.setControllerPosition(null);
    }
  for(IWarehouseStorageTile tile : this.storageTiles)
    {
    if(tile instanceof IControlledTile)
      {
      ict = (IControlledTile)tile;
      ict.setControllerPosition(null);
      }
    }
  this.inputTiles.clear();
  this.outputTiles.clear();
  this.storageTiles.clear();
  this.tilesToUpdate.clear();
  this.inputToEmpty.clear();  
  this.outputToCheck.clear();
  this.outputToFill.clear();
  this.viewers.clear();
  }

@Override
public void validate()
  {  
  super.validate();
  init = false;
  }

/**
 * should be called when tile is first loaded from disk, after world is set
 */
protected void scanInitialBlocks()
  {
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
          addInputBlock((TileWarehouseInput) te);
          if(te instanceof IControlledTile)
            {
            ((IControlledTile) te).setControllerPosition(new BlockPosition(xCoord, yCoord, zCoord));
            }
          }
        }
      }
    }
  }


/************************************************ INVENTORY TRACKING METHODS *************************************************/

private void onWarehouseInventoryUpdated()
  {
  warehouseInventoryUpdated = true;
  }

private void recheckOutputTiles()
  {
  Set<TileWarehouseOutput> toUpdate = new HashSet<TileWarehouseOutput>();
  toUpdate.addAll(outputToCheck);
  toUpdate.addAll(outputToFill);
  outputToCheck.clear();
  outputToFill.clear();
  for(TileWarehouseOutput tile : toUpdate)
    {
    updateOutputTile(tile);
    }
  updateViewers();
  }

private void informStorageTilesOfUpdate()
  {
  for(IWarehouseStorageTile tile : this.storageTiles)
    {
    tile.onWarehouseInventoryUpdated(this);
    }
  }

public void onInputInventoryUpdated(TileWarehouseInput tile)
  {
  if(inputTiles.contains(tile))
    {
    tilesToUpdate.add(tile);    
    }
  }

public void onOutputInventoryUpdated(TileEntity tile)
  {
  if(outputTiles.contains(tile))
    {
    tilesToUpdate.add(tile);    
    }
  }

public void requestItem(ItemStack filter)
  {
  int quantity = inventoryMap.getCount(filter);
  if(quantity>filter.getMaxStackSize())
    {
    quantity = filter.getMaxStackSize();
    }
  if(quantity<=0){return;}
  ItemStack toMerge = filter.copy();
  toMerge.stackSize = quantity;
  inventoryMap.decreaseCount(filter, quantity);
  toMerge = InventoryTools.mergeItemStack(inventory, toMerge, -1);
  if(toMerge!=null)
    {
    inventoryMap.addCount(toMerge, toMerge.stackSize);
    }
  updateViewers();
  onWarehouseInventoryUpdated();
  }

private void updateInputTile(TileWarehouseInput tile)
  {
  inputToEmpty.remove(tile);
  ItemStack item;
  for(int i = 0; i < tile.getSizeInventory(); i++)
    {
    item = tile.getStackInSlot(i);
    if(item!=null)
      {
      inputToEmpty.add(tile);
      break;
      }
    }
  }

private void updateOutputTile(TileWarehouseOutput tile)
  {
  outputToFill.remove(tile);//remove it in case it was already present in the toFill set
  outputToCheck.remove(tile);
  List<WarehouseItemFilter> filters = tile.getFilters();
  int count;
  for(WarehouseItemFilter filter : filters)
    {
    if(filter.getFilterItem()==null){continue;}
    count = InventoryTools.getCountOf(tile, -1, filter.getFilterItem()); 
    if(count<filter.getFilterQuantity())
      {
      count = inventoryMap.getCount(filter.getFilterItem());
      if(count>0)
        {
        outputToFill.add(tile);        
        }
      else
        {
        outputToCheck.add(tile);
        }
      break;
      }
    }  
  }

private void updateSlotCount()
  {
  this.currentItemCount = inventoryMap.getTotalItemCount();
  }

public int getCountOf(ItemStack item)
  {
  return inventoryMap.getCount(item);
  }

public void decreaseCountOf(ItemStack item, int count)
  {
  inventoryMap.decreaseCount(item, count);
  onWarehouseInventoryUpdated();
  }

/************************************************ WORKSITE METHODS *************************************************/

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
public void setBounds(BlockPosition p1, BlockPosition p2)
  {
  bbMin = p1;
  bbMax = p2;
  }

private boolean hasWarehouseWork()
  {
  return (!inputToEmpty.isEmpty() && currentItemCount<currentMaxItemCount) || !outputToFill.isEmpty();
  }

@Override
protected boolean processWork()
  {
  long t1 = System.nanoTime();  
  if(!inputToEmpty.isEmpty())
    {
    processInputWork();
    return true;
    }
  else if(!outputToFill.isEmpty())
    {
    processOutputWork();
    return true;
    }
  long t2 = System.nanoTime();
  long t3 = (t2-t1);
  float f1 = (float)((double)t3 / 1000000.d);
//  AWLog.logDebug("work time: "+(t2-t1)+"ns ("+f1+"ms)");
  updateViewers();
  return false;
  }

private void processInputWork()
  {
  TileWarehouseInput tile;
  outerLoopLabel:
  while(!inputToEmpty.isEmpty())
    {
    tile = inputToEmpty.remove(0);
    ItemStack stack;
    int transferQuantity;
    for(int i=0; i<tile.getSizeInventory(); i++)
      {
      stack = tile.getStackInSlot(i);
      if(stack!=null)
        {
        transferQuantity = currentMaxItemCount-currentItemCount;
        if(transferQuantity>stack.stackSize)
          {
          transferQuantity=stack.stackSize;
          }
        inventoryMap.addCount(stack, transferQuantity);
        onWarehouseInventoryUpdated();
        stack.stackSize-=transferQuantity;
        currentItemCount+=transferQuantity;
        if(stack.stackSize<=0)
          {
          tile.setInventorySlotContents(i, null);
          }
        tilesToUpdate.add(tile);
        
        //if a non-null stack was found
        break outerLoopLabel;
        }
      }    
    tilesToUpdate.add(tile);
    }
  }

private void processOutputWork()
  {
  TileWarehouseOutput tile;
  List<WarehouseItemFilter> filters;
  ItemStack toMerge;
  int filterQuantity, foundQuantity, transferQuantity, passXfer;
    
  outerLoopLabel:  
  while(!outputToFill.isEmpty())
    {
    tile = outputToFill.remove(0);
    tilesToUpdate.add(tile);
    filters = tile.getFilters();
    for(WarehouseItemFilter filter : filters)
      {
      if(filter.getFilterItem()==null){continue;}
      filterQuantity = filter.getFilterQuantity();
      foundQuantity = InventoryTools.getCountOf(tile, -1, filter.getFilterItem());
      if(foundQuantity<filterQuantity)
        {
        transferQuantity = inventoryMap.getCount(filter.getFilterItem());
        if(transferQuantity==0){continue;}
        if(transferQuantity > filterQuantity-foundQuantity)
          {
          transferQuantity = filterQuantity - foundQuantity;
          }
        while(transferQuantity>0)
          {
          toMerge = filter.getFilterItem().copy();
          passXfer = transferQuantity;
          if(passXfer>toMerge.getMaxStackSize())
            {
            passXfer = toMerge.getMaxStackSize();
            }
          toMerge.stackSize = passXfer;
          transferQuantity -= passXfer;
          inventoryMap.decreaseCount(toMerge, passXfer);
          onWarehouseInventoryUpdated();
          toMerge = InventoryTools.mergeItemStack(tile, toMerge, -1);
          if(toMerge!=null)//could only partially merge--perhaps output is full?
            {
            inventoryMap.addCount(toMerge, toMerge.stackSize);
            break;
            }
          }
        if(transferQuantity != filterQuantity-foundQuantity)//at least one item was merged, break completely out as work was done
          {
          break outerLoopLabel;
          }
        }
      }        
    }
  }


/************************************************ NETWORK METHODS *************************************************/

public void addViewer(ContainerWarehouseControl viewer)
  {
  if(!viewers.contains(viewer))
    {
    viewers.add(viewer);
    }
  }

public void removeViewer(ContainerWarehouseControl viewer)
  {  
  while(viewers.contains(viewer))
    {
    viewers.remove(viewer);
    }
  }

public void updateViewers()
  {
  for(ContainerWarehouseControl container : this.viewers)
    {
    container.refreshGui();
    if(!worldObj.isRemote)
      {
      container.onWarehouseInventoryUpdated();
      }
    }
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  bbMin.read(tag.getCompoundTag("pos1"));
  bbMax.read(tag.getCompoundTag("pos2"));  
  if(tag.hasKey("inventory"))
    {
    inventory.readFromNBT(tag.getCompoundTag("inventory"));
    }
  if(tag.hasKey("itemMap"))
    {
    inventoryMap.readFromNBT(tag.getCompoundTag("itemMap"));
    }
  this.updateSlotCount();
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setTag("pos1", bbMin.writeToNBT(new NBTTagCompound()));
  tag.setTag("pos2", bbMax.writeToNBT(new NBTTagCompound()));
  tag.setTag("inventory", inventory.writeToNBT(new NBTTagCompound()));
  tag.setTag("itemMap", inventoryMap.writeToNBT(new NBTTagCompound()));
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
  return inventory.getSizeInventory();
  }

@Override
public boolean isUseableByPlayer(EntityPlayer var1)
  {
  return true;
  }

@Override
public void openInventory()
  {  
  }

@Override
public void closeInventory()
  {  
  }

@Override
public boolean isItemValidForSlot(int var1, ItemStack var2)
  {
  return inventory.isItemValidForSlot(var1, var2);
  }

@Override
public int[] getAccessibleSlotsFromSide(int var1)
  {
  return new int[]{};
  }

@Override
public boolean canInsertItem(int var1, ItemStack var2, int var3)
  {
  return false;
  }

@Override
public boolean canExtractItem(int var1, ItemStack var2, int var3)
  {
  return false;
  }

@Override
protected boolean hasWorksiteWork()
  {
  return hasWarehouseWork();
  }

@Override
protected void updateOverflowInventory()
  {
  
  }

@Override
protected void updateWorksite()
  {
  
  }

}
