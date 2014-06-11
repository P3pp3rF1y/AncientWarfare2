package net.shadowmage.ancientwarfare.automation.item;

import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.chunkLoaderDeluxe;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.chunkLoaderSimple;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.flywheel;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.handCrankedEngine;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.mailbox;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.torqueConduit;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.torqueDistributor;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.torqueGeneratorSterling;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.torqueGeneratorWaterwheel;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.warehouseCrafting;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.warehouseInterface;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.worksiteAnimalFarm;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.worksiteAutoCrafting;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.worksiteCropFarm;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.worksiteFishFarm;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.worksiteForestry;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.worksiteMushroomFarm;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.worksiteQuarry;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.worksiteReedFarm;
import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.worksiteWarehouse;

import java.util.Collections;
import java.util.Comparator;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.api.AWItems;
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
    return AWItems.automationHammerIron;
    }  
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void displayAllReleventItems(java.util.List par1List) 
    {
    super.displayAllReleventItems(par1List);
    Collections.sort(par1List, sorter);    
    };
  };

  
public static void load()
  {
  }

private static final TabSorter sorter = new TabSorter();
private static class TabSorter implements Comparator<ItemStack>
{

@Override
public int compare(ItemStack arg0, ItemStack arg1)
  {
  Item i1 = arg0.getItem();
  Item i2 = arg1.getItem();
  int i1p = getItemPriority(i1);
  int i2p = getItemPriority(i2);
  if(i1p==i2p)
    {
    return arg0.getDisplayName().compareTo(arg1.getDisplayName());
    }
  else
    {
    return i1p < i2p ? -1 : 1;
    }
  }

private int getItemPriority(Item item)
  {
  if(item instanceof ItemBlock)
    {
    ItemBlock iblock = (ItemBlock)item;
    Block block = iblock.field_150939_a;
    if(block==chunkLoaderSimple || block==chunkLoaderDeluxe){return 10;}
    else if(block==flywheel){return 9;}
    else if(block==torqueGeneratorSterling || block==torqueGeneratorWaterwheel || block==handCrankedEngine){return 8;}
    else if(block==torqueDistributor || block==torqueConduit){return 7;}
    else if(block==mailbox){return 6;}
    else if(block==warehouseInterface || block==warehouseCrafting){return 5;}
    else if(block==worksiteWarehouse){return 4;}
    else if(block==worksiteAutoCrafting){return 3;}
    else if(block==worksiteQuarry || block==worksiteForestry || block==worksiteCropFarm || block==worksiteMushroomFarm
        || block==worksiteAnimalFarm || block==worksiteReedFarm || block==worksiteFishFarm){return 2;}    
    }
  return 0;
  }
}
}
