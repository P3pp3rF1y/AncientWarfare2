package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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

@Mod.EventBusSubscriber(modid = AncientWarfareStructures.modID)
public class AWStructuresBlockLoader {

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(new ItemBlockAdvancedSpawner(AWStructuresBlocks.advancedSpawner));
        registry.register(new ItemBlock(AWStructuresBlocks.gateProxy).setRegistryName(AWStructuresBlocks.gateProxy.getRegistryName()));
        registry.register(new ItemBlock(AWStructuresBlocks.draftingStation).setRegistryName(AWStructuresBlocks.draftingStation.getRegistryName()));
        registry.register(new ItemBlockStructureBuilder(AWStructuresBlocks.builderBlock));
        registry.register(new ItemBlock(AWStructuresBlocks.soundBlock).setRegistryName(AWStructuresBlocks.soundBlock.getRegistryName()));
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(new BlockAdvancedSpawner());
        GameRegistry.registerTileEntity(TileAdvancedSpawner.class, "advanced_spawner_tile");

        registry.register(new BlockGateProxy());
        GameRegistry.registerTileEntity(TEGateProxy.class, "gate_proxy_tile");

        registry.register(new BlockDraftingStation());
        GameRegistry.registerTileEntity(TileDraftingStation.class, "drafting_station_tile");

        registry.register(new BlockStructureBuilder());
        GameRegistry.registerTileEntity(TileStructureBuilder.class, "structure_builder_ticked_tile");

        registry.register(new BlockSoundBlock());
        GameRegistry.registerTileEntity(TileSoundBlock.class, "sound_block_tile");
    }
}
