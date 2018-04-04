package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.UUID;

/*
 * Tile entities/Entities that are owned by a player -- called by spawning/placing items to set owner
 *
 * @author Shadowmage
 */
public interface IOwnable {
	//TODO look into removing ownername from this if possible - uuid should be enough
	void setOwner(EntityPlayer player);

	void setOwner(String ownerName, UUID ownerUuid);

	@Nullable
	String getOwnerName();

	@Nullable
	UUID getOwnerUuid();

	boolean isOwner(EntityPlayer player);
}
