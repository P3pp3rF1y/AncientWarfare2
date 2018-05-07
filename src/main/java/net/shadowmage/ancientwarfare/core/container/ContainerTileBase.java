package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

/*
 * Created by Olivier on 05/02/2015.
 */
public class ContainerTileBase<T extends TileEntity> extends ContainerBase {
	public final T tileEntity;

	public ContainerTileBase(EntityPlayer player, int x, int y, int z) {
		super(player);
		tileEntity = (T) player.world.getTileEntity(new BlockPos(x, y, z));
		if (tileEntity == null) {
			throw new IllegalArgumentException("Tile is null");
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.getDistanceSq(player.posX, player.posY, player.posZ) <= 64D;
	}

	@Override
	public final boolean equals(Object o) {
		return this == o || o instanceof ContainerTileBase && tileEntity.equals(((ContainerTileBase<?>) o).tileEntity);
	}

	@Override
	public final int hashCode() {
		return tileEntity.hashCode();
	}
}
