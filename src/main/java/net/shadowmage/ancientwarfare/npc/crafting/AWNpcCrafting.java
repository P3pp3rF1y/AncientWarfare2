package net.shadowmage.ancientwarfare.npc.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.item.ItemComponent;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;


public class AWNpcCrafting {

    /**
     * load any recipes for automation module recipes
     */
    @SuppressWarnings("unchecked")
    public static void loadRecipes() {
        RecipeSorter.register("ancientwarfare:order_copy", OrderCopyingRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
        CraftingManager.getInstance().getRecipeList().add(new OrderCopyingRecipe(AWItems.upkeepOrder));
        CraftingManager.getInstance().getRecipeList().add(new OrderCopyingRecipe(AWItems.routingOrder));
        CraftingManager.getInstance().getRecipeList().add(new OrderCopyingRecipe(AWItems.combatOrder));
        CraftingManager.getInstance().getRecipeList().add(new OrderCopyingRecipe(AWItems.workOrder));


        ItemStack foodBundle = new ItemStack(AWItems.componentItem, 1, ItemComponent.NPC_FOOD_BUNDLE);

        //food bundle
        AWCraftingManager.INSTANCE.createRecipe(foodBundle.copy(), "leadership",
                "ap",
                "bb",
                "wc",
                'a', Items.apple,
                'b', Items.bread,
                'w', new ItemStack(Items.potionitem),
                'p', Items.cooked_porkchop,
                'b', Items.cooked_beef,
                'c', Items.cooked_chicken);

        //worker spawner
        AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("worker", ""), "leadership",
                "gf",
                "gt",
                'f', foodBundle.copy(),
                't', Items.wooden_pickaxe,
                'g', Items.gold_ingot);
        //combat spawner
        AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("combat", ""), "conscription",
                "gf",
                "gt",
                'f', foodBundle.copy(),
                't', Items.wooden_sword,
                'g', Items.gold_ingot);
        //courier bundle
        AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("courier", ""), "trade",
                "gf",
                "gt",
                'f', foodBundle.copy(),
                't', Blocks.wool,
                'g', Items.gold_ingot);
        //trader spawner
        AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("trader", ""), "trade",
                "gf_",
                "gtb",
                'f', foodBundle.copy(),
                't', Blocks.wool,
                'g', Items.gold_ingot,
                'b', Items.book);
        //priest spawner
        AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("priest", ""), "leadership",
                "gf",
                "gb",
                'f', foodBundle.copy(),
                'g', Items.gold_ingot,
                'b', Items.book);
        //bard spawner
        AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("bard", ""), "leadership",
                "gf",
                "gb",
                'f', foodBundle.copy(),
                'g', Items.gold_ingot,
                'b', new ItemStack(AWNpcItemLoader.bardInstrument, 1, 0));
    }


    private static class OrderCopyingRecipe implements IRecipe {
        private final Item item;

        private OrderCopyingRecipe(Item item) {
            this.item = item;
        }

        @Override
        public boolean matches(InventoryCrafting var1, World var2) {
            ItemStack order1 = null, order2 = null;
            boolean foundOtherStuff = false;
            ItemStack stack;
            for (int i = 0; i < var1.getSizeInventory(); i++) {
                stack = var1.getStackInSlot(i);
                if (stack == null) {
                    continue;
                }
                if (stack.getItem() == item) {
                    if (order1 == null) {
                        order1 = stack;
                    } else if (order2 == null) {
                        order2 = stack;
                    } else {
                        foundOtherStuff = true;
                        break;
                    }
                } else {
                    foundOtherStuff = true;
                    break;
                }
            }
            return !foundOtherStuff && order1 != null && order2 != null;
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting var1) {
            ItemStack order1 = null, order2 = null;
            boolean foundOtherStuff = false;
            ItemStack stack;
            for (int i = 0; i < var1.getSizeInventory(); i++) {
                stack = var1.getStackInSlot(i);
                if (stack == null) {
                    continue;
                }
                if (stack.getItem() == item) {
                    if (order1 == null) {
                        order1 = stack;
                    } else if (order2 == null) {
                        order2 = stack;
                    } else {
                        foundOtherStuff = true;
                        break;
                    }
                } else {
                    foundOtherStuff = true;
                    break;
                }
            }
            if (foundOtherStuff || order1 == null || order2 == null) {
                return null;
            }
            ItemStack retStack = order2.copy();
            if (order1.stackTagCompound != null) {
                retStack.setTagCompound((NBTTagCompound) order1.stackTagCompound.copy());
            } else {
                retStack.setTagCompound(null);
            }
            retStack.stackSize = 2;
            return retStack;
        }

        @Override
        public int getRecipeSize() {
            return 2;
        }

        @Override
        public ItemStack getRecipeOutput() {
            return null;
        }
    }
}
