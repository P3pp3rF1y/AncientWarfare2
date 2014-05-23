package net.shadowmage.ancientwarfare.npc.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class NpcCourier extends NpcPlayerOwned
{

public NpcCourier(World par1World)
  {
  super(par1World);
  // TODO Auto-generated constructor stub
  }

@Override
public boolean isValidOrdersStack(ItemStack stack)
  {
  // TODO Auto-generated method stub
  return false;
  }


}
