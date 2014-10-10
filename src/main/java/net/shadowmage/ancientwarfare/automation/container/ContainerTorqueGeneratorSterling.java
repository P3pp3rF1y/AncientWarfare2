package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileSterlingEngine;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class ContainerTorqueGeneratorSterling extends ContainerBase
{

public int guiHeight;
public double energy;
public int burnTime;
public int burnTimeBase;

public TileSterlingEngine tile;

public ContainerTorqueGeneratorSterling(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  tile = (TileSterlingEngine)player.worldObj.getTileEntity(x,y,z);
  addSlotToContainer(new Slot(tile,0, 8+4*18, 8+12)
    {
    @Override
    public boolean isItemValid(ItemStack par1ItemStack)
      {
      AWLog.logDebug("checking if item is fuel: "+par1ItemStack+" val: "+TileEntityFurnace.isItemFuel(par1ItemStack));
      return TileEntityFurnace.isItemFuel(par1ItemStack);
      }
    });
  addPlayerSlots(player, 8, 8+18+8+12+12, 4);
  guiHeight = 8+18+8 + 4*18 + 4 + 8 + 12+12;
  }

@Override
public void updateProgressBar(int par1, int par2)
  {
  if(par1==0)
    {
    energy = (double)par2 / 100.d;
    refreshGui();
    }  
  if(par1==1)
    {
    burnTime = par2;
    refreshGui();
    }
  if(par1==2)
    {
    burnTimeBase = par2;
    refreshGui();
    }
  }

@Override
public void detectAndSendChanges()
  {  
  super.detectAndSendChanges();
  double g = tile.getTorqueStored(tile.getPrimaryFacing());
  if(g!=energy)
    {
    int e = (int)(g*100.d);
    for (int j = 0; j < this.crafters.size(); ++j)
      {
      ((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, 0, e);
      }
    }
  int b = tile.getBurnTime();
  if(b!=burnTime)
    {
    for (int j = 0; j < this.crafters.size(); ++j)
      {
      ((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, 1, b);
      }
    }
  b = tile.getBurnTimeBase();
  if(b!=burnTimeBase)
    {
    for (int j = 0; j < this.crafters.size(); ++j)
      {
      ((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, 2, b);
      }
    }
  }

/**
 * @return should always return null for normal implementation, not sure wtf the rest of the code is about
 */
@Override
public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex)
  {
  int slots = 1;
  Slot slot = (Slot)this.inventorySlots.get(slotClickedIndex);
  if(slot==null || !slot.getHasStack()){return null;}
  ItemStack stackFromSlot = slot.getStack();
  if(slotClickedIndex == 0)//click on input slot, merge into player inventory
    {
    this.mergeItemStack(stackFromSlot, slots, slots+36, false);
    }
  else//click on player slot, attempt merge into te inventory
    {
    this.mergeItemStack(stackFromSlot, 0, 1, false);
    }
  if(stackFromSlot.stackSize == 0)
    {
    slot.putStack((ItemStack)null);
    }
  else
    {
    slot.onSlotChanged();
    }
  return null;  
  }

}
