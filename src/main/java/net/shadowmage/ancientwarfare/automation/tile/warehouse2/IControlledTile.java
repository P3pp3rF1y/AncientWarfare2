package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public interface IControlledTile {

    public void setController(IControllerTile tile);

    public IControllerTile getController();

    public BlockPosition getPosition();

}
