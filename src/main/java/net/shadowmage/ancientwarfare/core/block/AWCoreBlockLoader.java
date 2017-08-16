package net.shadowmage.ancientwarfare.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.item.ItemBlockRotatableMetaTile;
import net.shadowmage.ancientwarfare.core.tile.TileEngineeringStation;
import net.shadowmage.ancientwarfare.core.tile.TileResearchStation;

public class AWCoreBlockLoader {

    public static final AWCoreBlockLoader INSTANCE = new AWCoreBlockLoader();

    private AWCoreBlockLoader() {
    }

    public static final CreativeTabs coreTab = new CreativeTabs("tabs.awcore") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return AWItems.researchBook;
        }
    };

    public void load() {
        AWBlocks.engineeringStation = register(new BlockEngineeringStation(), "engineering_station", ItemBlockRotatableMetaTile.class, TileEngineeringStation.class);

        AWBlocks.researchStation = register(new BlockResearchStation(), "research_station", ItemBlockRotatableMetaTile.class, TileResearchStation.class);
    }

    public Block register(Block block, String name) {
        return GameRegistry.registerBlock(block, name);
    }

    public Block register(Block block, String name, Class<? extends ItemBlock> clazz) {
        return GameRegistry.registerBlock(block, clazz, name);
    }

    public Block register(Block block, String name, Class<? extends ItemBlock> blockItem, Class<? extends TileEntity> blockTile) {
        block.setBlockName(name);
        GameRegistry.registerTileEntity(blockTile, name + "_tile");
        return GameRegistry.registerBlock(block, blockItem, name);
    }
}
