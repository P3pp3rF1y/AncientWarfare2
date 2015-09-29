package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Tile entities/Entities that are owned by a player -- called by spawning/placing items to set owner
 *
 * @author Shadowmage
 */
public interface IOwnable {

    public void setOwner(EntityPlayer player);

    public String getOwnerName();

    public boolean isOwner(EntityPlayer player);
}
