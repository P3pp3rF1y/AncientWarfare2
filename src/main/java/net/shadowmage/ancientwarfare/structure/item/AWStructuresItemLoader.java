package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;

@Mod.EventBusSubscriber(modid = AncientWarfareStructures.MOD_ID)
public class AWStructuresItemLoader {

	public static final CreativeTabs structureTab = new CreativeTabs("tabs.structures") {
		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem() {
			return new ItemStack(AWItems.structureScanner);
		}
	};

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
	}
}
