package net.shadowmage.ancientwarfare.automation.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.item.ItemComponent;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;

import java.util.Collections;
import java.util.Comparator;

import static net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader.*;

public class AWAutomationItemLoader {

    public static final CreativeTabs automationTab = new CreativeTabs("tabs.automation") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return AWItems.automationHammerIron;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public void displayAllReleventItems(java.util.List par1List) {
            super.displayAllReleventItems(par1List);
            Collections.sort(par1List, sorter);
        }

        ;
    };

    public static void load() {
        ((ItemComponent) AWItems.componentItem).addSubItem(ItemComponent.WOODEN_GEAR_SET, "ancientwarfare:automation/wooden_gear");
        ((ItemComponent) AWItems.componentItem).addSubItem(ItemComponent.IRON_GEAR_SET, "ancientwarfare:automation/iron_gear");
        ((ItemComponent) AWItems.componentItem).addSubItem(ItemComponent.STEEL_GEAR_SET, "ancientwarfare:automation/steel_gear");
        ((ItemComponent) AWItems.componentItem).addSubItem(ItemComponent.WOODEN_BUSHINGS, "ancientwarfare:automation/wooden_bearings");
        ((ItemComponent) AWItems.componentItem).addSubItem(ItemComponent.IRON_BEARINGS, "ancientwarfare:automation/iron_bearings");
        ((ItemComponent) AWItems.componentItem).addSubItem(ItemComponent.STEEL_BEARINGS, "ancientwarfare:automation/steel_bearings");
        ((ItemComponent) AWItems.componentItem).addSubItem(ItemComponent.WOODEN_TORQUE_SHAFT, "ancientwarfare:automation/wooden_shaft");
        ((ItemComponent) AWItems.componentItem).addSubItem(ItemComponent.IRON_TORQUE_SHAFT, "ancientwarfare:automation/iron_shaft");
        ((ItemComponent) AWItems.componentItem).addSubItem(ItemComponent.STEEL_TORQUE_SHAFT, "ancientwarfare:automation/steel_shaft");

        ItemWorksiteUpgrade item = new ItemWorksiteUpgrade("worksite_upgrade");
        AWItems.worksiteUpgrade = item;
        item.addSubItemIcon(WorksiteUpgrade.SIZE_MEDIUM.flag(), "ancientwarfare:automation/upgrade_bounds_medium");
        item.addSubItemIcon(WorksiteUpgrade.SIZE_LARGE.flag(), "ancientwarfare:automation/upgrade_bounds_large");
        item.addSubItemIcon(WorksiteUpgrade.QUARRY_MEDIUM.flag(), "ancientwarfare:automation/upgrade_quarry_medium");
        item.addSubItemIcon(WorksiteUpgrade.QUARRY_LARGE.flag(), "ancientwarfare:automation/upgrade_quarry_large");
        item.addSubItemIcon(WorksiteUpgrade.ENCHANTED_TOOLS_1.flag(), "ancientwarfare:automation/upgrade_enchanted_tools_1");
        item.addSubItemIcon(WorksiteUpgrade.ENCHANTED_TOOLS_2.flag(), "ancientwarfare:automation/upgrade_enchanted_tools_2");
        item.addSubItemIcon(WorksiteUpgrade.TOOL_QUALITY_1.flag(), "ancientwarfare:automation/upgrade_quality_tools_1");
        item.addSubItemIcon(WorksiteUpgrade.TOOL_QUALITY_2.flag(), "ancientwarfare:automation/upgrade_quality_tools_2");
        item.addSubItemIcon(WorksiteUpgrade.TOOL_QUALITY_3.flag(), "ancientwarfare:automation/upgrade_quality_tools_3");
        item.addSubItemIcon(WorksiteUpgrade.BASIC_CHUNK_LOADER.flag(), "ancientwarfare:automation/upgrade_chunkloader_basic");
        item.addSubItemIcon(WorksiteUpgrade.QUARRY_CHUNK_LOADER.flag(), "ancientwarfare:automation/upgrade_chunkloader_quarry");
        GameRegistry.registerItem(AWItems.worksiteUpgrade, "worksite_upgrade");
    }

    private static final Comparator sorter = new Comparator<ItemStack>() {

        @Override
        public int compare(ItemStack arg0, ItemStack arg1) {
            Item i1 = arg0.getItem();
            Item i2 = arg1.getItem();
            int i1p = getItemPriority(i1);
            int i2p = getItemPriority(i2);
            if (i1p == i2p) {
                return arg0.getDisplayName().compareTo(arg1.getDisplayName());
            } else {
                return i1p < i2p ? -1 : 1;
            }
        }

        private int getItemPriority(Item item) {
            if (item instanceof ItemBlock) {
                ItemBlock iblock = (ItemBlock) item;
                Block block = iblock.field_150939_a;
                if (block == chunkLoaderSimple || block == chunkLoaderDeluxe) {
                    return 10;
                } else if (block == flywheel) {
                    return 9;
                } else if (block == torqueGeneratorSterling || block == torqueGeneratorWaterwheel || block == handCrankedEngine) {
                    return 8;
                } else if (block == torqueDistributor || block == torqueConduit) {
                    return 7;
                } else if (block == mailbox) {
                    return 6;
                } else if (block == warehouseInterface || block == warehouseCrafting) {
                    return 5;
                } else if (block == worksiteWarehouse) {
                    return 4;
                } else if (block == worksiteAutoCrafting) {
                    return 3;
                } else if (block == worksiteQuarry || block == worksiteForestry || block == worksiteCropFarm || block == worksiteMushroomFarm
                        || block == worksiteAnimalFarm || block == worksiteReedFarm || block == worksiteFishFarm) {
                    return 2;
                }
                }
            }
            return 0;
        }
    };
}
