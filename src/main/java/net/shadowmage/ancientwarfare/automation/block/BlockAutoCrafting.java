package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileAutoCrafting;

public class BlockAutoCrafting extends BlockWorksiteBase {

    public BlockAutoCrafting(Material p_i45394_1_, String regName) {
        super(p_i45394_1_, regName);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileAutoCrafting();
    }

    @Override
    public boolean shouldSideBeRendered(net.minecraft.world.IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {
        return false;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return Blocks.planks.getIcon(side, 0);
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
