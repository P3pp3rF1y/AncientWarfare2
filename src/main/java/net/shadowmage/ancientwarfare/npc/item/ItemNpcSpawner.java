package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class ItemNpcSpawner extends Item implements IItemClickable
{

public ItemNpcSpawner(String regName)
  {
  this.setCreativeTab(AWNPCItemLoader.npcTab);
  this.setUnlocalizedName(regName);
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, true);
  if(hit==null){return;}  
  NpcBase npc = (NpcBase) AWEntityRegistry.createEntity(AWEntityRegistry.NPC_TEST, player.worldObj);
  if(npc==null){return;}
  npc.setOwnerName(player.getCommandSenderName());
  npc.setPosition(hit.x+0.5d, hit.y, hit.z+0.5d);
  if(stack.hasTagCompound()&&stack.getTagCompound().hasKey("additionalNpcData"))
    {
    npc.readAdditionalItemData(stack.getTagCompound().getCompoundTag("additionalNpcData"));
    }
  player.worldObj.spawnEntityInWorld(npc);  
  stack.stackSize--;
  if(stack.stackSize<=0)
    {
    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
    }
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

}
