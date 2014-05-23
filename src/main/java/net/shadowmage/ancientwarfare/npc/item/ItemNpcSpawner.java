package net.shadowmage.ancientwarfare.npc.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemNpcSpawner extends Item implements IItemClickable
{

public static final List<String> npcNames = new ArrayList<String>();

public ItemNpcSpawner(String regName)
  {
  this.setCreativeTab(AWNpcItemLoader.npcTab);
  this.setUnlocalizedName(regName);
  }

@Override
public String getUnlocalizedName()
  {  
  return super.getUnlocalizedName();
  }

@Override
public String getUnlocalizedName(ItemStack par1ItemStack)
  {  
  String npcName = getNpcType(par1ItemStack);
  return super.getUnlocalizedName(par1ItemStack) + (npcName==null? "" : "."+npcName);
  }

@Override
public String getItemStackDisplayName(ItemStack par1ItemStack)
  {
  return super.getItemStackDisplayName(par1ItemStack);
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
  String type = getNpcType(stack);
  if(type==null){return;}
  NpcBase npc = (NpcBase) AWEntityRegistry.createEntity(type, player.worldObj);
  if(npc==null){return;}
  npc.setOwnerName(player.getCommandSenderName());
  npc.setPosition(hit.x+0.5d, hit.y, hit.z+0.5d);
  npc.setHomeArea(hit.x, hit.y, hit.z, 60);
  if(stack.hasTagCompound()&&stack.getTagCompound().hasKey("npcStoredData"))
    {
    npc.readAdditionalItemData(stack.getTagCompound().getCompoundTag("npcStoredData"));
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

@Override
@SideOnly(Side.CLIENT)
public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List list)
  {
  for(String string : npcNames)
    {
    list.add(getStackForNpcType(string));
    }
  }

public ItemStack getStackForNpcType(String type)
  {
  ItemStack stack = new ItemStack(this);
  stack.setTagInfo("npcType", new NBTTagString(type));
  return stack;
  }

public String getNpcType(ItemStack stack)
  {
  if(stack.hasTagCompound() && stack.getTagCompound().hasKey("npcType"))
    {
    return stack.getTagCompound().getString("npcType");
    }
  return null;
  }

}
