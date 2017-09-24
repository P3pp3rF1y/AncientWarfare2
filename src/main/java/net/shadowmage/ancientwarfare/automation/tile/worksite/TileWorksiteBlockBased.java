package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class TileWorksiteBlockBased extends TileWorksiteBoundedInventory {

    private final List<BlockPos> blocksToUpdate = new ArrayList<>();

    protected abstract void fillBlocksToProcess(Collection<BlockPos> addTo);

    protected abstract void scanBlockPosition(BlockPos pos);

    protected abstract void updateBlockWorksite();

    @Override
    protected final void updateWorksite() {
        world.profiler.startSection("Incremental Scan");
        if (blocksToUpdate.isEmpty() && hasWorkBounds()) {
            fillBlocksToProcess(blocksToUpdate);
        }
        if (!blocksToUpdate.isEmpty()) {
            int rand = world.rand.nextInt(blocksToUpdate.size());
            BlockPos pos = blocksToUpdate.remove(rand);
            scanBlockPosition(pos);
        }
        world.profiler.endSection();
        updateBlockWorksite();
    }
}
