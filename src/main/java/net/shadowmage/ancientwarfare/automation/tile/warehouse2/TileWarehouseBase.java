package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

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
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedTile;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueReceiver;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public abstract class TileWarehouseBase extends TileEntity implements IOwnable, IWorkSite, ITorqueReceiver, IBoundedTile, IInteractableTile, IControllerTile
{

BlockPosition min, max;
String ownerName;
double storedEnergy;

double maxEnergy = AWCoreStatics.energyPerWorkUnit*4;
double maxInput = AWCoreStatics.energyPerWorkUnit;

boolean init;
boolean shouldRecount;

Set<TileWarehouseInterface> interfaceTiles = new HashSet<TileWarehouseInterface>();
Set<IWarehouseStorageTile> storageTiles = new HashSet<IWarehouseStorageTile>();

/**
 * interfaces that need filling, AND there are items available to fill.  anytime storage block inventories are updated, this list needs to be
 * rechecked to make sure items are still available
 */
Set<TileWarehouseInterface> interfacesToFill = new HashSet<TileWarehouseInterface>();

/**
 * interfaces that have an excess of items or non-matching items will be in this set
 */
Set<TileWarehouseInterface> interfacesToEmpty = new HashSet<TileWarehouseInterface>();

WarehouseStorageMap storageMap = new WarehouseStorageMap();
ItemQuantityMap cachedItemMap = new ItemQuantityMap();

Set<ContainerWarehouseControl> viewers = new HashSet<ContainerWarehouseControl>();

public TileWarehouseBase()
  {
  
  }

protected abstract boolean tryFillInterfaces();

protected abstract boolean tryEmptyInterfaces();

public abstract ItemStack requestItem(ItemStack filter);

public abstract ItemStack mergeItem(ItemStack item);

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
  if(!init)
    {
    init=true;  
    scanForInitialTiles();
    }
  while(storedEnergy >= AWCoreStatics.energyPerWorkUnit && processWork())
    {
    storedEnergy -= AWCoreStatics.energyPerWorkUnit;
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

public final void addViewer(ContainerWarehouseControl viewer){viewers.add(viewer);}

public final void removeViewer(ContainerWarehouseControl viewer){viewers.remove(viewer);}

public final void updateViewers(){for(ContainerWarehouseControl viewer : viewers){viewer.onWarehouseInventoryUpdated();}}

public final void addStorageTile(IWarehouseStorageTile tile)
  {
  if(!storageTiles.contains(tile))
    {
    storageTiles.add(tile);    
    if(tile instanceof IControlledTile)
      {
      ((IControlledTile) tile).setController(this);
      }
    storageMap.addStorageTile(tile);
    tile.addItems(cachedItemMap);  
    onStorageInventoryUpdated(tile);
    }
  AWLog.logDebug("added storage tile, set now contains: "+storageTiles);
  }

public final void removeStorageTile(IWarehouseStorageTile tile)
  {
  storageTiles.remove(tile);
  storageMap.removeStorageTile(tile);
  }

public final void onStorageInventoryUpdated(IWarehouseStorageTile tile)
  {
  //TODO examine interface blocks to see if tile can fulfill any filters
  //if so, add interface block into toFill set for next update cycle/work cycle
  }

public final void addInterfaceTile(TileWarehouseInterface tile)
  {
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
  storageMap.updateTileFilters(tile, oldFilters, newFilters);
  }

@Override
public final void addControlledTile(IControlledTile tile)
  {
  if(tile instanceof IWarehouseStorageTile){addStorageTile((IWarehouseStorageTile) tile);}
  else if(tile instanceof TileWarehouseInterface){addInterfaceTile((TileWarehouseInterface) tile);}
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
  }

@Override
public final void setEnergy(double energy)
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
  if(storedEnergy>getMaxEnergy()){storedEnergy = getMaxEnergy();}
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
  if(storedEnergy>getMaxEnergy()){storedEnergy=getMaxEnergy();}
  }

@Override
public final double addEnergy(ForgeDirection from, double energy)
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
    if(storedEnergy>getMaxEnergy()){storedEnergy=getMaxEnergy();}
    return energy;    
    }
  return 0;
  }

@Override
public final double getMaxEnergy()
  {
  return maxEnergy;
  }

@Override
public final double getEnergyStored()
  {
  return storedEnergy;
  }

@Override
public final double getMaxInput()
  {
  return maxInput;
  }

@Override
public final boolean canInput(ForgeDirection from)
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
  AWLog.logDebug("set bounds to: "+min+" :: "+max);
  }

@Override
public final AxisAlignedBB getRenderBoundingBox()
  {
  AxisAlignedBB bb = super.getRenderBoundingBox();
  if(hasWorkBounds())
    {
    BlockPosition min = getWorkBoundsMin();
    BlockPosition max = getWorkBoundsMax();
    bb.minX = min.x < bb.minX ? min.x : bb.minX;
    bb.minY = min.y < bb.minY ? min.y : bb.minY;
    bb.minZ = min.z < bb.minZ ? min.z : bb.minZ;
    bb.maxX = max.x+1 > bb.maxX ? max.x+1 : bb.maxX;
    bb.maxY = max.y+1 > bb.maxY ? max.y+1 : bb.maxY;
    bb.maxZ = max.z+1 > bb.maxZ ? max.z+1 : bb.maxZ;
    }
  return bb;
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
  tag.setTag("min", min.writeToNBT(new NBTTagCompound()));
  tag.setTag("max", max.writeToNBT(new NBTTagCompound()));
  return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {
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
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  tag.setDouble("storedEnergy", storedEnergy);
  tag.setString("ownerName", ownerName);
  tag.setTag("min", min.writeToNBT(new NBTTagCompound()));
  tag.setTag("max", max.writeToNBT(new NBTTagCompound()));
  }

}
