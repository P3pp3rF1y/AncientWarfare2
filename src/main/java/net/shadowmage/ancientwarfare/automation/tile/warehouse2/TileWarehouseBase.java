package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseControl;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseInterface.InterfaceEmptyRequest;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseInterface.InterfaceFillRequest;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteBounded;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public abstract class TileWarehouseBase extends TileWorksiteBounded implements IControllerTile
{

private boolean init;
private boolean shouldRecount;

private Set<TileWarehouseStockViewer> stockViewers = new HashSet<TileWarehouseStockViewer>();
private Set<TileWarehouseInterface> interfaceTiles = new HashSet<TileWarehouseInterface>();
private Set<IWarehouseStorageTile> storageTiles = new HashSet<IWarehouseStorageTile>();

/**
 * interfaces that need filling, AND there are items available to fill.  anytime storage block inventories are updated, this list needs to be
 * rechecked to make sure items are still available
 */
private Set<TileWarehouseInterface> interfacesToFill = new HashSet<TileWarehouseInterface>();

/**
 * interfaces that have an excess of items or non-matching items will be in this set
 */
private Set<TileWarehouseInterface> interfacesToEmpty = new HashSet<TileWarehouseInterface>();

protected WarehouseStorageMap storageMap = new WarehouseStorageMap();
private ItemQuantityMap cachedItemMap = new ItemQuantityMap();

private Set<ContainerWarehouseControl> viewers = new HashSet<ContainerWarehouseControl>();


public TileWarehouseBase()
  {
  
  }

@Override
protected void updateOverflowInventory(){}//NOOP

@Override
public void onBoundsAdjusted()
  {
  BlockPosition max = getWorkBoundsMax();
  BlockPosition min = getWorkBoundsMin();
  max.y = min.y + 3;
  this.interfacesToEmpty.clear();
  this.interfacesToFill.clear();
  for(TileWarehouseInterface i : interfaceTiles)
    {
    i.setController(null);
    }
  this.interfaceTiles.clear();
  for(TileWarehouseStockViewer i : stockViewers)
    {
    i.setController(null);
    }
  this.stockViewers.clear();
  for(IWarehouseStorageTile i : storageTiles)
    {
    ((TileControlled) i).setController(null);
    }
  this.storageTiles.clear();
  
  storageMap = new WarehouseStorageMap();
  cachedItemMap.clear();
  
  List<TileEntity> tiles = WorldTools.getTileEntitiesInArea(worldObj, min.x, min.y, min.z, max.x, max.y, max.z);
  for(TileEntity te : tiles)
    {
    if(te instanceof IWarehouseStorageTile)
      {
      addStorageTile((IWarehouseStorageTile) te);
      }
    else if(te instanceof TileWarehouseInterface)
      {
      addInterfaceTile((TileWarehouseInterface) te);
      }
    else if(te instanceof TileWarehouseStockViewer)
      {
      addStockViewer((TileWarehouseStockViewer) te);
      }
    }  
  }

@Override
public boolean userAdjustableBlocks(){return false;}

@Override
public EnumSet<WorksiteUpgrade> getValidUpgrades()
  {
  return EnumSet.of(
      WorksiteUpgrade.SIZE_MEDIUM,
      WorksiteUpgrade.SIZE_LARGE,
      WorksiteUpgrade.ENCHANTED_TOOLS_1,
      WorksiteUpgrade.ENCHANTED_TOOLS_2,
      WorksiteUpgrade.TOOL_QUALITY_1,
      WorksiteUpgrade.TOOL_QUALITY_2,
      WorksiteUpgrade.TOOL_QUALITY_3,
      WorksiteUpgrade.BASIC_CHUNK_LOADER
      );
  }

@Override
public int getBoundsMaxWidth()
  {
  return getUpgrades().contains(WorksiteUpgrade.SIZE_MEDIUM)? 9 : getUpgrades().contains(WorksiteUpgrade.SIZE_LARGE)? 16 : 5;
  }

@Override
public int getBoundsMaxHeight(){return 4;}

public abstract void handleSlotClick(EntityPlayer player, ItemStack filter, boolean shiftClick);

public void changeCachedQuantity(ItemStack filter, int change)
  {
  if(change>0)
    {
    cachedItemMap.addCount(filter, change);
    }
  else
    {
    cachedItemMap.decreaseCount(filter, -change);
    }
  updateViewers();
  }

private boolean tryEmptyInterfaces()
  {  
  List<TileWarehouseInterface> toEmpty = new ArrayList<TileWarehouseInterface>(interfacesToEmpty);  
  for(TileWarehouseInterface tile : toEmpty)
    {
    if(tryEmptyTile(tile))
      {
      tile.recalcRequests();
      return true;
      }
    }   
  return false;
  }

private boolean tryEmptyTile(TileWarehouseInterface tile)
  {
  List<InterfaceEmptyRequest> reqs = tile.getEmptyRequests();
  for(InterfaceEmptyRequest req : reqs)
    {
    if(tryRemoveFromRequest(tile, req)){return true;}   
    }
  return false;
  }

private boolean tryRemoveFromRequest(TileWarehouseInterface tile, InterfaceEmptyRequest request)
  {
  ItemStack stack = tile.getStackInSlot(request.slotNum);
  if(stack==null){return false;}
  int stackSize = stack.stackSize;
  int moved;
  List<IWarehouseStorageTile> potentialStorage = new ArrayList<IWarehouseStorageTile>();
  storageMap.getDestinations(stack, potentialStorage);
  for(IWarehouseStorageTile dest : potentialStorage)
    {
    moved = dest.insertItem(stack, stack.stackSize);
    if(moved>0)
      {
      cachedItemMap.addCount(stack, moved);
      updateViewers();
      }
    stack.stackSize -= moved;
    if(stack.stackSize!=stackSize)
      {
      if(stack.stackSize<=0)
        {
        tile.inventory.setInventorySlotContents(request.slotNum, null);
        }
      return true;
      }
    }  
  return false;
  }

private boolean tryFillInterfaces()
  {
  List<TileWarehouseInterface> toFill = new ArrayList<TileWarehouseInterface>(interfacesToFill);
  for(TileWarehouseInterface tile : toFill)
    {
    if(tryFillTile(tile))
      {
      tile.recalcRequests();
      return true;
      }
    }
  return false;
  }

private boolean tryFillTile(TileWarehouseInterface tile)
  {
  List<InterfaceFillRequest> reqs = tile.getFillRequests();
  for(InterfaceFillRequest req : reqs)
    {
    if(tryFillFromRequest(tile, req)){return true;}
    }
  return false;
  }

private boolean tryFillFromRequest(TileWarehouseInterface tile, InterfaceFillRequest request)
  {  
  List<IWarehouseStorageTile> potentialStorage = new ArrayList<IWarehouseStorageTile>();
  storageMap.getDestinations(request.requestedItem, potentialStorage);
  int found, moved;
  ItemStack stack;
  int stackSize;
  for(IWarehouseStorageTile source : potentialStorage)
    {
    found = source.getQuantityStored(request.requestedItem);
    if(found>0)
      {
      stack = request.requestedItem.copy();
      stack.stackSize = found>stack.getMaxStackSize() ? stack.getMaxStackSize() : found;
      stackSize = stack.stackSize;
      stack = InventoryTools.mergeItemStack(tile.inventory, stack, -1);
      if(stack==null || stack.stackSize!=stackSize)
        {        
        moved = stack==null ? stackSize : stackSize-stack.stackSize;
        source.extractItem(request.requestedItem, moved);
        cachedItemMap.decreaseCount(request.requestedItem, moved);  
        updateViewers();      
        return true;
        }
      }
    }
  return false;
  }

public final void getItems(ItemQuantityMap map)
  {
  map.addAll(cachedItemMap);
  }

@Override
public final void updateEntity()
  { 
  super.updateEntity();
  if(worldObj.isRemote){return;}
  if(!init)
    {
    init=true;  
    scanForInitialTiles();
    }
  if(shouldRecount)
    {
    shouldRecount=false;
    recountInventory();
    }
  }

@Override
protected boolean processWork()
  {
  if(!interfacesToEmpty.isEmpty())
    {
    if(tryEmptyInterfaces()){return true;}
    }
  if(!interfacesToFill.isEmpty())
    {
    if(tryFillInterfaces()){return true;}
    }
  return false;
  }

@Override
protected boolean hasWorksiteWork()
  {
  return !interfacesToEmpty.isEmpty() || !interfacesToFill.isEmpty();
  }

@Override
protected void updateWorksite()
  {
  
  }

private void scanForInitialTiles()
  {
  BlockPosition max = getWorkBoundsMax();
  BlockPosition min = getWorkBoundsMin();
  List<TileEntity> tiles = WorldTools.getTileEntitiesInArea(worldObj, min.x, min.y, min.z, max.x, max.y, max.z);
  for(TileEntity te : tiles)
    {
    if(te instanceof IWarehouseStorageTile)
      {
      addStorageTile((IWarehouseStorageTile) te);
      }
    else if(te instanceof TileWarehouseInterface)
      {
      addInterfaceTile((TileWarehouseInterface) te);
      }
    else if(te instanceof TileWarehouseStockViewer)
      {
      addStockViewer((TileWarehouseStockViewer) te);
      }
    }  
  }

private void recountInventory()
  {
  cachedItemMap.clear();  
  for(IWarehouseStorageTile tile : storageTiles)
    {
    tile.addItems(cachedItemMap);
    }
  }

public final void addViewer(ContainerWarehouseControl viewer)
  {
  if(worldObj.isRemote){return;}
  viewers.add(viewer);
  }

public final void removeViewer(ContainerWarehouseControl viewer){viewers.remove(viewer);}

public final void updateViewers()
  {
  for(ContainerWarehouseControl viewer : viewers)
    {
    viewer.onWarehouseInventoryUpdated();
    }
  for(TileWarehouseStockViewer viewer : stockViewers)
    {
    viewer.onWarehouseInventoryUpdated();
    }
  }

public final void addStorageTile(IWarehouseStorageTile tile)
  {
  if(worldObj.isRemote){return;}
  if(!storageTiles.contains(tile))
    {
    storageTiles.add(tile);    
    if(tile instanceof IControlledTile)
      {
      ((IControlledTile) tile).setController(this);
      }
    storageMap.addStorageTile(tile);
    tile.addItems(cachedItemMap);  
    }
  }

public final void removeStorageTile(IWarehouseStorageTile tile)
  {
  ItemQuantityMap iqm = new ItemQuantityMap();
  tile.addItems(iqm);  
  this.cachedItemMap.removeAll(iqm);
  storageTiles.remove(tile);
  storageMap.removeStorageTile(tile);
  updateViewers();
  }

public final void addInterfaceTile(TileWarehouseInterface tile)
  {
  if(worldObj.isRemote){return;}
  if(!interfaceTiles.contains(tile))
    {
    interfaceTiles.add(tile);  
    tile.setController(this);
    if(!tile.getEmptyRequests().isEmpty())
      {
      interfacesToEmpty.add(tile);
      }
    if(!tile.getFillRequests().isEmpty())
      {
      interfacesToFill.add(tile);
      }
    }
  }

public final void removeInterfaceTile(TileWarehouseInterface tile)
  {
  interfaceTiles.remove(tile);
  interfacesToFill.remove(tile);
  interfacesToEmpty.remove(tile);
  }

public final void onIterfaceInventoryChanged(TileWarehouseInterface tile)
  {  
  if(worldObj.isRemote){return;}
  interfacesToFill.remove(tile);
  interfacesToEmpty.remove(tile);
  if(!tile.getEmptyRequests().isEmpty())
    {
    interfacesToEmpty.add(tile);
    }
  if(!tile.getFillRequests().isEmpty())
    {
    interfacesToFill.add(tile);
    }
  }

public final void onInterfaceFilterChanged(TileWarehouseInterface tile)
  {
  if(worldObj.isRemote){return;}
  interfacesToFill.remove(tile);
  interfacesToEmpty.remove(tile);
  if(!tile.getEmptyRequests().isEmpty())
    {
    interfacesToEmpty.add(tile);
    }
  if(!tile.getFillRequests().isEmpty())
    {
    interfacesToFill.add(tile);
    }
  }

public final void onStorageFilterChanged(IWarehouseStorageTile tile, List<WarehouseStorageFilter> oldFilters, List<WarehouseStorageFilter> newFilters)
  {
  if(worldObj.isRemote){return;}
  storageMap.updateTileFilters(tile, oldFilters, newFilters);
  }

public final void addStockViewer(TileWarehouseStockViewer viewer)
  {
  if(worldObj.isRemote){return;}
  stockViewers.add(viewer);
  viewer.setController(this);
  viewer.onWarehouseInventoryUpdated();
  }

public final void removeStockViewer(TileWarehouseStockViewer tile)
  {
  stockViewers.remove(tile);
  }

@Override
public final void addControlledTile(IControlledTile tile)
  {
  if(tile instanceof IWarehouseStorageTile){addStorageTile((IWarehouseStorageTile) tile);}
  else if(tile instanceof TileWarehouseInterface){addInterfaceTile((TileWarehouseInterface) tile);}
  else if(tile instanceof TileWarehouseStockViewer){addStockViewer((TileWarehouseStockViewer) tile);}
  }

@Override
public final BlockPosition getPosition()
  {
  return new BlockPosition(xCoord, yCoord, zCoord);
  }

@Override
public final void removeControlledTile(IControlledTile tile)
  {  
  if(tile instanceof IWarehouseStorageTile){removeStorageTile((IWarehouseStorageTile) tile);}
  else if(tile instanceof TileWarehouseInterface){removeInterfaceTile((TileWarehouseInterface) tile);}
  else if(tile instanceof TileWarehouseStockViewer){removeStockViewer((TileWarehouseStockViewer) tile);}
  }

@Override
public final WorkType getWorkType()
  {
  return WorkType.CRAFTING;
  }

@Override
public final boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_CONTROL, xCoord, yCoord, zCoord);    
    }
  return true;
  }

@Override
public final boolean shouldRenderInPass(int pass)
  {
  return pass==1;//TODO can render in pass1?
  }

public int getCountOf(ItemStack layoutStack)
  {
  return cachedItemMap.getCount(layoutStack);
  }

public void decreaseCountOf(ItemStack layoutStack, int i)
  {
  List<IWarehouseStorageTile> dest = new ArrayList<IWarehouseStorageTile>();
  storageMap.getDestinations(layoutStack, dest);
  int found = 0;
  for(IWarehouseStorageTile tile : dest)
    {
    found =  tile.getQuantityStored(layoutStack);
    if(found>0)
      {
      if(found>i){found=i;}
      i-=found;
      tile.extractItem(layoutStack, found);
      cachedItemMap.decreaseCount(layoutStack, found);
      if(i<=0){break;}
      }
    }
  updateViewers();
  }

public ItemStack tryAdd(ItemStack stack)
  {
  List<IWarehouseStorageTile> destinations = new ArrayList<IWarehouseStorageTile>();
  storageMap.getDestinations(stack, destinations);
  int moved = 0;
  for(IWarehouseStorageTile tile : destinations)
    {
    moved = tile.insertItem(stack, stack.stackSize);
    stack.stackSize-=moved;  
    changeCachedQuantity(stack, moved);
    updateViewers();
    if(stack.stackSize<=0){break;}
    }
  if(stack.stackSize<=0)
    {
    return null;
    }
  return stack;
  }


}
