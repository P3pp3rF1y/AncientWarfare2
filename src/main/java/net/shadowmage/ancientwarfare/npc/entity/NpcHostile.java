package net.shadowmage.ancientwarfare.npc.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class NpcHostile extends NpcBase
{

public NpcHostile(World par1World)
  {
  super(par1World);
  }

@Override
public void onOrdersInventoryChanged()
  {
  //noop
  }

@Override
public boolean isValidOrdersStack(ItemStack stack)
  {
  //noop on hostile
  return false;
  }

@Override
public void onWeaponInventoryChanged()
  {
  //noop for hostile
  }

@Override
public String getNpcSubType()
  {
  //TODO lookup type based on item equipped in main slot and 'faction'
  return null;
  }

@Override
public String getNpcType()
  {
  return "hostile";
  }

@Override
public void readAdditionalItemData(NBTTagCompound tag)
  {
  
  }

@Override
public void writeAdditionalItemData(NBTTagCompound tag)
  {
  
  }

}
