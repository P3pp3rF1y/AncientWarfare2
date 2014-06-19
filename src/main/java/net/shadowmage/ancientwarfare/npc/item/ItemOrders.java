package net.shadowmage.ancientwarfare.npc.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;

public abstract class ItemOrders extends Item implements IItemClickable, IItemKeyInterface
{

public ItemOrders(String name)
  {
  this.setUnlocalizedName(name);
  this.setCreativeTab(AWNpcItemLoader.npcTab);
  }

@Override
public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List list, boolean par4)
  {
  list.add(StatCollector.translateToLocal("guistrings.npc.orders.open_gui"));
  String key = InputHandler.instance().getKeybindBinding(InputHandler.KEY_ALT_ITEM_USE_0);
  list.add(StatCollector.translateToLocalFormatted("guistrings.npc.orders.add_position", key));
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
public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemKey key)
  {
  return key==ItemKey.KEY_0;
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public boolean cancelRightClick(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public boolean cancelLeftClick(EntityPlayer player, ItemStack stack)
  {
  return false;
  }

}
