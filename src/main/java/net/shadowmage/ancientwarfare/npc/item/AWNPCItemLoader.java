package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.item.ItemComponent;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

import java.util.Comparator;

@Mod.EventBusSubscriber(modid = AncientWarfareNPC.modID)
public class AWNPCItemLoader {

	public static final CreativeTabs npcTab = new CreativeTabs("tabs.npc") {
		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem() {
			return new ItemStack(AWNPCItems.npcSpawner);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void displayAllRelevantItems(NonNullList<ItemStack> items) {
			super.displayAllRelevantItems(items);
			items.sort(sorter);
		}
	};

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		registry.register(new ItemCommandBaton("wooden_command_baton", ToolMaterial.WOOD));
		registry.register(new ItemCommandBaton("stone_command_baton", ToolMaterial.STONE));
		registry.register(new ItemCommandBaton("iron_command_baton", ToolMaterial.IRON));
		registry.register(new ItemCommandBaton("gold_command_baton", ToolMaterial.GOLD));
		registry.register(new ItemCommandBaton("diamond_command_baton", ToolMaterial.DIAMOND));

		registry.register(new ItemBardInstrument("bard_instrument"));

		registry.register(new ItemShield("wooden_shield", ToolMaterial.WOOD));
		registry.register(new ItemShield("stone_shield", ToolMaterial.WOOD));
		registry.register(new ItemShield("iron_shield", ToolMaterial.WOOD));
		registry.register(new ItemShield("gold_shield", ToolMaterial.WOOD));
		registry.register(new ItemShield("diamond_shield", ToolMaterial.WOOD));

		registry.register(new ItemNpcSpawner());
		registry.register(new ItemWorkOrder());
		registry.register(new ItemUpkeepOrder());
		registry.register(new ItemCombatOrder());
		registry.register(new ItemRoutingOrder());
		registry.register(new ItemTradeOrder());

		AWItems.componentItem.addSubItem(ItemComponent.NPC_FOOD_BUNDLE, "npc/component#variant=food_bundle", "foodBundle");
	}

	private static final Comparator<ItemStack> sorter = new Comparator<ItemStack>() {

		@Override
		public int compare(ItemStack arg0, ItemStack arg1) {
			Item i1 = arg0.getItem();
			Item i2 = arg1.getItem();
			int i1p = getItemPriority(i1);
			int i2p = getItemPriority(i2);
			if (i1p == i2p) {
				if (i1 == AWNPCItems.npcSpawner && i2 == AWNPCItems.npcSpawner) {
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
			if (f1 == f2) {
				return s1.compareTo(s2);
			} else {
				return f1 ? 1 : -1;
			}
		}

		private int getItemPriority(Item item) {
			if (item instanceof ItemNpcSpawner) {
				return 4;
			} else if (item == AWNPCItems.bardInstrument) {
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
