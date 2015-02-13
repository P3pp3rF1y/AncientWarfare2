package net.shadowmage.ancientwarfare.structure.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class AWStructuresItemLoader {

    public static final CreativeTabs structureTab = new CreativeTabs("tabs.structures") {
        private Item cache;
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            if(cache == null)
                cache = GameRegistry.findItem("AncientWarfareStructure", "structure_scanner");
            return cache;
        }
    };

    public static ItemGateSpawner gateSpawner;

    public static void load() {
        GameRegistry.registerItem(new ItemStructureScanner("structure_scanner"), "structure_scanner");
        GameRegistry.registerItem(new ItemStructureBuilder("structure_builder"), "structure_builder");
        GameRegistry.registerItem(new ItemStructureBuilderWorldGen("structure_builder_world_gen"), "structure_builder_world_gen");
        GameRegistry.registerItem(new ItemTownBuilder("town_builder"), "town_builder");
        GameRegistry.registerItem(new ItemSpawnerPlacer("spawner_placer"), "spawner_placer");
        GameRegistry.registerItem(gateSpawner = new ItemGateSpawner("gate_spawner"), "gate_spawner");
        GameRegistry.registerItem(new ItemConstructionTool("construction_tool"), "construction_tool");
        GameRegistry.registerItem(new ItemConstructionToolLakes("construction_tool_lakes"), "construction_tool_lakes");
        GameRegistry.registerItem(new ItemBlockInfo("block_info_clicker"), "block_info_clicker");
    }

}
