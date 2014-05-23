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
public boolean isValidOrdersStack(ItemStack stack)
  {
  //noop on hostile
  return false;
  }

@Override
public void readAdditionalItemData(NBTTagCompound tag)
  {
  
  }

@Override
public void writeAddtionalItemData(NBTTagCompound tag)
  {
  
  }

@Override
public void writeSpawnData(ByteBuf buffer)
  {
  
  }

@Override
public void readSpawnData(ByteBuf additionalData)
  {
  
  }

}
