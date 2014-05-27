package net.shadowmage.ancientwarfare.npc.item;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;

public class ItemCommandBaton extends Item implements IItemKeyInterface, IItemClickable 
{

public ItemCommandBaton(String name)
  {
  this.setUnlocalizedName(name);
  this.setCreativeTab(AWNpcItemLoader.npcTab);
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack)
  {
  //return true for shouldSendPacket
  return true;
  }

@Override
public boolean onLeftClickClient(EntityPlayer player, ItemStack stack)
  {
  //noop
  return false;
  }

@Override
public void onLeftClick(EntityPlayer player, ItemStack stack)
  {
  //noop
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack)
  {
  // TODO Auto-generated method stub
  
  }

private class NpcCommandSet
{
List<UUID> commandedEntities;
}

}
