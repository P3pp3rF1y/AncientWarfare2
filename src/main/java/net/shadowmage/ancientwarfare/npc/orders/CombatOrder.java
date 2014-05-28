package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;

public class CombatOrder extends NpcOrders
{

public CombatOrder()
  {
  // TODO Auto-generated constructor stub
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {

  }

@Override
public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  return tag;
  }

public static UpkeepOrder getUpkeepOrder(ItemStack stack)
  {
//  if(stack!=null && stack.getItem()==AWNpcItemLoader.upkeepOrder)
//    {
//    UpkeepOrder order = new UpkeepOrder();
//    if(stack.hasTagCompound() && stack.getTagCompound().hasKey("orders"))
//      {
//      order.readFromNBT(stack.getTagCompound().getCompoundTag("orders"));
//      }
//    return order;
//    }
  //TODO
  return null;
  }

public static void writeUpkeepOrder(ItemStack stack, UpkeepOrder order)
  {
//  if(stack!=null && stack.getItem()==AWNpcItemLoader.upkeepOrder)
//    {
//    stack.setTagInfo("orders", order.writeToNBT(new NBTTagCompound()));
//    }
  //TODO
  }

}
