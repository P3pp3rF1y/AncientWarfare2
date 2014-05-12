package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class WarehouseRoutedItem
{

private TileEntity origin;
private TileEntity destination;
private Item item;
private int damage;
private int quantity;
private NBTTagCompound itemTag;

public WarehouseRoutedItem(TileEntity origin, TileEntity destination, ItemStack stack)
  {  
  if(origin==null || destination==null || stack==null || stack.getItem()==null)
    {
    throw new IllegalArgumentException("Origin, destination, stack, and stack-item must not be null.");
    }  
  this.origin = origin;
  this.destination = destination;
  this.item = stack.getItem();
  this.quantity = stack.stackSize;
  this.damage = stack.getItemDamage();
  this.itemTag = (NBTTagCompound) (stack.stackTagCompound==null ? null : stack.stackTagCompound.copy());
  }

public WarehouseRoutedItem(TileEntity origin, TileEntity destination, Item item, int damage, int quantity, NBTTagCompound itemTag)
  {
  this.origin = origin;
  this.destination = destination;
  this.item = item;
  this.quantity = quantity;
  this.damage = damage;
  this.itemTag = (NBTTagCompound) (itemTag==null ? null : itemTag.copy());
  }

public final Item getItem()
  {
  return item;
  }

public final int getDamage()
  {
  return damage;
  }

public final NBTTagCompound getItemTag()
  {
  return itemTag;
  }

public final TileEntity getOrigin()
  {
  return origin;
  }

public final TileEntity getDestination()
  {
  return destination;
  }

public final int getQuantity()
  {
  return quantity;
  }

}
