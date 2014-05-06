package net.shadowmage.ancientwarfare.modeler.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.modeler.AncientWarfareModeler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemModelEditor extends Item implements IItemClickable
{

public static final CreativeTabs editorTab = new CreativeTabs("tabs.editor")
  {    
  @Override
  @SideOnly(Side.CLIENT)
  public Item getTabIconItem()
    {
    return Items.stick;
    }
  };

public ItemModelEditor(String localizationKey)
  {
  this.setUnlocalizedName(localizationKey);
  this.setCreativeTab(editorTab);
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack)
  {
  if(player.worldObj.isRemote)
    {
    AncientWarfareModeler.proxy.openGui(player);
    }
  return false;
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  
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
