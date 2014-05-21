package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileTorqueBase extends TileEntity
{

protected TileEntity[] neighborTileCache = new TileEntity[6];

@Override
public void invalidate()
  {  
  super.invalidate();
  neighborTileCache = new TileEntity[6];
  }

/**
 * should be called from the containing block when it receives a 'onNeighbotUpdate' callback 
 */
public void onBlockUpdated()
  {
  ForgeDirection d;
  TileEntity te;
  for(int i = 0; i < 6; i++)
    {
    d = ForgeDirection.getOrientation(i);
    te = worldObj.getTileEntity(xCoord+d.offsetX, yCoord+d.offsetY, zCoord+d.offsetZ);
    this.neighborTileCache[i] = te;
    }
  }

}
