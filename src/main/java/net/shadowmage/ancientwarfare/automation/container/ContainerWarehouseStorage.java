package net.shadowmage.ancientwarfare.automation.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseStorageBase;
import net.shadowmage.ancientwarfare.automation.tile.WarehouseItemFilter;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.StringTools;

public class ContainerWarehouseStorage extends ContainerBase
{

public List<WarehouseItemFilter> itemFilters = new ArrayList<WarehouseItemFilter>();
public int guiHeight;
public TileWarehouseStorageBase storageTile;
public String tileName = "";
private String tileNameOrig;

public ContainerWarehouseStorage(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  storageTile = (TileWarehouseStorageBase) player.worldObj.getTileEntity(x, y, z);
  tileName = storageTile.getInventoryName();  
  tileNameOrig = tileName;
  itemFilters.addAll(storageTile.getFilters());
  int x1, y1;
  for(int i = 0; i < storageTile.getSizeInventory(); i++)
    {
    x1 = (i%9) * 18 + 8;
    y1 = (i/9) * 18 + 20;
    addSlotToContainer(new SlotFiltered(storageTile, i, x1, y1));
    }
  
  guiHeight = 8 + 12 + 18*(storageTile.getSizeInventory()/9)+8;
  guiHeight = addPlayerSlots(player, 8, guiHeight, 4) - 16;    
  }

@Override
public void sendInitData()
  {
  
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("setName")){storageTile.setInventoryName(tag.getString("setName"));}  
  refreshGui();
  }

public void sendDataToServer()
  {
  if(!StringTools.doStringsMatch(tileNameOrig, tileName))
    {
    NBTTagCompound tag = new NBTTagCompound();
    tag.setString("setName", tileName);
    sendDataToServer(tag);
    }
  }

private final class SlotFiltered extends Slot
{

public SlotFiltered(IInventory par1iInventory, int par2, int par3, int par4)
  {
  super(par1iInventory, par2, par3, par4);
  }

@Override
public boolean isItemValid(ItemStack par1ItemStack)
  {
  if(itemFilters.isEmpty()){return true;}
  for(WarehouseItemFilter filter : itemFilters)
    {
    if(filter.isItemValid(par1ItemStack))
      {
      AWLog.logDebug("item validated: "+par1ItemStack+ " :: "+filter);
      return true;
      }
    }
  return false;
  }

}

}
