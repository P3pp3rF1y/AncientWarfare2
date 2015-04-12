package net.shadowmage.ancientwarfare.structure.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.structure.gates.types.Gate;


public class AWStructureCrafting {

    /**
     * load any recipes for structure module recipes
     */
    public static void loadRecipes() {

        AWCraftingManager.INSTANCE.createRecipe(Gate.getItemToConstruct("gate.verticalWooden"), "construction",
                "sps",
                "sps",
                "srs",
                'p', "plankWood",
                'r', "dustRedstone",
                's', "stone");

        AWCraftingManager.INSTANCE.createRecipe(Gate.getItemToConstruct("gate.verticalIron"), "construction",
                "sps",
                "sps",
                "srs",
                'p', "ingotIron",
                'r', "dustRedstone",
                's', "stone");

        AWCraftingManager.INSTANCE.createRecipe(Gate.getItemToConstruct("gate.singleWood"), "construction",
                "sss",
                "ppp",
                "srs",
                'p', "plankWood",
                'r', "dustRedstone",
                's', "stone");

        AWCraftingManager.INSTANCE.createRecipe(Gate.getItemToConstruct("gate.singleIron"), "construction",
                "sss",
                "ppp",
                "srs",
                'p', "ingotIron",
                'r', "dustRedstone",
                's', "stone");

        AWCraftingManager.INSTANCE.createRecipe(Gate.getItemToConstruct("gate.doubleWood"), "construction",
                "sss",
                "ppp",
                "prp",
                'p', "plankWood",
                'r', "dustRedstone",
                's', "stone");

        AWCraftingManager.INSTANCE.createRecipe(Gate.getItemToConstruct("gate.doubleIron"), "construction",
                "sss",
                "ppp",
                "prp",
                'p', "ingotIron",
                'r', "dustRedstone",
                's', "stone");

        AWCraftingManager.INSTANCE.createRecipe(Gate.getItemToConstruct("gate.drawbridge"), "construction",
                "ppp",
                "ppp",
                "prp",
                'p', "plankWood",
                'r', "dustRedstone");
    }

}
