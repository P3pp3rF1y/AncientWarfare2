package net.shadowmage.ancientwarfare.npc.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.item.ItemComponent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AWNpcItemLoader {

    public static final CreativeTabs npcTab = new CreativeTabs("tabs.npc") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return npcSpawner;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public void displayAllReleventItems(List par1List) {
            super.displayAllReleventItems(par1List);
            Collections.sort(par1List, sorter);
        }
    };

    public static final ItemNpcSpawner npcSpawner = new ItemNpcSpawner("npc_spawner");
    public static final ItemWorkOrder workOrder = new ItemWorkOrder("work_order");
    public static final ItemUpkeepOrder upkeepOrder = new ItemUpkeepOrder("upkeep_order");
    public static final ItemCombatOrder combatOrder = new ItemCombatOrder("combat_order");
    public static final ItemRoutingOrder routingOrder = new ItemRoutingOrder("routing_order");
    public static final ItemTradeOrder tradeOrder = new ItemTradeOrder("trade_order");
    public static final ItemCommandBaton commandBatonWood = new ItemCommandBaton("wooden_command_baton", ToolMaterial.WOOD);
    public static final ItemCommandBaton commandBatonStone = new ItemCommandBaton("stone_command_baton", ToolMaterial.STONE);
    public static final ItemCommandBaton commandBatonIron = new ItemCommandBaton("iron_command_baton", ToolMaterial.IRON);
    public static final ItemCommandBaton commandBatonGold = new ItemCommandBaton("gold_command_baton", ToolMaterial.GOLD);
    public static final ItemCommandBaton commandBatonDiamond = new ItemCommandBaton("diamond_command_baton", ToolMaterial.EMERALD);
    public static final ItemBardInstrument bardInstrument = new ItemBardInstrument("bard_instrument");
    public static final ItemShield woodenShield = new ItemShield("wooden_shield", ToolMaterial.WOOD);
    public static final ItemShield stoneShield = new ItemShield("stone_shield", ToolMaterial.STONE);
    public static final ItemShield ironShield = new ItemShield("iron_shield", ToolMaterial.IRON);
    public static final ItemShield goldShield = new ItemShield("gold_shield", ToolMaterial.GOLD);
    public static final ItemShield diamondShield = new ItemShield("diamond_shield", ToolMaterial.EMERALD);

    public static void load() {
        GameRegistry.registerItem(npcSpawner, "npc_spawner");
        GameRegistry.registerItem(workOrder, "work_order");
        GameRegistry.registerItem(upkeepOrder, "upkeep_order");
        GameRegistry.registerItem(combatOrder, "combat_order");
        GameRegistry.registerItem(routingOrder, "routing_order");
        GameRegistry.registerItem(tradeOrder, "trade_order");
        GameRegistry.registerItem(commandBatonWood, "wooden_command_baton");
        GameRegistry.registerItem(commandBatonStone, "stone_command_baton");
        GameRegistry.registerItem(commandBatonIron, "iron_command_baton");
        GameRegistry.registerItem(commandBatonGold, "gold_command_baton");
        GameRegistry.registerItem(commandBatonDiamond, "diamond_command_baton");
        GameRegistry.registerItem(bardInstrument, "bard_instrument");

        GameRegistry.registerItem(woodenShield, "wooden_shield");
        GameRegistry.registerItem(stoneShield, "stone_shield");
        GameRegistry.registerItem(ironShield, "iron_shield");
        GameRegistry.registerItem(goldShield, "gold_shield");
        GameRegistry.registerItem(diamondShield, "diamond_shield");

        ((ItemComponent) AWItems.componentItem).addSubItem(ItemComponent.NPC_FOOD_BUNDLE, "ancientwarfare:npc/food_bundle");
    }

    private static final Comparator<ItemStack> sorter = new Comparator<ItemStack>() {

        @Override
        public int compare(ItemStack arg0, ItemStack arg1) {
            Item i1 = arg0.getItem();
            Item i2 = arg1.getItem();
            int i1p = getItemPriority(i1);
            int i2p = getItemPriority(i2);
            if (i1p == i2p) {
                if (i1 == npcSpawner && i2 == npcSpawner) {
                    return compareSpawnerStacks(arg0, arg1);
                }
                return arg0.getDisplayName().compareTo(arg1.getDisplayName());
            } else {
                return i1p < i2p ? -1 : 1;
            }
        }

        private int compareSpawnerStacks(ItemStack arg0, ItemStack arg1) {
            String s1 = arg0.getUnlocalizedName();
            String s2 = arg1.getUnlocalizedName();
            boolean f1 = s1.contains("bandit") || s1.contains("viking") || s1.contains("native") || s1.contains("desert") || s1.contains("pirate") || s1.contains("custom_1") || s1.contains("custom_2") || s1.contains("custom_3");
            boolean f2 = s2.contains("bandit") || s2.contains("viking") || s2.contains("native") || s2.contains("desert") || s2.contains("pirate") || s2.contains("custom_1") || s2.contains("custom_2") || s2.contains("custom_3");
            if (f1 && f2) {
                return s1.compareTo(s2);
            } else if (!f1 && !f2) {
                return s1.compareTo(s2);
            } else {
                return f1 ? 1 : -1;
            }
        }

        private int getItemPriority(Item item) {
            if (item == npcSpawner) {
                return 4;
            } else if (item == bardInstrument) {
                return 3;
            } else if (item instanceof ItemCommandBaton) {
                return 2;
            } else if (item instanceof ItemOrders) {
                return 1;
            } else {
                return 0;
            }
        }
    };

}
