package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.shadowmage.ancientwarfare.core.util.BlockPosition;


public abstract class TileWorksiteBlockBased extends TileWorksiteBounded
{

private List<BlockPosition> blocksToUpdate = new ArrayList<BlockPosition>();

protected abstract void fillBlocksToProcess(Collection<BlockPosition> addTo);

protected abstract void scanBlockPosition(BlockPosition pos);

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(worldObj.isRemote){return;}
  worldObj.theProfiler.startSection("AWWorksite");
  worldObj.theProfiler.startSection("Incremental Scan");
  incrementalScan();
  worldObj.theProfiler.endSection();
  worldObj.theProfiler.endSection();
  }

protected void incrementalScan()
  {
  if(blocksToUpdate.isEmpty())
    {
    fillBlocksToProcess(blocksToUpdate);
    }
  if(!blocksToUpdate.isEmpty())
    {
    int rand = worldObj.rand.nextInt(blocksToUpdate.size());
    BlockPosition pos = blocksToUpdate.remove(rand);
    scanBlockPosition(pos);
    }
  }


}
