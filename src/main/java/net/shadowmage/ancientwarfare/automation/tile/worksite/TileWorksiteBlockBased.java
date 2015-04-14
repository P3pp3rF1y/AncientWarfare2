package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.shadowmage.ancientwarfare.core.util.BlockPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class TileWorksiteBlockBased extends TileWorksiteBoundedInventory {

    private List<BlockPosition> blocksToUpdate = new ArrayList<BlockPosition>();

    protected abstract void fillBlocksToProcess(Collection<BlockPosition> addTo);

    protected abstract void scanBlockPosition(BlockPosition pos);

    protected abstract void updateBlockWorksite();

    @Override
    protected void updateWorksite() {
        worldObj.theProfiler.startSection("Incremental Scan");
        incrementalScan();
        worldObj.theProfiler.endSection();
        updateBlockWorksite();
    }

    protected void clearBlocksToUpdate() {
        blocksToUpdate.clear();
    }

    protected void incrementalScan() {
        if (blocksToUpdate.isEmpty()) {
            fillBlocksToProcess(blocksToUpdate);
        }
        if (!blocksToUpdate.isEmpty()) {
            int rand = worldObj.rand.nextInt(blocksToUpdate.size());
            BlockPosition pos = blocksToUpdate.remove(rand);
            scanBlockPosition(pos);
        }
    }


}
