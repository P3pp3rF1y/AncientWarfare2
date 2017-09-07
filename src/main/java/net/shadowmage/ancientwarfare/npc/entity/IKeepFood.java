package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.util.math.BlockPos;

/*
 * Created by Olivier on 09/07/2015.
 */
public interface IKeepFood {

    int getUpkeepAmount();

    int getUpkeepBlockSide();

    int getUpkeepDimensionId();

    void setUpkeepAutoPosition(BlockPos pos);

    BlockPos getUpkeepPoint();
}
