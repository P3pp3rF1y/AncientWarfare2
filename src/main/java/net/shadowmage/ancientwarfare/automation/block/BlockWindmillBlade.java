package net.shadowmage.ancientwarfare.automation.block;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileWindmillBlade;

public class BlockWindmillBlade extends Block {

    public BlockWindmillBlade(String regName) {
        super(Material.wood);
        this.setUnlocalizedName(regName);
        this.setCreativeTab(AWAutomationItemLoader.automationTab);
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int a, int b) {
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(a, b);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(net.minecraft.world.IBlockAccess access, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.glass.getIcon(side, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
        super.onPostBlockPlaced(world, x, y, z, meta);
        TileWindmillBlade te = (TileWindmillBlade) world.getTileEntity(pos);
        te.blockPlaced();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int face) {
        TileWindmillBlade te = (TileWindmillBlade) world.getTileEntity(pos);
        super.breakBlock(world, x, y, z, block, face);
        te.blockBroken();//have to call post block-break so that the tile properly sees the block/tile as gone
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileWindmillBlade();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 60;
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 20;
    }
}
