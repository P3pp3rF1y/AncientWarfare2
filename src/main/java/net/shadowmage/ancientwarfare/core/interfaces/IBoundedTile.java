package net.shadowmage.ancientwarfare.core.interfaces;

import net.shadowmage.ancientwarfare.core.util.BlockPosition;

/**
 * Tile entities that have a min/max bounds (for work/whatever)
 * @author Shadowmage
 */
public interface IBoundedTile
{

public void setBounds(BlockPosition p1, BlockPosition p2);

}
