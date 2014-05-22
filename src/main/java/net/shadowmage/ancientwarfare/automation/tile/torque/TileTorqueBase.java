package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.config.AWLog;

public abstract class TileTorqueBase extends TileEntity
{

protected TileEntity[] neighborTileCache = null;

public void onBlockUpdated()
  {
  AWLog.logDebug("torque tile update...");
  buildNeighborCache();
  }

@Override
public void validate()
  {  
  super.validate();
  neighborTileCache = null;
  }

@Override
public void invalidate()
  {  
  super.invalidate();
  neighborTileCache = null;
  }

public TileEntity[] getNeighbors()
  {
  if(neighborTileCache==null){buildNeighborCache();}
  return neighborTileCache;
  }

protected void buildNeighborCache()
  {
  this.neighborTileCache = new TileEntity[6];
  worldObj.theProfiler.startSection("AWPowerTileNeighborUpdate");
  ForgeDirection d;
  TileEntity te;
  for(int i = 0; i < 6; i++)
    {
    d = ForgeDirection.getOrientation(i);
    te = worldObj.getTileEntity(xCoord+d.offsetX, yCoord+d.offsetY, zCoord+d.offsetZ);
    this.neighborTileCache[i] = te;
    }
  worldObj.theProfiler.endSection();    
  }

}
