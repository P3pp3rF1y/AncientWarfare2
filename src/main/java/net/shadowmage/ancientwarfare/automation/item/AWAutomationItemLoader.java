package net.shadowmage.ancientwarfare.automation.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AWAutomationItemLoader
{

public static final CreativeTabs automationTab = new CreativeTabs("tabs.automation")
  {
  @Override
  @SideOnly(Side.CLIENT)
  public Item getTabIconItem()
    {  
    return Items.stick;
    }  
  };
 

public static void load()
  {
  AWItems.automationHammer = new ItemHammer("hammer");
  AWItems.automationHammer.setTextureName("ancientwarfare:automation/hammer");
  GameRegistry.registerItem(AWItems.automationHammer, "hammer");
  }

}
