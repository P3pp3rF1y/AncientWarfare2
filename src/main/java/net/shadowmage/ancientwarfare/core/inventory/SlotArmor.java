package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SlotArmor extends Slot
{

int armorSlotID;
Entity entity;
public SlotArmor(IInventory par1iInventory, int par2, int par3, int par4, int armorSlotID, Entity entity)
  {
  super(par1iInventory, par2, par3, par4);
  this.armorSlotID = armorSlotID;
  this.entity = entity;
  }

@Override
public int getSlotStackLimit()
  {
  return 1;
  }

@Override
public boolean isItemValid(ItemStack par1ItemStack)
  {
  if (par1ItemStack == null) return false;
  return par1ItemStack.getItem().isValidArmor(par1ItemStack, armorSlotID, entity);
  }

@Override
@SideOnly(Side.CLIENT)
public IIcon getBackgroundIconIndex()
  {
  return ItemArmor.func_94602_b(armorSlotID);
  }

}
