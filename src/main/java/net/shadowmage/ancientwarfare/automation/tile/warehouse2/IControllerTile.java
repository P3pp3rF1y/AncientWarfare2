package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.util.math.BlockPos;

public interface IControllerTile {

    public void addControlledTile(IControlledTile tile);

    public void removeControlledTile(IControlledTile tile);

    public BlockPos getPos();

}
