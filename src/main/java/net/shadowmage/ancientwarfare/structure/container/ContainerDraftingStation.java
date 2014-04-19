package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.structure.tile.TileDraftingStation;

public class ContainerDraftingStation extends ContainerBase
{

private TileDraftingStation tile;

public ContainerDraftingStation(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  tile = (TileDraftingStation) player.worldObj.getTileEntity(x, y, z);
  }


}
