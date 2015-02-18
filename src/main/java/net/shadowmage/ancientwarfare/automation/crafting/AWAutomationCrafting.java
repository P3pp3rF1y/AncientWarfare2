package net.shadowmage.ancientwarfare.automation.crafting;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
        ItemStack woodenGear = new ItemStack(AWItems.componentItem, 1, ItemComponent.WOODEN_GEAR_SET);
        ItemStack ironGear = new ItemStack(AWItems.componentItem, 1, ItemComponent.IRON_GEAR_SET);
        ItemStack steelGear = new ItemStack(AWItems.componentItem, 1, ItemComponent.STEEL_GEAR_SET);
        ItemStack woodenBushing = new ItemStack(AWItems.componentItem, 1, ItemComponent.WOODEN_BUSHINGS);
        ItemStack ironBearing = new ItemStack(AWItems.componentItem, 1, ItemComponent.IRON_BEARINGS);
        ItemStack steelBearing = new ItemStack(AWItems.componentItem, 1, ItemComponent.STEEL_BEARINGS);
        ItemStack woodShaft = new ItemStack(AWItems.componentItem, 1, ItemComponent.WOODEN_TORQUE_SHAFT);
        ItemStack ironShaft = new ItemStack(AWItems.componentItem, 1, ItemComponent.IRON_TORQUE_SHAFT);
        ItemStack steelShaft = new ItemStack(AWItems.componentItem, 1, ItemComponent.STEEL_TORQUE_SHAFT);

        //wooden gear set
        AWCraftingManager.INSTANCE.createRecipe(woodenGear.copy(), "",
                "s_s",
                "_p_",
                "s_s",
                's', Items.stick,
                'p', Blocks.planks);
        //iron gear
        AWCraftingManager.INSTANCE.createRecipe(ironGear.copy(), "",
                "i_i",
                "_i_",
                "i_i",
                'i', Items.iron_ingot);
        //steel gear
        AWCraftingManager.INSTANCE.createRecipe(steelGear.copy(), "",
                "i_i",
                "_i_",
                "i_i",
                'i', AWItems.steel_ingot);

        //wooden bushing set
        AWCraftingManager.INSTANCE.createRecipe(woodenBushing.copy(), "",
                "s_s",
                "___",
                "s_s",
                's', Items.stick,
                'p', Blocks.planks);
        //iron bearing
        AWCraftingManager.INSTANCE.createRecipe(ironBearing.copy(), "",
                "_i_",
                "i_i",
                "_i_",
                'i', Items.iron_ingot);
        //steel bearing
        AWCraftingManager.INSTANCE.createRecipe(steelBearing.copy(), "",
                "_i_",
                "i_i",
                "_i_",
                'i', AWItems.steel_ingot);

        //wooden shaft
        AWCraftingManager.INSTANCE.createRecipe(woodShaft.copy(), "",
                "_p_",
                "_p_",
                "_p_",
                'p', Blocks.planks);
        //iron shaft
        AWCraftingManager.INSTANCE.createRecipe(ironShaft.copy(), "",
                "_i_",
                "_i_",
                "_i_",
                'i', Items.iron_ingot);
        //steel shaft
        AWCraftingManager.INSTANCE.createRecipe(steelShaft.copy(), "",
                "_i_",
                "_i_",
                "_i_",
                'i', AWItems.steel_ingot);

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteAutoCrafting), "mass_production",
                "_c_",
                "gwg",
                "_i_",
                '_', Blocks.planks,
                'c', Blocks.chest,
                'w', Blocks.crafting_table,
                'i', Items.iron_ingot,
                'g', woodenGear.copy());

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteCropFarm), "agriculture",
                "___",
                "gwg",
                "_c_",
                '_', Blocks.planks,
                'w', Items.iron_hoe,
                'c', Blocks.chest,
                'g', woodenGear.copy());

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteReedFarm), "farming",
                "___",
                "gwg",
                "_c_",
                '_', Blocks.planks,
                'w', Items.iron_shovel,
                'c', Blocks.chest,
                'g', woodenGear.copy());

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteMushroomFarm), "farming",
                "___",
                "gwg",
                "_c_",
                '_', Blocks.planks,
                'w', Items.wooden_shovel,
                'c', Blocks.chest,
                'g', woodenGear.copy());

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteAnimalFarm), "animal_husbandry",
                "___",
                "gwg",
                "_c_",
                '_', Blocks.planks,
                'w', Items.iron_sword,
                'c', Blocks.chest,
                'g', woodenGear.copy());

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteQuarry), "engineering",
                "___",
                "gwg",
                "_c_",
                '_', Blocks.planks,
                'w', Items.iron_pickaxe,
                'c', Blocks.chest,
                'g', woodenGear.copy());

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteForestry), "farming",
                "___",
                "gwg",
                "_c_",
                '_', Blocks.planks,
                'w', Items.iron_axe,
                'c', Blocks.chest,
                'g', woodenGear.copy());

        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.worksiteFishFarm), "fishing",
                "___",
                "gwg",
                "_c_",
                '_', Blocks.planks,
                'w', Items.fishing_rod,
                'c', Blocks.chest,
                'g', woodenGear.copy());

        //torque conduit s
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueConduit, 1, 0), "the_wheel",
                "_s_",
                "sgs",
                "_s_",
                's', woodShaft.copy(),
                'g', woodenGear.copy());

//torque conduit m
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueConduit, 1, 1), "the_wheel",
                "_s_",
                "sgs",
                "_s_",
                's', ironShaft.copy(),
                'g', ironGear.copy());

//torque conduit l
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueConduit, 1, 2), "mass_production",
                "_s_",
                "sgs",
                "_s_",
                's', steelShaft.copy(),
                'g', steelGear.copy());

        //torque distributor s
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueDistributor, 1, 0), "the_wheel",
                "_s_",
                "ggg",
                "_s_",
                's', woodShaft.copy(),
                'g', woodenGear.copy());

        //torque distributor m
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueDistributor, 1, 1), "the_wheel",
                "_s_",
                "ggg",
                "_s_",
                's', ironShaft.copy(),
                'g', ironGear.copy());

        //torque distributor l
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueDistributor, 1, 2), "mass_production",
                "_s_",
                "ggg",
                "_s_",
                's', steelShaft.copy(),
                'g', steelGear.copy());

        //torque shaft s
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueShaft, 1, 0), "the_wheel",
                "s",
                "s",
                "g",
                's', woodShaft.copy(),
                'g', woodenGear.copy());

//torque shaft m
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueShaft, 1, 1), "the_wheel",
                "s",
                "s",
                "g",
                's', ironShaft.copy(),
                'g', ironGear.copy());

//torque shaft l
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueShaft, 1, 2), "mass_production",
                "s",
                "s",
                "g",
                's', steelShaft.copy(),
                'g', steelGear.copy());

        //torque flywheel control s
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.flywheel, 1, 0), "theory_of_gravity",
                "pgp",
                "ppp",
                "pgp",
                'p', Blocks.planks,
                'g', woodenGear.copy());

        //torque flywheel control m
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.flywheel, 1, 1), "theory_of_gravity",
                "igi",
                "iii",
                "igi",
                'i', Items.iron_ingot,
                'g', ironGear.copy());

        //torque flywheel control l
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.flywheel, 1, 2), "mass_production",
                "igi",
                "iii",
                "igi",
                'i', AWItems.steel_ingot,
                'g', steelGear.copy());

        //torque generator hand
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.handCrankedEngine), "the_wheel",
                "igi",
                "gig",
                "iii",
                'i', Items.iron_ingot,
                'g', ironGear.copy());

        //torque generator waterwheel
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueGeneratorWaterwheel), "theory_of_gravity",
                "igi",
                "gwg",
                "iwi",
                'i', Blocks.planks,
                'g', ironGear.copy(),
                'w', woodenGear.copy());

        //torque generator sterling
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.torqueGeneratorSterling), "machinery",
                "iii",
                "ggg",
                "igi",
                'i', Items.iron_ingot,
                'g', ironGear.copy());

        //torque generator windmill control
        AWCraftingManager.INSTANCE.createRecipe(new ItemStack(AWAutomationBlockLoader.windmillControl), "the_wheel",
                "ppp",
                "gpg",
                "ppp",
                'i', Blocks.planks,
                'g', woodenGear.copy());

        ItemStack upgradeBoundsMed = new ItemStack(AWItems.worksiteUpgrade, 1, WorksiteUpgrade.SIZE_MEDIUM.flag());//engineering
        ItemStack upgradeBoundsLarge = new ItemStack(AWItems.worksiteUpgrade, 1, WorksiteUpgrade.SIZE_LARGE.flag());//mathematics
        ItemStack upgradeQuarryMed = new ItemStack(AWItems.worksiteUpgrade, 1, WorksiteUpgrade.QUARRY_MEDIUM.flag());//mining
        ItemStack upgradeQuarryLarge = new ItemStack(AWItems.worksiteUpgrade, 1, WorksiteUpgrade.QUARRY_LARGE.flag());//machinery
        ItemStack upgradeEnchantedTools1 = new ItemStack(AWItems.worksiteUpgrade, 1, WorksiteUpgrade.ENCHANTED_TOOLS_1.flag());//mathematics
        ItemStack upgradeEnchantedTools2 = new ItemStack(AWItems.worksiteUpgrade, 1, WorksiteUpgrade.ENCHANTED_TOOLS_2.flag());//mass production
        ItemStack upgradeQualityTools1 = new ItemStack(AWItems.worksiteUpgrade, 1, WorksiteUpgrade.TOOL_QUALITY_1.flag());//engineering
        ItemStack upgradeQualityTools2 = new ItemStack(AWItems.worksiteUpgrade, 1, WorksiteUpgrade.TOOL_QUALITY_2.flag());//construction
        ItemStack upgradeQualityTools3 = new ItemStack(AWItems.worksiteUpgrade, 1, WorksiteUpgrade.TOOL_QUALITY_3.flag());//refining
        ItemStack upgradeBasicChunkloader = new ItemStack(AWItems.worksiteUpgrade, 1, WorksiteUpgrade.BASIC_CHUNK_LOADER.flag());//mathematics
        ItemStack upgradeQuarryChunkloader = new ItemStack(AWItems.worksiteUpgrade, 1, WorksiteUpgrade.QUARRY_CHUNK_LOADER.flag());//machinery

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
                'p', Blocks.planks,
                'f', Blocks.fence);

        AWCraftingManager.INSTANCE.createRecipe(upgradeQuarryLarge, "machinery",
                "ppp",
                "fif",
                "fif",
                'p', Blocks.planks,
                'f', Blocks.fence,
                'i', Items.iron_ingot);

        ItemStack enchantedBook = new ItemStack(Items.enchanted_book, 1, 0);
        enchantedBook.addEnchantment(Enchantment.fortune, 1);

        AWCraftingManager.INSTANCE.createRecipe(upgradeEnchantedTools1, "mathematics",
                " b ",
                "iii",
                'b', enchantedBook,
                'i', Items.iron_ingot);

        enchantedBook = new ItemStack(Items.enchanted_book, 1, 0);
        enchantedBook.addEnchantment(Enchantment.fortune, 2);

        AWCraftingManager.INSTANCE.createRecipe(upgradeEnchantedTools2, "mass_production",
                "b b",
                "iii",
                'b', enchantedBook,
                'i', Items.iron_ingot);

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
                't', AWItems.steel_ingot,
                'o', Blocks.obsidian,
                'p', Items.diamond_pickaxe,
                's', Items.diamond_shovel,
                'a', Items.diamond_axe);

        AWCraftingManager.INSTANCE.createRecipe(upgradeBasicChunkloader, "mathematics",
                " i ",
                "ici",
                " i ",
                'i', Items.iron_ingot,
                'c', AWAutomationBlockLoader.chunkLoaderSimple);

        AWCraftingManager.INSTANCE.createRecipe(upgradeQuarryChunkloader, "machinery",
                " i ",
                "ici",
                " i ",
                'i', Items.iron_ingot,
                'c', AWAutomationBlockLoader.chunkLoaderDeluxe);

    }


}
