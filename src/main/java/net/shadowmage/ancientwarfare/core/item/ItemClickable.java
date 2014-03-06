package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;

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

@Override
public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
  {
  return true;
  }

@Override
public boolean onItemUse(ItemStack stack, EntityPlayer player, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
  {
  return true;
  }

@Override
public ItemStack onItemRightClick(ItemStack stack, World par2World, EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    onRightClick(stack, player, getMovingObjectPositionFromPlayer(player.worldObj, player, true));
    }
  return stack;
  }

@Override
public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
  {
  if(!player.worldObj.isRemote)
    {
    onRightClick(stack, player, getMovingObjectPositionFromPlayer(player.worldObj, player, true));    
    }
  return false;
  }

public void onLeftClick(ItemStack stack, EntityPlayer player, MovingObjectPosition hit)
  {
  AWLog.logDebug("item left click (event)... client: "+player.worldObj.isRemote);   
  }

public void onRightClick(ItemStack stack, EntityPlayer player, MovingObjectPosition hit)
  {
  AWLog.logDebug("item right click (event)... client: "+player.worldObj.isRemote);      
  }

@Override
public MovingObjectPosition getMovingObjectPositionFromPlayer( World par1World, EntityPlayer par2EntityPlayer, boolean par3)
  {
  return super.getMovingObjectPositionFromPlayer(par1World, par2EntityPlayer,par3);
  }
}
