package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;

/**
 * Tile Entities with right-click actions (usually opening a GUI)
 *
 * @author Shadowmage
 */
public interface IInteractableTile {

    public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand);

}
