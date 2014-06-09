package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public interface IControllerTile
{

public void addControlledTile(IControlledTile tile);
public void removeControlledTile(IControlledTile tile);
public BlockPosition getPosition();

}
