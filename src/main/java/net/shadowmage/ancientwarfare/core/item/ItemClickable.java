package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemClickable extends ItemBase
{

protected boolean hasLeftClick;

public ItemClickable(String localizationKey)
  {
  super(localizationKey);
  this.setCreativeTab(CreativeTabs.tabMisc);  
  }

@Override
public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
  {
  return false;
  }

public void onLeftClick(ItemStack stack, EntityPlayer player, MovingObjectPosition hit)
  {
  
  }

public void onRightClick(ItemStack stack, EntityPlayer player, MovingObjectPosition hit)
  {
  
  }

@Override
public MovingObjectPosition getMovingObjectPositionFromPlayer( World par1World, EntityPlayer par2EntityPlayer, boolean par3)
  {
  return super.getMovingObjectPositionFromPlayer(par1World, par2EntityPlayer,par3);
  }
}
