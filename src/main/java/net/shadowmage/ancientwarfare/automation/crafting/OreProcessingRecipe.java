package net.shadowmage.ancientwarfare.automation.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreProcessingRecipe {

    ItemStack input;
    ItemStack output;

    ItemStack[] oreDictInput;
    ItemStack[] oreDictOutput;

    public OreProcessingRecipe(ItemStack input, ItemStack output) {
        this.input = input;
        this.output = output;

        int[] inIds = OreDictionary.getOreIDs(input);
        int[] outIds = OreDictionary.getOreIDs(output);
    }

    boolean matches(ItemStack input) {
        return false;
    }

    ItemStack getOutput() {
        return output;
    }//TODO remap output dynamically depending upon ore dictionary entry?

}
