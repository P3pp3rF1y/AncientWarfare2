package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by Olivier on 05/02/2015.
 */
public class ContainerTileBase<T extends TileEntity> extends ContainerBase {
    public final T tileEntity;

    public ContainerTileBase(EntityPlayer player, int x, int y, int z) {
        super(player);
        tileEntity = (T) player.worldObj.getTileEntity(x, y, z);
        if (tileEntity == null) {
            throw new IllegalArgumentException("Tile is null");
        }
    }
}
