package net.shadowmage.ancientwarfare.automation.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlocks;
import net.shadowmage.ancientwarfare.automation.block.BlockAutoCrafting;
import net.shadowmage.ancientwarfare.automation.block.BlockChunkLoaderSimple;
import net.shadowmage.ancientwarfare.automation.block.BlockFlywheelController;
import net.shadowmage.ancientwarfare.automation.block.BlockHandCrankedGenerator;
import net.shadowmage.ancientwarfare.automation.block.BlockMailbox;
import net.shadowmage.ancientwarfare.automation.block.BlockTorqueBase;
import net.shadowmage.ancientwarfare.automation.block.BlockTorqueGenerator;
import net.shadowmage.ancientwarfare.automation.block.BlockWorksiteBase;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.item.ItemComponent;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;

import java.util.Collections;
import java.util.Comparator;

@Mod.EventBusSubscriber(modid = AncientWarfareAutomation.modID)
public class AWAutomationItemLoader {

    public static final CreativeTabs automationTab = new CreativeTabs("tabs.automation") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(AWItems.automationHammerIron);
        }

        @Override
        public void displayAllRelevantItems(NonNullList<ItemStack> list) {
            super.displayAllRelevantItems(list);
            Collections.sort(list, sorter);
        }
    };

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        AWItems.worksiteUpgrade = new ItemWorksiteUpgrade().listenToProxy(AncientWarfareAutomation.proxy);
        AWItems.worksiteUpgrade.addSubItem(WorksiteUpgrade.SIZE_MEDIUM.ordinal(), "automation/worksite_upgrade#variant=bounds_medium");
        AWItems.worksiteUpgrade.addSubItem(WorksiteUpgrade.SIZE_LARGE.ordinal(), "automation/worksite_upgrade#variant=bounds_large");
        AWItems.worksiteUpgrade.addSubItem(WorksiteUpgrade.QUARRY_MEDIUM.ordinal(), "automation/worksite_upgrade#variant=quarry_medium");
        AWItems.worksiteUpgrade.addSubItem(WorksiteUpgrade.QUARRY_LARGE.ordinal(), "automation/worksite_upgrade#variant=quarry_large");
        AWItems.worksiteUpgrade.addSubItem(WorksiteUpgrade.ENCHANTED_TOOLS_1.ordinal(), "automation/worksite_upgrade#variant=enchanted_tools_1");
        AWItems.worksiteUpgrade.addSubItem(WorksiteUpgrade.ENCHANTED_TOOLS_2.ordinal(), "automation/worksite_upgrade#variant=enchanted_tools_2");
        AWItems.worksiteUpgrade.addSubItem(WorksiteUpgrade.TOOL_QUALITY_1.ordinal(), "automation/worksite_upgrade#variant=quality_tools_1");
        AWItems.worksiteUpgrade.addSubItem(WorksiteUpgrade.TOOL_QUALITY_2.ordinal(), "automation/worksite_upgrade#variant=quality_tools_2");
        AWItems.worksiteUpgrade.addSubItem(WorksiteUpgrade.TOOL_QUALITY_3.ordinal(), "automation/worksite_upgrade#variant=quality_tools_3");
        AWItems.worksiteUpgrade.addSubItem(WorksiteUpgrade.BASIC_CHUNK_LOADER.ordinal(), "automation/worksite_upgrade#variant=chunkloader_basic");
        AWItems.worksiteUpgrade.addSubItem(WorksiteUpgrade.QUARRY_CHUNK_LOADER.ordinal(), "automation/worksite_upgrade#variant=chunkloader_quarry");

        registry.register(AWItems.worksiteUpgrade);

        AWItems.componentItem.addSubItem(ItemComponent.WOODEN_GEAR_SET, "automation/component#variant=wooden_gear", "gearWood");
        AWItems.componentItem.addSubItem(ItemComponent.IRON_GEAR_SET, "automation/component#variant=iron_gear", "gearIron");
        AWItems.componentItem.addSubItem(ItemComponent.STEEL_GEAR_SET, "automation/component#variant=steel_gear", "gearSteel");
        AWItems.componentItem.addSubItem(ItemComponent.WOODEN_BEARINGS, "automation/component#variant=wooden_bearings", "bearingWood");
        AWItems.componentItem.addSubItem(ItemComponent.IRON_BEARINGS, "automation/component#variant=iron_bearings", "bearingIron");
        AWItems.componentItem.addSubItem(ItemComponent.STEEL_BEARINGS, "automation/component#variant=steel_bearings", "bearingSteel");
        AWItems.componentItem.addSubItem(ItemComponent.WOODEN_TORQUE_SHAFT, "automation/component#variant=wooden_shaft", "shaftWood");
        AWItems.componentItem.addSubItem(ItemComponent.IRON_TORQUE_SHAFT, "automation/component#variant=iron_shaft", "shaftIron");
        AWItems.componentItem.addSubItem(ItemComponent.STEEL_TORQUE_SHAFT, "automation/component#variant=steel_shaft", "shaftSteel");
    }

    private static final Comparator sorter = new Comparator<ItemStack>() {

        @Override
        public int compare(ItemStack arg0, ItemStack arg1) {
            int i1p = getItemPriority(arg0.getItem());
            int i2p = getItemPriority(arg1.getItem());
            if (i1p == i2p) {
                return arg0.getDisplayName().compareTo(arg1.getDisplayName());
            } else {
                return i1p - i2p;
            }
        }

        private int getItemPriority(Item item) {
            if (item instanceof ItemBlock) {
                Block block = ((ItemBlock) item).getBlock();
                if (block instanceof BlockChunkLoaderSimple) {
                    return 10;
                } else if (block instanceof BlockFlywheelController) {
                    return 9;
                } else if (block instanceof BlockTorqueGenerator || block instanceof BlockHandCrankedGenerator) {
                    return 8;
                } else if (block instanceof BlockTorqueBase) {
                    return 7;
                } else if (block instanceof BlockMailbox) {
                    return 6;
                } else if (block == AWAutomationBlocks.warehouseInterface || block == AWAutomationBlocks.warehouseCrafting) {
                    return 5;
                } else if (block == AWAutomationBlocks.worksiteWarehouse) {
                    return 4;
                } else if (block instanceof BlockAutoCrafting) {
                    return 3;
                } else if (block instanceof BlockWorksiteBase) {
                    return 2;
                }
            }
            return 0;
        }
    };
}
