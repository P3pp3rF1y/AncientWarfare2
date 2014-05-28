package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.orders.NpcOrders;
import net.shadowmage.ancientwarfare.npc.orders.UpkeepOrder;

public class ItemUpkeepOrder extends ItemOrders
{

public ItemUpkeepOrder(String name)
  {
  super(name);
  this.setTextureName("ancientwarfare:npc/upkeep_order");
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_UPKEEP_ORDER, 0, 0, 0);
  }

@Override
public boolean onKeyActionClient(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack)
  {
  //TODO set upkeep position
  //TODO open GUI so player may select block-side
  UpkeepOrder upkeepOrder = UpkeepOrder.getUpkeepOrder(stack);
  if(upkeepOrder!=null)
    {
    BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, false);
    if(hit!=null && player.worldObj.getTileEntity(hit.x, hit.y, hit.z) instanceof IInventory)
      {
      if(upkeepOrder.addUpkeepPosition(player.worldObj, hit))
        {
        UpkeepOrder.writeUpkeepOrder(stack, upkeepOrder);
        player.openContainer.detectAndSendChanges();
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_UPKEEP_ORDER, 0, 0, 0);
        //TODO add chat output message regarding adding a worksite to the work-orders
        }
      }
    }
  }

}
