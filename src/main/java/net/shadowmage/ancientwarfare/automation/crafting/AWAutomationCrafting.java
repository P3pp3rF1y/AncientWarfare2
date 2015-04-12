package net.shadowmage.ancientwarfare.automation.crafting;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.item.ItemComponent;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;

public class AWAutomationCrafting {

    /**
     * load any recipes for automation module recipes
     */
    public static void loadRecipes() {
        ItemStack woodenGear = AWItems.componentItem.getSubItem(ItemComponent.WOODEN_GEAR_SET);
        ItemStack ironGear = AWItems.componentItem.getSubItem(ItemComponent.IRON_GEAR_SET);
        ItemStack steelGear = AWItems.componentItem.getSubItem(ItemComponent.STEEL_GEAR_SET);
        ItemStack woodenBushing = AWItems.componentItem.getSubItem(ItemComponent.WOODEN_BUSHINGS);
        ItemStack ironBearing = AWItems.componentItem.getSubItem(ItemComponent.IRON_BEARINGS);
        ItemStack steelBearing = AWItems.componentItem.getSubItem(ItemComponent.STEEL_BEARINGS);
        ItemStack woodShaft = AWItems.componentItem.getSubItem(ItemComponent.WOODEN_TORQUE_SHAFT);
        ItemStack ironShaft = AWItems.componentItem.getSubItem(ItemComponent.IRON_TORQUE_SHAFT);
        ItemStack steelShaft = AWItems.componentItem.getSubItem(ItemComponent.STEEL_TORQUE_SHAFT);
        //wooden gear set
        AWCraftingManager.INSTANCE.createRecipe(woodenGear.copy(), "",
                "s_s",
                "_p_",
                "s_s",
                's', "stickWood",
                'p', "plankWood");
        //iron gear
        AWCraftingManager.INSTANCE.createRecipe(ironGear.copy(), "",
                "i_i",
                "_i_",
                "i_i",
                'i', "ingotIron");
        //steel gear
        AWCraftingManager.INSTANCE.createRecipe(steelGear.copy(), "",
                "i_i",
                "_i_",
                "i_i",
                'i', "ingotSteel");

        //wooden bushing set
        AWCraftingManager.INSTANCE.createRecipe(woodenBushing.copy(), "",
                "s_s",
                "___",
                "s_s",
                's', "stickWood",
                'p', "plankWood");
        //iron bearing
        AWCraftingManager.INSTANCE.createRecipe(ironBearing.copy(), "",
                "_i_",
                "i_i",
                "_i_",
                'i', "ingotIron");
        //steel bearing
        AWCraftingManager.INSTANCE.createRecipe(steelBearing.copy(), "",
                "_i_",
                "i_i",
                "_i_",
                'i', "ingotSteel");

        //wooden shaft
        AWCraftingManager.INSTANCE.createRecipe(woodShaft.copy(), "",
                "_p_",
                "_p_",
                "_p_",
                'p', "plankWood");
        //iron shaft
        AWCraftingManager.INSTANCE.createRecipe(ironShaft.copy(), "",
                "_i_",
                "_i_",
                "_i_",
                'i', "ingotIron");
        //steel shaft
        AWCraftingManager.INSTANCE.createRecipe(steelShaft.copy(), "",
                "_i_",
                "_i_",
                "_i_",
                'i', "ingotSteel");

        //torque conduit s
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueConduit, 1, 0), "the_wheel",
                "_s_",
                "sgs",
                "_s_",
                's', woodShaft.copy(),
                'g', "gearWood");

        //torque conduit m
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueConduit, 1, 1), "the_wheel",
                "_s_",
                "sgs",
                "_s_",
                's', ironShaft.copy(),
                'g', "gearIron");

        //torque conduit l
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueConduit, 1, 2), "mass_production",
                "_s_",
                "sgs",
                "_s_",
                's', steelShaft.copy(),
                'g', "gearSteel");

        //torque distributor s
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueDistributor, 1, 0), "the_wheel",
                "_s_",
                "ggg",
                "_s_",
                's', woodShaft.copy(),
                'g', "gearWood");

        //torque distributor m
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueDistributor, 1, 1), "the_wheel",
                "_s_",
                "ggg",
                "_s_",
                's', ironShaft.copy(),
                'g', "gearIron");

        //torque distributor l
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueDistributor, 1, 2), "mass_production",
                "_s_",
                "ggg",
                "_s_",
                's', steelShaft.copy(),
                'g', "gearSteel");

        //torque shaft s
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueShaft, 1, 0), "the_wheel",
                "s",
                "s",
                "g",
                's', woodShaft.copy(),
                'g', "gearWood");

        //torque shaft m
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueShaft, 1, 1), "the_wheel",
                "s",
                "s",
                "g",
                's', ironShaft.copy(),
                'g', "gearIron");

        //torque shaft l
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueShaft, 1, 2), "mass_production",
                "s",
                "s",
                "g",
                's', steelShaft.copy(),
                'g', "gearSteel");

        //torque flywheel control l
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.flywheel, 1, 2), "mass_production",
                "igi",
                "iii",
                "igi",
                'i', "ingotSteel",
                'g', "gearSteel");

        ItemStack upgradeBoundsMed = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.SIZE_MEDIUM.ordinal());//engineering
        ItemStack upgradeBoundsLarge = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.SIZE_LARGE.ordinal());//mathematics
        ItemStack upgradeQuarryMed = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.QUARRY_MEDIUM.ordinal());//mining
        ItemStack upgradeQuarryLarge = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.QUARRY_LARGE.ordinal());//machinery
        ItemStack upgradeEnchantedTools1 = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.ENCHANTED_TOOLS_1.ordinal());//mathematics
        ItemStack upgradeEnchantedTools2 = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.ENCHANTED_TOOLS_2.ordinal());//mass production
        ItemStack upgradeQualityTools1 = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.TOOL_QUALITY_1.ordinal());//engineering
        ItemStack upgradeQualityTools2 = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.TOOL_QUALITY_2.ordinal());//construction
        ItemStack upgradeQualityTools3 = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.TOOL_QUALITY_3.ordinal());//refining
        ItemStack upgradeBasicChunkloader = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.BASIC_CHUNK_LOADER.ordinal());//mathematics
        ItemStack upgradeQuarryChunkloader = AWItems.worksiteUpgrade.getSubItem(WorksiteUpgrade.QUARRY_CHUNK_LOADER.ordinal());//machinery

        AWCraftingManager.INSTANCE.createRecipe(upgradeBoundsMed, "engineering",
                " s ",
                "fff",
                " s ",
                's', Items.string,
                'f', Blocks.fence);

        AWCraftingManager.INSTANCE.createRecipe(upgradeBoundsLarge, "mathematics",
                "fff",
                "sss",
                "fff",
                's', Items.string,
                'f', Blocks.fence);

        AWCraftingManager.INSTANCE.createRecipe(upgradeQuarryMed, "mining",
                "ppp",
                "f f",
                "f f",
                'p', "plankWood",
                'f', Blocks.fence);

        AWCraftingManager.INSTANCE.createRecipe(upgradeQuarryLarge, "machinery",
                "ppp",
                "fif",
                "fif",
                'p', "plankWood",
                'f', Blocks.fence,
                'i', "ingotIron");

        ItemStack enchantedBook = new ItemStack(Items.enchanted_book, 1, 0);
        enchantedBook.addEnchantment(Enchantment.fortune, 1);

        AWCraftingManager.INSTANCE.createRecipe(upgradeEnchantedTools1, "mathematics",
                " b ",
                "iii",
                'b', enchantedBook,
                'i', "ingotIron");

        enchantedBook = new ItemStack(Items.enchanted_book, 1, 0);
        enchantedBook.addEnchantment(Enchantment.fortune, 2);

        AWCraftingManager.INSTANCE.createRecipe(upgradeEnchantedTools2, "mass_production",
                "b b",
                "iii",
                'b', enchantedBook,
                'i', "ingotIron");

        AWCraftingManager.INSTANCE.createRecipe(upgradeQualityTools1, "engineering",
                "psa",
                'p', Items.iron_pickaxe,
                's', Items.iron_shovel,
                'a', Items.iron_axe);

        AWCraftingManager.INSTANCE.createRecipe(upgradeQualityTools2, "construction",
                "psa",
                'p', Items.diamond_pickaxe,
                's', Items.diamond_shovel,
                'a', Items.diamond_axe);

        AWCraftingManager.INSTANCE.createRecipe(upgradeQualityTools3, "refining",
                "oto",
                "psa",
                "oto",
                't', "ingotSteel",
                'o', Blocks.obsidian,
                'p', Items.diamond_pickaxe,
                's', Items.diamond_shovel,
                'a', Items.diamond_axe);

        AWCraftingManager.INSTANCE.createRecipe(upgradeBasicChunkloader, "mathematics",
                " i ",
                "ici",
                " i ",
                'i', "ingotIron",
                'c', AWAutomationBlockLoader.chunkLoaderSimple);

        AWCraftingManager.INSTANCE.createRecipe(upgradeQuarryChunkloader, "machinery",
                " i ",
                "ici",
                " i ",
                'i', "ingotIron",
                'c', AWAutomationBlockLoader.chunkLoaderDeluxe);

    }


}
