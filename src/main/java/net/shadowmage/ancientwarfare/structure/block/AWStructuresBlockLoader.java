package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructures;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockAdvancedSpawner;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockStructureBuilder;
import net.shadowmage.ancientwarfare.structure.tile.TEGateProxy;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;
import net.shadowmage.ancientwarfare.structure.tile.TileDraftingStation;
import net.shadowmage.ancientwarfare.structure.tile.TileSoundBlock;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;

import static net.shadowmage.ancientwarfare.structure.AncientWarfareStructures.MOD_PREFIX;

@Mod.EventBusSubscriber(modid = AncientWarfareStructures.MOD_ID)
public class AWStructuresBlockLoader {
	private AWStructuresBlockLoader() {
	}

	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		registry.register(new ItemBlockAdvancedSpawner(AWStructuresBlocks.advancedSpawner));
		//noinspection ConstantConditions
		registry.register(new ItemBlock(AWStructuresBlocks.gateProxy).setRegistryName(AWStructuresBlocks.gateProxy.getRegistryName()));
		//noinspection ConstantConditions
		registry.register(new ItemBlock(AWStructuresBlocks.draftingStation).setRegistryName(AWStructuresBlocks.draftingStation.getRegistryName()));
		registry.register(new ItemBlockStructureBuilder(AWStructuresBlocks.builderBlock));
		//noinspection ConstantConditions
		registry.register(new ItemBlock(AWStructuresBlocks.soundBlock).setRegistryName(AWStructuresBlocks.soundBlock.getRegistryName()));
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();

		registry.register(new BlockAdvancedSpawner());
		registerTile(TileAdvancedSpawner.class, "advanced_spawner_tile");

		registry.register(new BlockGateProxy());
		registerTile(TEGateProxy.class, "gate_proxy_tile");

		registry.register(new BlockDraftingStation());
		registerTile(TileDraftingStation.class, "drafting_station_tile");

		registry.register(new BlockStructureBuilder());
		registerTile(TileStructureBuilder.class, "structure_builder_ticked_tile");

		registry.register(new BlockSoundBlock());
		registerTile(TileSoundBlock.class, "sound_block_tile");
	}

	private static void registerTile(Class<? extends TileEntity> teClass, String teId) {
		GameRegistry.registerTileEntity(teClass, new ResourceLocation(MOD_PREFIX, teId));
	}
}
