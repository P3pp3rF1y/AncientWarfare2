package net.shadowmage.ancientwarfare.npc.entity;

import net.shadowmage.ancientwarfare.core.util.BlockPosition;

/**
 * Created by Olivier on 09/07/2015.
 */
public interface IKeepFood {

    public int getUpkeepAmount();

    public int getUpkeepBlockSide();

    public int getUpkeepDimensionId();

    public void setUpkeepAutoPosition(BlockPosition pos);

    public BlockPosition getUpkeepPoint();
}
