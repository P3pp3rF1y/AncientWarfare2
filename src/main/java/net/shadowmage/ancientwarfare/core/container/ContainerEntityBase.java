package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Olivier on 05/02/2015.
 */
public class ContainerEntityBase<T extends Entity> extends ContainerBase{
    public final T entity;
    public ContainerEntityBase(EntityPlayer player, int id) {
        super(player);
        entity = (T) player.worldObj.getEntityByID(id);
        if(entity == null){
            throw new IllegalArgumentException("Id wasn't a valid entity");
        }
    }
}
