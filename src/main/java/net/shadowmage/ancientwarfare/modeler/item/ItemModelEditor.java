package net.shadowmage.ancientwarfare.modeler.item;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.item.ItemClickable;
import net.shadowmage.ancientwarfare.modeler.AncientWarfareModeler;
import net.shadowmage.ancientwarfare.modeler.gui.GuiModelEditor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemModelEditor extends ItemClickable
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
  super(localizationKey);
  this.setCreativeTab(editorTab);
  }

@Override
public void onRightClick(ItemStack stack, EntityPlayer player, MovingObjectPosition hit)
  {
  if(player.worldObj.isRemote)
    {
    AncientWarfareModeler.proxy.openGui(player);
    }
  }

}
