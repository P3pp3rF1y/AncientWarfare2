package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Tile Entities with right-click actions (usually opening a GUI)
 *
 * @author Shadowmage
 */
public interface IInteractableTile {

    public boolean onBlockClicked(EntityPlayer player);

}
