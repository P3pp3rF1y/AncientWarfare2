package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder;

public class ContainerWorkOrder extends ContainerBase
{

public WorkOrder wo;
private boolean hasChanged;

public ContainerWorkOrder(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  ItemStack stack = player.getCurrentEquippedItem();
  if(stack==null || stack.getItem()==null){throw new IllegalArgumentException("Cannot open Work Order GUI for null stack/item.");}  
  wo = WorkOrder.getWorkOrder(stack);
  if(wo==null){throw new IllegalArgumentException("Work orders was null for some reason");}
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("wo"))
    {
    wo.readFromNBT(tag.getCompoundTag("wo"));
    hasChanged=true;
    }  
  }

@Override
public void onContainerClosed(EntityPlayer par1EntityPlayer)
  {  
  super.onContainerClosed(par1EntityPlayer);
  if(hasChanged && !player.worldObj.isRemote)
    {
    WorkOrder.writeWorkOrder(player.getCurrentEquippedItem(), wo);    
    }
  }

}
