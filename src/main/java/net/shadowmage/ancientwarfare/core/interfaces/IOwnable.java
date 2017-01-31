package net.shadowmage.ancientwarfare.core.interfaces;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Tile entities/Entities that are owned by a player -- called by spawning/placing items to set owner
 *
 * @author Shadowmage
 */
public interface IOwnable {

    public void setOwner(EntityPlayer player);
    
    public void setOwner(String ownerName, UUID ownerUuid);

    public String getOwnerName();
    
    public UUID getOwnerUuid();

    public boolean isOwner(EntityPlayer player);
}
