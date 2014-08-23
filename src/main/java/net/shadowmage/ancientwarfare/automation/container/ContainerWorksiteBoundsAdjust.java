package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class ContainerWorksiteBoundsAdjust extends ContainerBase
{

public int x, y, z;
public BlockPosition min, max;
public IWorkSite worksite;

public ContainerWorksiteBoundsAdjust(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  this.x = x;
  this.y = y;
  this.z = z;
  TileEntity te = player.worldObj.getTileEntity(x, y, z);
  worksite = (IWorkSite)te;
  min = worksite.getWorkBoundsMin().copy();
  max = worksite.getWorkBoundsMax().copy();  
  }

}
