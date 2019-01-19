package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Optional;

public interface IControlledTile {
	void setController(@Nullable TileWarehouseBase tile);

	Optional<TileWarehouseBase> getController();

	BlockPos getPosition();

	boolean isValidController(IControllerTile tile);
}
