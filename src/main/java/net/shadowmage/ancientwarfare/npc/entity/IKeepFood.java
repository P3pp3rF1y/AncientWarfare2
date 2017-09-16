package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/*
 * Created by Olivier on 09/07/2015.
 */
public interface IKeepFood {

    int getUpkeepAmount();

    EnumFacing getUpkeepBlockSide();

    int getUpkeepDimensionId();

    void setUpkeepAutoPosition(BlockPos pos);

    BlockPos getUpkeepPoint();
}
