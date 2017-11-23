package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface IControlledTile {
    void setController(@Nullable IControllerTile tile);
    IControllerTile getController();
    BlockPos getPosition();
}
