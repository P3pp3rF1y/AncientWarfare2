package net.shadowmage.ancientwarfare.automation.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
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
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
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
