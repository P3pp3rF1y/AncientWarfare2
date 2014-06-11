package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class TileWarehouseStockViewer extends TileControlled implements IOwnable, IInteractableTile
{


HashMap<WarehouseStorageFilter, Integer> filterQuantities = new HashMap<WarehouseStorageFilter, Integer>();
String ownerName;

public TileWarehouseStockViewer()
  {
  
  }

@Override
public void setOwnerName(String name)
  {
  ownerName = name;
  }

@Override
public String getOwnerName()
  {
  return ownerName;
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_STOCK, xCoord, yCoord, zCoord);
    }
  return false;
  }

@Override
protected void updateTile()
  {
  
  }

@Override
protected void searchForController()
  {
  
  }

@Override
protected boolean isValidController(IControllerTile tile)
  {
  
  return false;
  }

@Override
public boolean receiveClientEvent(int p_145842_1_, int p_145842_2_)
  {
  
  return super.receiveClientEvent(p_145842_1_, p_145842_2_);
  }

}
