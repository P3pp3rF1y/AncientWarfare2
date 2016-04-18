package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
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

    @Override
    public boolean canInteractWith(EntityPlayer var1){
        if(tileEntity instanceof IInventory && !((IInventory) tileEntity).isUseableByPlayer(var1))
            return false;
        return tileEntity.getDistanceFrom(var1.posX, var1.posY, var1.posZ) <= 64D;
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
