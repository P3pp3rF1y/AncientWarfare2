package net.shadowmage.ancientwarfare.structure.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SlotItemFilter;
import net.shadowmage.ancientwarfare.core.inventory.SlotFiltered;
import net.shadowmage.ancientwarfare.structure.tile.TileDraftingStation;

public class ContainerDraftingStation extends ContainerStructureSelectionBase
{

public boolean isStarted = false;
public boolean isFinished =false;
public int remainingTime;
public String structureName;//selection name
public List<ItemStack> neededResources = new ArrayList<ItemStack>();

private TileDraftingStation tile;

public ContainerDraftingStation(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  tile = (TileDraftingStation) player.worldObj.getTileEntity(x, y, z);
  
  structureName = tile.getCurrentTemplateName();
  neededResources.addAll(tile.getNeededResources());
  isStarted = tile.isStarted();
  isFinished = tile.isFinished();
  remainingTime = tile.getRemainingTime();
  
  int y2 = 94;
  
  int xp;
  int yp;
  int slotNum;
  for(int y1 = 0; y1 <3; y1++)
    {
    for(int x1 = 0; x1<9; x1++)
      {
      slotNum = y1*9 + x1;
      xp = 8 + x1*18;
      yp = y2 + y1*18;
      addSlotToContainer(new Slot(tile.inputSlots, slotNum, xp, yp));
      }
    }
  
  SlotItemFilter filter = new SlotItemFilter()
    {
    @Override
    public boolean isItemValid(ItemStack stack)
      {
      return false;
      }
    };

  addSlotToContainer(new SlotFiltered(tile.outputSlot, 0, 8+4*18, 94-16-18, filter));
  
  this.addPlayerSlots(player, 8, 156, 4);
  }

@Override
public void sendInitData()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setBoolean("isStarted", isStarted);
  tag.setBoolean("isFinished", isFinished);
  tag.setInteger("remainingTime", remainingTime);
  if(structureName!=null)
    {
    tag.setString("structureName", structureName);    
    }
  /**
   * TODO send initial resource list
   */
  this.sendDataToClient(tag);
  }

@Override
public void handleNameSelection(String name)
  {  
  super.handleNameSelection(name);  
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("structName")){}//TODO handle template selection, pass name to server-side tile
  if(tag.hasKey("isStarted")){isStarted = tag.getBoolean("isStarted");}
  if(tag.hasKey("isFinished")){isFinished = tag.getBoolean("isFinished");}
  if(tag.hasKey("remainingTime")){remainingTime = tag.getInteger("remainingTime");}
  if(tag.hasKey("neededResources")){}//TODO
  if(tag.hasKey("structureName")){structureName = tag.getString("structureName");}
  }

@Override
public void detectAndSendChanges()
  {
  super.detectAndSendChanges();
  if(tile.isFinished()!=isFinished)
    {
    
    }
  if(tile.isStarted()!=isStarted)
    {
    
    }    
  }

}
