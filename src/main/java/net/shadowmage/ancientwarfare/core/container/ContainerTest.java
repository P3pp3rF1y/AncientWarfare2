package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerTest extends ContainerBase
{

ItemStack[] stacks = new ItemStack[5];
public IInventory inventory;

public ContainerTest(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  inventory = new IInventory()
    {

    @Override
    public int getSizeInventory()
      {
      // TODO Auto-generated method stub
      return 5;
      }

    @Override
    public ItemStack getStackInSlot(int var1)
      {
      return stacks[var1];
      }

    @Override
    public ItemStack decrStackSize(int var1, int var2)
      {
      ItemStack stack = stacks[var1];
      ItemStack returnStack = null;
      if(stack!=null)
        {
        returnStack = stack.copy();
        stack.stackSize-=var2;
        returnStack.stackSize = var2;
        if(stack.stackSize<=0)
          {
          stacks[var1] = null;
          }
        }
      return returnStack;
      }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1)
      {
      ItemStack stack = stacks[var1];
      stacks[var1] = null;
      return stack;
      }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2)
      {
      stacks[var1] = var2;
      }

    @Override
    public String getInventoryName()
      {
      return "testInventory";
      }

    @Override
    public boolean hasCustomInventoryName()
      {
      return false;
      }

    @Override
    public int getInventoryStackLimit()
      {
      return 64;
      }

    @Override
    public void markDirty()
      {

      }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1)
      {
      return true;
      }

    @Override
    public void openInventory()
      {

      }

    @Override
    public void closeInventory()
      {

      }

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2)
      {
      return true;
      }
    };
  this.inventories = new IInventory[1];
  this.inventories[0] = inventory;
  inventory.setInventorySlotContents(0, new ItemStack(Items.stick));
  }

}
