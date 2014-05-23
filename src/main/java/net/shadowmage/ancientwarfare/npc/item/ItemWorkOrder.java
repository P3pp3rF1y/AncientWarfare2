package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.orders.NpcOrders;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder;


public class ItemWorkOrder extends ItemOrders
{

public ItemWorkOrder(String name)
  {
  super(name);
  }

@Override
public WorkOrder getOrders(ItemStack stack)
  {
  if(stack.getItem()==this)
    {
    WorkOrder order = new WorkOrder();
    if(stack.hasTagCompound() && stack.getTagCompound().hasKey("orders"))
      {
      order.readFromNBT(stack.getTagCompound().getCompoundTag("orders"));      
      }
    AWLog.logDebug("returning orders..."+order);
    return order;
    }
  return null;
  }

@Override
public void writeOrders(NpcOrders orders, ItemStack stack)
  {
  if(stack!=null && stack.getItem()==this)
    {
    stack.setTagInfo("orders", orders.writeToNBT(new NBTTagCompound()));
    }
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_WORK_ORDER, 0, 0, 0);
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack)
  {
  WorkOrder wo = getOrders(stack);
  if(wo!=null)
    {
    BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, false);
    if(hit!=null && player.worldObj.getTileEntity(hit.x, hit.y, hit.z) instanceof IWorkSite)
      {
      if(wo.addWorkPosition(player.worldObj, hit))
        {
        writeOrders(wo, stack);
        //TODO add chat output message regarding adding a worksite to the work-orders
        //TODO possibly open the gui after setting the work-point?
//        player.setCurrentItemOrArmor(0, stack);//TODO probably un-necessary        
        }
      }
    }
  }



}
