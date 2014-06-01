package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class ItemBackpack extends Item implements IItemClickable
{

public ItemBackpack(String regName)
  {
  this.setUnlocalizedName(regName);
  this.setCreativeTab(AWCoreBlockLoader.coreTab);
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_BACKPACK, 0, 0, 0);
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
public boolean getShareTag()
  {
  return false;
  }

public static InventoryBackpack getInventoryFor(ItemStack stack)
  {
  if(stack==null || stack.getItem()!=AWItems.backpack){return null;}  
  InventoryBackpack pack = new InventoryBackpack((stack.getItemDamage()+1)*9);
  if(stack.hasTagCompound() && stack.getTagCompound().hasKey("backpackItems"))
    {
    InventoryTools.readInventoryFromNBT(pack, stack.getTagCompound().getCompoundTag("backpackItems"));
    }
  return pack;
  }

public static void writeBackpackToItem(InventoryBackpack pack, ItemStack stack)
  {
  if(stack==null || stack.getItem()!=AWItems.backpack){return;}
  NBTTagCompound invTag = InventoryTools.writeInventoryToNBT(pack, new NBTTagCompound());
  stack.setTagInfo("backpackItems", invTag);
  }

}
