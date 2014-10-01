package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseControl;
import net.shadowmage.ancientwarfare.automation.item.ItemWorksiteUpgrade;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseInterface.InterfaceEmptyRequest;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseInterface.InterfaceFillRequest;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public abstract class TileWarehouseBase extends TileEntity implements IOwnable, IWorkSite, ITorqueTile, IInteractableTile, IControllerTile
{

private BlockPosition min, max;
private String ownerName;
private double storedEnergy;

private double maxEnergy = AWCoreStatics.energyPerWorkUnit*4;
private double maxInput = AWCoreStatics.energyPerWorkUnit;

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

private EnumSet<WorksiteUpgrade> upgrades = EnumSet.noneOf(WorksiteUpgrade.class);
private double efficiency;

public TileWarehouseBase()
  {
  
  }

@Override
public boolean cascadedInput()
  {
  return false;
  }

@Override
public ITorqueTile[] getNeighborTorqueTiles()
  {
  return null;
  }

@Override
public double getClientOutputRotation()
  {
  return 0;
  }

@Override
public double getPrevClientOutputRotation()
  {
  return 0;
  }

@Override
public boolean useClientRotation()
  {
  return false;
  }

@Override
public void onBoundsAdjusted()
  {
  this.max.y = min.y + 3;
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
public EnumSet<WorksiteUpgrade> getUpgrades(){return upgrades;}

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

@Override
public void onBlockBroken()
  {
  for(WorksiteUpgrade ug : this.upgrades)
    {
    InventoryTools.dropItemInWorld(worldObj, ItemWorksiteUpgrade.getStack(ug), xCoord, yCoord, zCoord);
    }
  efficiency = 0;
  upgrades.clear();
  }

@Override
public void addUpgrade(WorksiteUpgrade upgrade)
  {
  upgrades.add(upgrade);
  updateEfficiency();
  worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  }

@Override
public void removeUpgrade(WorksiteUpgrade upgrade)
  {
  upgrades.remove(upgrade);
  updateEfficiency();
  worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  }

protected void updateEfficiency()
  {
  efficiency = 0.d;
  if(upgrades.contains(WorksiteUpgrade.ENCHANTED_TOOLS_1)){efficiency+=0.05;}
  if(upgrades.contains(WorksiteUpgrade.ENCHANTED_TOOLS_2)){efficiency+=0.1;}
  if(upgrades.contains(WorksiteUpgrade.TOOL_QUALITY_1)){efficiency+=0.05;}
  if(upgrades.contains(WorksiteUpgrade.TOOL_QUALITY_2)){efficiency+=0.15;}
  if(upgrades.contains(WorksiteUpgrade.TOOL_QUALITY_3)){efficiency+=0.25;}
  }

protected double getEnergyPerUse()
  {
  return AWCoreStatics.energyPerWorkUnit * (1.f - efficiency);
  }

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
public final boolean canUpdate()
  {
  return true;
  }

@Override
public final void updateEntity()
  { 
  if(worldObj.isRemote){return;}
  if(!init)
    {
    init=true;  
    scanForInitialTiles();
    }
  while(storedEnergy >= getEnergyPerUse() && processWork())
    {
    storedEnergy -= getEnergyPerUse();
    }
  if(shouldRecount)
    {
    shouldRecount=false;
    recountInventory();
    }
  }

private boolean processWork()
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

private void scanForInitialTiles()
  {
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
  AWLog.logDebug("added storage tile, set now contains: "+storageTiles);
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
  AWLog.logDebug("added interface tile, set now contains: "+interfaceTiles);
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
  AWLog.logDebug("receiving interface inventory changed update for: "+tile);
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
  AWLog.logDebug("added stock viewer tile, set now contains: "+stockViewers);
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
public final void setTorqueEnergy(double energy)
  {
  this.storedEnergy = energy;
  if(this.storedEnergy>this.maxEnergy)
    {
    this.storedEnergy = this.maxEnergy;
    }
  }

@Override
public final void addEnergyFromWorker(IWorker worker)
  {
  storedEnergy += AWCoreStatics.energyPerWorkUnit * worker.getWorkEffectiveness(getWorkType());
  if(storedEnergy>getMaxTorque()){storedEnergy = getMaxTorque();}
  }

@Override
public final void setOwnerName(String name)
  {
  if(name==null){name="";}
  this.ownerName = name;  
  }

@Override
public final void addEnergyFromPlayer(EntityPlayer player)
  {
  storedEnergy+=AWCoreStatics.energyPerWorkUnit;
  if(storedEnergy>getMaxTorque()){storedEnergy=getMaxTorque();}
  }

@Override
public final double addTorque(ForgeDirection from, double energy)
  {
  if(canInputTorque(from))
    {
    if(energy+getTorqueStored()>getMaxTorque())
      {
      energy = getMaxTorque()-getTorqueStored();
      }
    if(energy>getMaxTorqueInput())
      {
      energy = getMaxTorqueInput();
      }
    storedEnergy+=energy;
    if(storedEnergy>getMaxTorque()){storedEnergy=getMaxTorque();}
    return energy;    
    }
  return 0;
  }

@Override
public double getTorqueTransferLossPercent()
  {
  return 1;
  }

@Override
public final double getMaxTorque()
  {
  return maxEnergy;
  }

@Override
public final double getTorqueStored()
  {
  return storedEnergy;
  }

@Override
public final double getMaxTorqueInput()
  {
  return maxInput;
  }

@Override
public final boolean canInputTorque(ForgeDirection from)
  {
  return true;
  }

@Override
public final boolean hasWork()
  {
  return storedEnergy<maxEnergy;
  }

@Override
public final WorkType getWorkType()
  {
  return WorkType.CRAFTING;
  }

@Override
public final Team getTeam()
  {  
  return worldObj.getScoreboard().getPlayersTeam(ownerName);
  }

@Override
public final BlockPosition getWorkBoundsMin()
  {
  return min;
  }

@Override
public final BlockPosition getWorkBoundsMax()
  {
  return max;
  }

@Override
public final boolean hasWorkBounds()
  {
  return true;
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
public final String getOwnerName()
  {
  return ownerName;
  }

@Override
public final void setBounds(BlockPosition p1, BlockPosition p2)
  {
  min = BlockTools.getMin(p1, p2);
  max = BlockTools.getMax(p1, p2);
  max.y = min.y+3;
  }

@Override
public final AxisAlignedBB getRenderBoundingBox()
  {
  if(hasWorkBounds() && getWorkBoundsMin()!=null && getWorkBoundsMax()!=null)
    {
    AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+1, zCoord+1);
    BlockPosition min = getWorkBoundsMin();
    BlockPosition max = getWorkBoundsMax();
    bb.minX = min.x < bb.minX ? min.x : bb.minX;
    bb.minY = min.y < bb.minY ? min.y : bb.minY;
    bb.minZ = min.z < bb.minZ ? min.z : bb.minZ;
    bb.maxX = max.x+1 > bb.maxX ? max.x+1 : bb.maxX;
    bb.maxY = max.y+1 > bb.maxY ? max.y+1 : bb.maxY;
    bb.maxZ = max.z+1 > bb.maxZ ? max.z+1 : bb.maxZ;
    return bb;
    }
  return super.getRenderBoundingBox();
  }

@Override
public final boolean shouldRenderInPass(int pass)
  {
  return pass==1;
  }

@Override
public Packet getDescriptionPacket()
  {
  NBTTagCompound tag = new NBTTagCompound();
  int[] ugs = new int[upgrades.size()];
  int i = 0;
  for(WorksiteUpgrade ug : upgrades)
    {
    ugs[i] = ug.ordinal();
    i++;
    }
  tag.setIntArray("upgrades", ugs);
  tag.setTag("min", min.writeToNBT(new NBTTagCompound()));
  tag.setTag("max", max.writeToNBT(new NBTTagCompound()));
  return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, tag);
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {
  super.onDataPacket(net, pkt);
  if(pkt.func_148857_g().hasKey("upgrades"))
    {
    int[] ugs = pkt.func_148857_g().getIntArray("upgrades");
    upgrades.clear();
    for(int i = 0; i < ugs.length; i++)
      {
      upgrades.add(WorksiteUpgrade.values()[ugs[i]]);
      }
    }
  min = new BlockPosition(pkt.func_148857_g().getCompoundTag("min"));
  max = new BlockPosition(pkt.func_148857_g().getCompoundTag("max"));
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  storedEnergy=tag.getDouble("storedEnergy");
  ownerName = tag.getString("ownerName");
  min = new BlockPosition(tag.getCompoundTag("min"));
  max = new BlockPosition(tag.getCompoundTag("max"));
  int[] ug = tag.getIntArray("upgrades");
  for(int i= 0; i < ug.length; i++)
    {
    upgrades.add(WorksiteUpgrade.values()[i]);
    }
  updateEfficiency();
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  tag.setDouble("storedEnergy", storedEnergy);
  tag.setString("ownerName", ownerName);
  tag.setTag("min", min.writeToNBT(new NBTTagCompound()));
  tag.setTag("max", max.writeToNBT(new NBTTagCompound()));
  int[] ug = new int[getUpgrades().size()];
  int i = 0;
  for(WorksiteUpgrade u : getUpgrades())
    {
    ug[i] = u.ordinal();
    i++;
    }
  tag.setIntArray("upgrades", ug);
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

@Override
public void setWorkBoundsMax(BlockPosition max)
  {
  this.max = max;
  }

@Override
public void setWorkBoundsMin(BlockPosition min)
  {
  this.min = min;
  }


}
