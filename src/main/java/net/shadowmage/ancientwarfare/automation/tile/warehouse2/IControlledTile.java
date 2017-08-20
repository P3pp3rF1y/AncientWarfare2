package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.util.math.BlockPos;

public interface IControlledTile {

    public void setController(IControllerTile tile);

    public IControllerTile getController();

    public BlockPos getPos();

}
