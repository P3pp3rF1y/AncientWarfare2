package net.shadowmage.ancientwarfare.core.owner;

import net.minecraft.entity.player.EntityPlayer;

/*
 * Tile entities/Entities that are owned by a player -- called by spawning/placing items to set owner
 *
 * @author Shadowmage
 */
public interface IOwnable {
	void setOwner(EntityPlayer player);

	void setOwner(Owner owner);

	Owner getOwner();

	boolean isOwner(EntityPlayer player);
}
