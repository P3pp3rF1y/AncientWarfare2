package net.shadowmage.ancientwarfare.core.interfaces;

import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public interface IBoundedSite {

    public BlockPosition getWorkBoundsMin();

    public BlockPosition getWorkBoundsMax();

    public boolean userAdjustableBlocks();

    public boolean hasWorkBounds();

    public int getBoundsMaxWidth();

    public int getBoundsMaxHeight();

    public void setBounds(BlockPosition p1, BlockPosition p2);

    public void setWorkBoundsMax(BlockPosition max);

    public void setWorkBoundsMin(BlockPosition min);

    /**
     * Called from container when a user adjusts work bounds for a block.
     * Tile should take the opportunity to revalidate the selection and/or offset bounds
     * for tile special placement/offset/whatever
     */
    public void onBoundsAdjusted();

    /**
     * Called from container AFTER bounds have been adjusted.  Tile should take this opportunity
     * to reseat any chunkloading or re-init any scan stuff
     */
    public void onPostBoundsAdjusted();
}
