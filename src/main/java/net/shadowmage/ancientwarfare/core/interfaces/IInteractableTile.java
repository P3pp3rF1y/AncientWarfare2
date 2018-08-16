package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;

public interface IInteractableTile {
	boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand);
}
