package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;

public class ItemShield extends Item
{

ToolMaterial material;
int armorValue;

public ItemShield(String name, ToolMaterial material)
  {
  setUnlocalizedName(name);
  setCreativeTab(AWNpcItemLoader.npcTab);
  this.setFull3D();
  this.setTextureName("ancientwarfare:npc/"+name);  
  this.material = material;
  this.armorValue = material.getHarvestLevel()*2 + 1;
  }

public int getArmorBonusValue()
  {
  return armorValue;
  }

//block action does strange orientation/rendering -- can perhaps try and counter it in render somehow
//
//@Override
//public EnumAction getItemUseAction(ItemStack par1ItemStack)
//  {
//  return EnumAction.block;
//  }
//
//@Override
//public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
//  {
//  par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
//  return par1ItemStack;
//  }
//
//@Override
//public int getMaxItemUseDuration(ItemStack par1ItemStack)
//  {
//  return 72000;
//  }

}
