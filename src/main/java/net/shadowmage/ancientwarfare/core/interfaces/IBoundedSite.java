package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.util.math.BlockPos;

public interface IBoundedSite {

	BlockPos getWorkBoundsMin();

	BlockPos getWorkBoundsMax();

	boolean userAdjustableBlocks();

	boolean hasWorkBounds();

	int getBoundsMaxWidth();

	int getBoundsMaxHeight();

	void setBounds(BlockPos p1, BlockPos p2);

	void setWorkBoundsMax(BlockPos max);

	void setWorkBoundsMin(BlockPos min);

	/*
	 * Called from container when a user adjusts work bounds for a block.
	 * Tile should take the opportunity to revalidate the selection and/or offset bounds
	 * for tile special placement/offset/whatever
	 */
	void onBoundsAdjusted();

	/*
	 * Called from container AFTER bounds have been adjusted.  Tile should take this opportunity
	 * to reseat any chunkloading or re-init any scan stuff
	 */
	void onPostBoundsAdjusted();
}
