package net.shadowmage.ancientwarfare.core.init;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;
import net.shadowmage.ancientwarfare.core.item.ItemBaseCore;
import net.shadowmage.ancientwarfare.core.item.ItemComponent;
import net.shadowmage.ancientwarfare.core.item.ItemHammer;
import net.shadowmage.ancientwarfare.core.item.ItemInfoTool;
import net.shadowmage.ancientwarfare.core.item.ItemManual;
import net.shadowmage.ancientwarfare.core.item.ItemMulti;
import net.shadowmage.ancientwarfare.core.item.ItemQuill;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.item.ItemResearchNotes;
import net.shadowmage.ancientwarfare.core.util.InjectionTools;

@SuppressWarnings("WeakerAccess") //public static final is needed for ObjectHolders to work
@ObjectHolder(AncientWarfareCore.MOD_ID)
@Mod.EventBusSubscriber(modid = AncientWarfareCore.MOD_ID)
public class AWCoreItems {
	public static final ItemInfoTool INFO_TOOL = InjectionTools.nullValue();
	public static final Item IRON_HAMMER = InjectionTools.nullValue();
	public static final Item MANUAL = InjectionTools.nullValue();

	private AWCoreItems() {}

	public static final Item RESEARCH_BOOK = InjectionTools.nullValue();
	public static final Item BACKPACK = InjectionTools.nullValue();
	public static final Item IRON_QUILL = InjectionTools.nullValue();
	public static final Item STEEL_INGOT = InjectionTools.nullValue();

	public static void load() {
		OreDictionary.registerOre("ingotSteel", STEEL_INGOT);
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		registry.register(new ItemInfoTool());
		registry.register(new ItemResearchBook());

		registry.register(new ItemResearchNotes());

		registry.register(new ItemBackpack());

		registry.register(new ItemQuill("wooden_quill", Item.ToolMaterial.WOOD));
		registry.register(new ItemQuill("stone_quill", Item.ToolMaterial.STONE));
		registry.register(new ItemQuill("iron_quill", Item.ToolMaterial.IRON));
		registry.register(new ItemQuill("gold_quill", Item.ToolMaterial.GOLD));
		registry.register(new ItemQuill("diamond_quill", Item.ToolMaterial.DIAMOND));

		registry.register(new ItemHammer("wooden_hammer", Item.ToolMaterial.WOOD));
		registry.register(new ItemHammer("stone_hammer", Item.ToolMaterial.STONE));
		registry.register(new ItemHammer("iron_hammer", Item.ToolMaterial.IRON));
		registry.register(new ItemHammer("gold_hammer", Item.ToolMaterial.GOLD));
		registry.register(new ItemHammer("diamond_hammer", Item.ToolMaterial.DIAMOND));

		registry.register(new ItemManual());

		//TODO break this out of here and replace with separate items in automation and NPCS / will need data fixer
		ItemMulti component = new ItemComponent().listenToProxy(AncientWarfareCore.proxy);
		registry.register(component);

		component.addSubItem(ItemComponent.WOODEN_GEAR_SET, "automation/component#variant=wooden_gear", "gearWood");
		component.addSubItem(ItemComponent.IRON_GEAR_SET, "automation/component#variant=iron_gear", "gearIron");
		component.addSubItem(ItemComponent.STEEL_GEAR_SET, "automation/component#variant=steel_gear", "gearSteel");
		component.addSubItem(ItemComponent.WOODEN_BEARINGS, "automation/component#variant=wooden_bearings", "bearingWood");
		component.addSubItem(ItemComponent.IRON_BEARINGS, "automation/component#variant=iron_bearings", "bearingIron");
		component.addSubItem(ItemComponent.STEEL_BEARINGS, "automation/component#variant=steel_bearings", "bearingSteel");
		component.addSubItem(ItemComponent.WOODEN_TORQUE_SHAFT, "automation/component#variant=wooden_shaft", "shaftWood");
		component.addSubItem(ItemComponent.IRON_TORQUE_SHAFT, "automation/component#variant=iron_shaft", "shaftIron");
		component.addSubItem(ItemComponent.STEEL_TORQUE_SHAFT, "automation/component#variant=steel_shaft", "shaftSteel");

		registry.register(new ItemBaseCore("steel_ingot"));
	}
}
