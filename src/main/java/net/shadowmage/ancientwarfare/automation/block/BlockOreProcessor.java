package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileOreProcessor;

public class BlockOreProcessor extends BlockWorksiteBase {

    public BlockOreProcessor(String regName) {
        super(regName);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileOreProcessor();
    }

    @Override
    public boolean shouldSideBeRendered(net.minecraft.world.IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isNormalCube() {
        return false;
    }

}
