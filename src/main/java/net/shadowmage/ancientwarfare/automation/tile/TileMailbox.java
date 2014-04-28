package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.inventory.ISidedTile;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SlotItemFilter;

public class TileMailbox extends TileEntity implements ISidedTile
{

public boolean autoExport;//should automatically try and export from output side

private InventorySided inventory;

private String owningPlayerName;
private String mailboxName;
private String destinationName;

public TileMailbox()
  {
  inventory = new InventorySided(36, this);
  
  this.inventory.addSlotViewMap(InventorySide.TOP, 8, 8, "guistrings.inventory.side.top");
  for(int i =0; i <18; i++)
    {
    this.inventory.addSidedMapping(InventorySide.TOP, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.TOP, i, (i%9)*18, (i/9)*18);
    }
    
  this.inventory.addSlotViewMap(InventorySide.BOTTOM, 8, (3*18)+12+8, "guistrings.inventory.side.bottom");
  for(int i = 18, k = 0; i<36; i++, k++)
    {
    this.inventory.addSidedMapping(InventorySide.BOTTOM, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.BOTTOM, i, (k%9)*18, (k/9)*18);
    }
  }

public void checkOutputDirection()
  {
  int check = RelativeSide.getAccessDirection(RelativeSide.REAR, getTileMeta());
  AWLog.logDebug("checking output direction for rear: "+check + " :: "+ForgeDirection.getOrientation(check));
  }

@Override
public int getTileMeta()
  {
  return worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
  }

@Override
public void onInventoryChanged()
  {
  
  }

public void setOwningPlayer(String commandSenderName)
  {
  this.owningPlayerName = commandSenderName;
  }

}
