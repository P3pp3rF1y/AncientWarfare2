package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.npc.orders.NpcOrders;

public abstract class ItemOrders extends Item implements IItemClickable, IItemKeyInterface
{

public ItemOrders(String name)
  {
  this.setUnlocalizedName(name);
  this.setCreativeTab(AWNpcItemLoader.npcTab);
  }

@Override
public boolean onLeftClickClient(EntityPlayer player, ItemStack stack)
  {  
  return false;
  }

@Override
public void onLeftClick(EntityPlayer player, ItemStack stack)
  {
   
  }

@Override
public boolean onKeyActionClient(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

}
