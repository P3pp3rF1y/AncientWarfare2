package net.shadowmage.ancientwarfare.npc.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.util.InjectionTools;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.item.ItemBardInstrument;
import net.shadowmage.ancientwarfare.npc.item.ItemClub;
import net.shadowmage.ancientwarfare.npc.item.ItemCoin;
import net.shadowmage.ancientwarfare.npc.item.ItemCombatOrder;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.item.ItemExtendedReachWeapon;
import net.shadowmage.ancientwarfare.npc.item.ItemFoodBundle;
import net.shadowmage.ancientwarfare.npc.item.ItemIceSpear;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;
import net.shadowmage.ancientwarfare.npc.item.ItemPitchfork;
import net.shadowmage.ancientwarfare.npc.item.ItemRoutingOrder;
import net.shadowmage.ancientwarfare.npc.item.ItemScythe;
import net.shadowmage.ancientwarfare.npc.item.ItemShield;
import net.shadowmage.ancientwarfare.npc.item.ItemSickle;
import net.shadowmage.ancientwarfare.npc.item.ItemTradeOrder;
import net.shadowmage.ancientwarfare.npc.item.ItemUpkeepOrder;
import net.shadowmage.ancientwarfare.npc.item.ItemWorkOrder;
import net.shadowmage.ancientwarfare.npc.registry.FactionDefinition;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;
import net.shadowmage.ancientwarfare.structure.block.BlockProtectionFlag;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockColored;

import java.util.Map;

@ObjectHolder(AncientWarfareNPC.MOD_ID)
@Mod.EventBusSubscriber(modid = AncientWarfareNPC.MOD_ID)
public class AWNPCItems {
	private AWNPCItems() {}

	public static final ItemCommandBaton IRON_COMMAND_BATON = InjectionTools.nullValue();
	public static final ItemShield WOODEN_SHIELD = InjectionTools.nullValue();
	public static final Item NPC_SPAWNER = InjectionTools.nullValue();
	public static final Item WORK_ORDER = InjectionTools.nullValue();
	public static final Item UPKEEP_ORDER = InjectionTools.nullValue();
	public static final Item COMBAT_ORDER = InjectionTools.nullValue();
	public static final Item ROUTING_ORDER = InjectionTools.nullValue();
	public static final Item TRADE_ORDER = InjectionTools.nullValue();
	public static final Item BARD_INSTRUMENT = InjectionTools.nullValue();
	public static final Item COIN = InjectionTools.nullValue();

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		registry.register(new ItemCommandBaton("wooden_command_baton", Item.ToolMaterial.WOOD));
		registry.register(new ItemCommandBaton("stone_command_baton", Item.ToolMaterial.STONE));
		registry.register(new ItemCommandBaton("iron_command_baton", Item.ToolMaterial.IRON));
		registry.register(new ItemCommandBaton("gold_command_baton", Item.ToolMaterial.GOLD));
		registry.register(new ItemCommandBaton("diamond_command_baton", Item.ToolMaterial.DIAMOND));

		registry.register(new ItemBardInstrument("bard_instrument"));

		registry.register(new ItemShield("wooden_shield", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("stone_shield", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("iron_shield", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("gold_shield", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("diamond_shield", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("shield_tribal_1", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("shield_tribal_2", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("shield_round_1", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("shield_round_2", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("shield_round_3", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("shield_round_4", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("shield_round_5", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("shield_round_6", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("shield_witchbane_1", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("shield_witchbane_2", Item.ToolMaterial.WOOD));

		registry.register(new ItemWorkOrder());
		registry.register(new ItemUpkeepOrder());
		registry.register(new ItemCombatOrder());
		registry.register(new ItemRoutingOrder());
		registry.register(new ItemTradeOrder());
		registry.register(new ItemNpcSpawner());

		registry.register(new ItemCoin());
		registry.register(new ItemSickle(Item.ToolMaterial.IRON, -2.3F));
		registry.register(new ItemPitchfork(Item.ToolMaterial.IRON, -2.3F));
		registry.register(new ItemScythe(Item.ToolMaterial.IRON, "scythe", 0.0F, -2.3F));
		registry.register(new ItemScythe(Item.ToolMaterial.DIAMOND, "death_scythe", 0.0F, -2.3F));
		registry.register(new ItemClub(Item.ToolMaterial.DIAMOND, "giant_club", 3.5, -3.6D, 4.2F));
		registry.register(new ItemIceSpear(Item.ToolMaterial.DIAMOND,"ice_spear", 2, -3, 4.2F));

		ItemFoodBundle bundle = new ItemFoodBundle();
		registry.register(bundle);
		OreDictionary.registerOre("foodBundle", bundle);

		registerExtendedReachWeapons(registry, "spear", 2, -3, 4.2F);
		registerExtendedReachWeapons(registry, "halberd", 3, -3.2D, 4.5F);
		registerExtendedReachWeapons(registry, "lance", 2.5D, -3.2D, 5.5F);
		registerExtendedReachWeapons(registry, "cleaver", 3.5D, -2.8D, 3.0F);

		registerUniqueExtendedReachWeapon(registry, "obsidian_spear", 2, -3, 4.2F);
	}

	private static void registerExtendedReachWeapons(IForgeRegistry<Item> registry, String name, double attackOffset, double attackSpeed, float reach) {
		registry.register(new ItemExtendedReachWeapon(Item.ToolMaterial.WOOD, "wooden_" + name, attackOffset, attackSpeed, reach));
		registry.register(new ItemExtendedReachWeapon(Item.ToolMaterial.STONE, "stone_" + name, attackOffset, attackSpeed, reach));
		registry.register(new ItemExtendedReachWeapon(Item.ToolMaterial.IRON, "iron_" + name, attackOffset, attackSpeed, reach));
		registry.register(new ItemExtendedReachWeapon(Item.ToolMaterial.GOLD, "golden_" + name, attackOffset, attackSpeed, reach));
		registry.register(new ItemExtendedReachWeapon(Item.ToolMaterial.DIAMOND, "diamond_" + name, attackOffset, attackSpeed, reach));
	}

	private static void registerUniqueExtendedReachWeapon(IForgeRegistry<Item> registry, String name, double attackOffset, double attackSpeed, float reach) {
		registry.register(new ItemExtendedReachWeapon(Item.ToolMaterial.DIAMOND, name, attackOffset, attackSpeed, reach));
	}

	public static void addFactionBlocks() {
		for (FactionDefinition definition : FactionRegistry.getFactionDefinitions()) {
			AWStructureBlocks.PROTECTION_FLAG.addFlagDefinition(new BlockProtectionFlag.FlagDefinition(
					definition.getName(), 0xEF5757, definition.getColor()));

			for (Map.Entry<String, NBTTagCompound> blockData : definition.getThemedBlocksTags().entrySet()) {
				Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockData.getKey()));
				if (block == null) {
					AncientWarfareNPC.LOG.warn("Can't find block with registry name {} in block registry, skipping...", blockData.getKey());
					continue;
				}
				Item itemBlock = Item.getItemFromBlock(block);
				if (itemBlock instanceof ItemBlockColored) {
					blockData.getValue().setString("unlocalizedNamePart", "faction");
					((ItemBlockColored) itemBlock).addCustomItemTag(blockData.getValue());
				}
			}
		}
	}
}
