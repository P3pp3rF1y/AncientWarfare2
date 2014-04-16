package net.shadowmage.ancientwarfare.core.tile;

import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;

public class TileResearchStation extends TileEntity
{

public InventoryBasic bookInventory = new InventoryBasic(1);

public TileResearchStation()
  {
  // TODO Auto-generated constructor stub
  }

@Override
public boolean canUpdate()
  {
  return true;
  }

public String getCrafterName()
  {
  return ItemResearchBook.getResearcherName(bookInventory.getStackInSlot(0));
  }

}
