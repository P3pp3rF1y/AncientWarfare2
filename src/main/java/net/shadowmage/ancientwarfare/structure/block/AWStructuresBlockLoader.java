package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.item.ItemBlock;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockAdvancedSpawner;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockStructureBuilder;
import net.shadowmage.ancientwarfare.structure.tile.*;

public class AWStructuresBlockLoader {

    public static void load() {
        AWBlocks.advancedSpawner = AWCoreBlockLoader.INSTANCE.register(new BlockAdvancedSpawner(), "advanced_spawner", ItemBlockAdvancedSpawner.class, TileAdvancedSpawner.class);

        AWBlocks.gateProxy = AWCoreBlockLoader.INSTANCE.register(new BlockGateProxy(), "gate_proxy", ItemBlock.class, TEGateProxy.class);

        BlockDraftingStation draftingStation = new BlockDraftingStation();
        AWBlocks.draftingStation = AWCoreBlockLoader.INSTANCE.register(draftingStation, "drafting_station", ItemBlock.class, TileDraftingStation.class);
        draftingStation.setIcon(0, "ancientwarfare:structure/drafting_station_bottom");
        draftingStation.setIcon(1, "ancientwarfare:structure/drafting_station_top");
        draftingStation.setIcon(2, "ancientwarfare:structure/drafting_station_front");
        draftingStation.setIcon(3, "ancientwarfare:structure/drafting_station_front");
        draftingStation.setIcon(4, "ancientwarfare:structure/drafting_station_side");
        draftingStation.setIcon(5, "ancientwarfare:structure/drafting_station_side");

        BlockStructureBuilder builder = new BlockStructureBuilder();
        AWBlocks.builderBlock = AWCoreBlockLoader.INSTANCE.register(builder, "structure_builder_ticked", ItemBlockStructureBuilder.class, TileStructureBuilder.class);
        builder.setIcon(0, "ancientwarfare:structure/builder_bottom");
        builder.setIcon(1, "ancientwarfare:structure/builder_top");
        builder.setIcon(2, "ancientwarfare:structure/builder_side");
        builder.setIcon(3, "ancientwarfare:structure/builder_side");
        builder.setIcon(4, "ancientwarfare:structure/builder_side");
        builder.setIcon(5, "ancientwarfare:structure/builder_side");

        AWBlocks.soundBlock = AWCoreBlockLoader.INSTANCE.register(new BlockSoundBlock(), "sound_block", ItemBlock.class, TileSoundBlock.class);
    }

}
