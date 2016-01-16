package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.shadowmage.ancientwarfare.core.util.BlockPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class TileWorksiteBlockBased extends TileWorksiteBoundedInventory {

    private final List<BlockPosition> blocksToUpdate = new ArrayList<BlockPosition>();

    protected abstract void fillBlocksToProcess(Collection<BlockPosition> addTo);

    protected abstract void scanBlockPosition(BlockPosition pos);

    protected abstract void updateBlockWorksite();

    @Override
    protected final void updateWorksite() {
        worldObj.theProfiler.startSection("Incremental Scan");
        if (blocksToUpdate.isEmpty() && hasWorkBounds()) {
            fillBlocksToProcess(blocksToUpdate);
        }
        if (!blocksToUpdate.isEmpty()) {
            int rand = worldObj.rand.nextInt(blocksToUpdate.size());
            BlockPosition pos = blocksToUpdate.remove(rand);
            scanBlockPosition(pos);
        }
        worldObj.theProfiler.endSection();
        updateBlockWorksite();
    }
}
