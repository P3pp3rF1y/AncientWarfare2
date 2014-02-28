package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemClickable extends ItemBase
{

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

@SubscribeEvent
public void onItemUse(PlayerInteractEvent evt)
  {
  if(evt.action==Action.LEFT_CLICK_BLOCK)
    {
    EntityPlayer player = evt.entityPlayer;
    ItemStack stack = evt.entityPlayer.inventory.getCurrentItem();
    
    if(stack==null || !(stack.getItem() instanceof ItemClickable))
      {
      return;
      }
    ItemClickable item = (ItemClickable) stack.getItem();
    item.onLeftClick(stack, player, getMovingObjectPositionFromPlayer(player.worldObj, player, true));
    evt.setCanceled(true);
    }
  }
}
