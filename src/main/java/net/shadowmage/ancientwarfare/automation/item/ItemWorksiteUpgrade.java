package net.shadowmage.ancientwarfare.automation.item;

import java.util.HashMap;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.item.ItemBase;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

public class ItemWorksiteUpgrade extends ItemBase implements IItemClickable
{

HashMap<Integer, String> subItems = new HashMap<Integer, String>();
HashMap<Integer, IIcon> subItemIcons = new HashMap<Integer, IIcon>();

public ItemWorksiteUpgrade(String localizationKey)
  {
  super(localizationKey);
  this.setHasSubtypes(true);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  }

@Override
public String getUnlocalizedName(ItemStack par1ItemStack)
  {
  return super.getUnlocalizedName(par1ItemStack)+"."+par1ItemStack.getItemDamage();
  }

@SuppressWarnings({ "unchecked", "rawtypes" })
@Override
public void getSubItems(Item item, CreativeTabs p_150895_2_, List list)
  {
  for(Integer num : subItems.keySet())
    {
    list.add(new ItemStack(item,1,num));
    }
  }

@Override
public IIcon getIconFromDamage(int par1)
  {
  return subItemIcons.get(par1);
  }

@Override
public void registerIcons(IIconRegister par1IconRegister)
  {
  for(Integer num : subItems.keySet())
    {
    subItemIcons.put(num, par1IconRegister.registerIcon(subItems.get(num)));
    }
  }

public void addSubItemIcon(int num, String texture)
  {
  subItems.put(num, texture);
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public boolean cancelRightClick(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  BlockPosition pos = BlockTools.getBlockClickedOn(player, player.worldObj, false);
  if(pos!=null)
    {
    TileEntity te = player.worldObj.getTileEntity(pos.x, pos.y, pos.z);
    if(te instanceof IWorkSite)
      {
      if(((IWorkSite) te).onUpgradeItemUsed(stack))
        {
        stack.stackSize--;
        if(stack.stackSize<=0)
          {
          player.destroyCurrentEquippedItem();          
          }
        player.openContainer.detectAndSendChanges();
        }
      }
    }
  }

@Override
public boolean onLeftClickClient(EntityPlayer player, ItemStack stack){return false;}

@Override
public boolean cancelLeftClick(EntityPlayer player, ItemStack stack){return false;}

@Override
public void onLeftClick(EntityPlayer player, ItemStack stack){}

}
