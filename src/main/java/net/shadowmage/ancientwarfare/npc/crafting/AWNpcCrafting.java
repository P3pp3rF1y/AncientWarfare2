package net.shadowmage.ancientwarfare.npc.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = AncientWarfareNPC.modID)
public class AWNpcCrafting {

    @SubscribeEvent
    public static void register(RegistryEvent.Register<IRecipe> event) {
        IForgeRegistry<IRecipe> registry = event.getRegistry();
        registry.register(new OrderCopyingRecipe("upkeep_order_copy", AWItems.upkeepOrder));
        registry.register(new OrderCopyingRecipe("routing_order_copy", AWItems.routingOrder));
        registry.register(new OrderCopyingRecipe("combat_order_copy", AWItems.combatOrder));
        registry.register(new OrderCopyingRecipe("work_order_copy", AWItems.workOrder));
    }

    /*
     * load any recipes for automation module recipes
     */
    public static void loadRecipes() {
        //TODO JSON recipes
//        //worker spawner
//        AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("worker", ""), "leadership",
//                "gf",
//                "gt",
//                'f', "foodBundle",
//                't', Items.WOODEN_PICKAXE,
//                'g', "ingotGold");
//        //combat spawner
//        AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("combat", ""), "conscription",
//                "gf",
//                "gt",
//                'f', "foodBundle",
//                't', Items.WOODEN_SWORD,
//                'g', "ingotGold");
//        //courier bundle
//        AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("courier", ""), "trade",
//                "gf",
//                "gt",
//                'f', "foodBundle",
//                't', Blocks.WOOL,
//                'g', "ingotGold");
//        //trader spawner
//        AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("trader", ""), "trade",
//                "gf_",
//                "gtb",
//                'f', "foodBundle",
//                't', Blocks.WOOL,
//                'g', "ingotGold",
//                'b', Items.BOOK);
//        //priest spawner
//        AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("priest", ""), "leadership",
//                "gf",
//                "gb",
//                'f', "foodBundle",
//                'g', "ingotGold",
//                'b', Items.BOOK);
//        //bard spawner
//        AWCraftingManager.INSTANCE.createRecipe(ItemNpcSpawner.getStackForNpcType("bard", ""), "leadership",
//                "gf",
//                "gb",
//                'f', "foodBundle",
//                'g', "ingotGold",
//                'b', new ItemStack(AWNpcItemLoader.bardInstrument, 1, 0));
    }


    private static class OrderCopyingRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
        private final Item item;

        private OrderCopyingRecipe(String name, Item item) {
            setRegistryName(new ResourceLocation(AncientWarfareNPC.modID, name));
            this.item = item;
        }

        @Override
        public boolean matches(InventoryCrafting var1, World var2) {
            @Nonnull ItemStack order1 = ItemStack.EMPTY, order2 = ItemStack.EMPTY;
            boolean foundOtherStuff = false;
            @Nonnull ItemStack stack;
            for (int i = 0; i < var1.getSizeInventory(); i++) {
                stack = var1.getStackInSlot(i);
                if (stack.isEmpty()) {
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
            @Nonnull ItemStack order1 = ItemStack.EMPTY, order2 = ItemStack.EMPTY;
            boolean foundOtherStuff = false;
            @Nonnull ItemStack stack;
            for (int i = 0; i < var1.getSizeInventory(); i++) {
                stack = var1.getStackInSlot(i);
                if (stack.isEmpty()) {
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
            @Nonnull ItemStack retStack = order2.copy();
            if (order1.getTagCompound() != null) {
                retStack.setTagCompound(order1.getTagCompound().copy());
            } else {
                retStack.setTagCompound(null);
            }
            retStack.setCount(2);
            return retStack;
        }

        @Override
        public boolean canFit(int width, int height) {
            return width * height >= 2;
        }

        @Override
        public ItemStack getRecipeOutput() {
            return new ItemStack(item);
        }
    }
}
