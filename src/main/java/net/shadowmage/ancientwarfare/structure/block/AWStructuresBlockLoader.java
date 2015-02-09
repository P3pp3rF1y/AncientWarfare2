package net.shadowmage.ancientwarfare.structure.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemBlock;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockAdvancedSpawner;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockStructureBuilder;
import net.shadowmage.ancientwarfare.structure.tile.TEGateProxy;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;
import net.shadowmage.ancientwarfare.structure.tile.TileDraftingStation;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;

public class AWStructuresBlockLoader {

    public static void load() {
        AWBlocks.advancedSpawner = AWCoreBlockLoader.INSTANCE.register(new BlockAdvancedSpawner(), "advanced_spawner", ItemBlockAdvancedSpawner.class, TileAdvancedSpawner.class);

        AWBlocks.gateProxy = AWCoreBlockLoader.INSTANCE.register(new BlockGateProxy(), "gate_proxy", ItemBlock.class, TEGateProxy.class);

        BlockDraftingStation draftingStation = new BlockDraftingStation("drafting_station");
        AWBlocks.draftingStation = GameRegistry.registerBlock(draftingStation, "drafting_station");
        GameRegistry.registerTileEntity(TileDraftingStation.class, "drafting_station_tile");
        draftingStation.setIcon(0, 0, "ancientwarfare:structure/drafting_station_bottom");
        draftingStation.setIcon(0, 1, "ancientwarfare:structure/drafting_station_top");
        draftingStation.setIcon(0, 2, "ancientwarfare:structure/drafting_station_front");
        draftingStation.setIcon(0, 3, "ancientwarfare:structure/drafting_station_front");
        draftingStation.setIcon(0, 4, "ancientwarfare:structure/drafting_station_side");
        draftingStation.setIcon(0, 5, "ancientwarfare:structure/drafting_station_side");

        BlockStructureBuilder builder = new BlockStructureBuilder();
        AWBlocks.builderBlock = AWCoreBlockLoader.INSTANCE.register(builder, "structure_builder_ticked", ItemBlockStructureBuilder.class, TileStructureBuilder.class);
        builder.setIcon(0, 0, "ancientwarfare:structure/builder_bottom");
        builder.setIcon(0, 1, "ancientwarfare:structure/builder_top");
        builder.setIcon(0, 2, "ancientwarfare:structure/builder_side");
        builder.setIcon(0, 3, "ancientwarfare:structure/builder_side");
        builder.setIcon(0, 4, "ancientwarfare:structure/builder_side");
        builder.setIcon(0, 5, "ancientwarfare:structure/builder_side");

//  AWBlocks.soundBlock = new BlockSoundBlock("sound_block");
//  GameRegistry.registerBlock(AWBlocks.soundBlock, "sound_block");
//  GameRegistry.registerTileEntity(TileSoundBlock.class, "tile_sound_block");
    }

}
