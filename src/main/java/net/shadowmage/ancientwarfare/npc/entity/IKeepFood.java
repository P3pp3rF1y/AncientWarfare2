package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.util.math.BlockPos;

/*
 * Created by Olivier on 09/07/2015.
 */
public interface IKeepFood {

    public int getUpkeepAmount();

    public int getUpkeepBlockSide();

    public int getUpkeepDimensionId();

    public void setUpkeepAutoPosition(BlockPos pos);

    public BlockPos getUpkeepPoint();
}
