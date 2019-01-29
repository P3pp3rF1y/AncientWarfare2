package net.shadowmage.ancientwarfare.structure.init;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.util.InjectionTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockColored;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockInfo;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockStructureBuilder;
import net.shadowmage.ancientwarfare.structure.item.ItemConstructionTool;
import net.shadowmage.ancientwarfare.structure.item.ItemConstructionToolLakes;
import net.shadowmage.ancientwarfare.structure.item.ItemGateSpawner;
import net.shadowmage.ancientwarfare.structure.item.ItemLootChestPlacer;
import net.shadowmage.ancientwarfare.structure.item.ItemSpawnerPlacer;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureBuilder;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureBuilderWorldGen;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;
import net.shadowmage.ancientwarfare.structure.item.ItemTownBuilder;

@ObjectHolder(AncientWarfareStructure.MOD_ID)
@Mod.EventBusSubscriber(modid = AncientWarfareStructure.MOD_ID)
public class AWStructureItems {
	private AWStructureItems() {}

	public static final ItemGateSpawner GATE_SPAWNER = InjectionTools.nullValue();
	public static final Item STRUCTURE_SCANNER = InjectionTools.nullValue();
	public static final Item TOTEM_PART = InjectionTools.nullValue();
	public static final ItemBlockStructureBuilder STRUCTURE_BUILDER_TICKED = InjectionTools.nullValue();
	public static final ItemBlockColored ALTAR_CANDLE = InjectionTools.nullValue();
	public static final ItemBlockColored ALTAR_SHORT_CLOTH = InjectionTools.nullValue();
	public static final ItemBlockColored ALTAR_LONG_CLOTH = InjectionTools.nullValue();

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		registry.register(new ItemStructureScanner("structure_scanner"));
		registry.register(new ItemStructureBuilder("structure_builder"));
		registry.register(new ItemStructureBuilderWorldGen("structure_builder_world_gen"));
		registry.register(new ItemTownBuilder("town_builder"));
		registry.register(new ItemSpawnerPlacer("spawner_placer"));
		registry.register(new ItemGateSpawner("gate_spawner"));
		registry.register(new ItemConstructionTool("construction_tool"));
		registry.register(new ItemConstructionToolLakes("construction_tool_lakes"));
		registry.register(new ItemBlockInfo("block_info_clicker"));
		registry.register(new ItemLootChestPlacer());
	}
}
