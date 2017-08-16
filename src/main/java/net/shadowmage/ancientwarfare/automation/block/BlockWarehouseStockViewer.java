package net.shadowmage.ancientwarfare.automation.block;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockViewer;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;

import java.util.List;

public class BlockWarehouseStockViewer extends Block implements IRotatableBlock {

    public BlockWarehouseStockViewer(String regName) {
        super(Material.ROCK);
        this.setUnlocalizedName(regName);
        this.setCreativeTab(AWAutomationItemLoader.automationTab);
        setHardness(2.f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_) {

    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean isBlockSolid(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean invertFacing() {
        return true;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void addCollisionBoxesToList(World p_149743_1_, int p_149743_2_, int p_149743_3_, int p_149743_4_, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity p_149743_7_) {
        //noop for no collisions
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        EnumFacing d = EnumFacing.getOrientation(meta).getOpposite();
        float wmin = 0.125f;
        float wmax = 0.875f;
        float hmin = 0.375f;
        float hmax = 0.875f;
        switch (d) {
            case EAST: {
                setBlockBounds(wmax, hmin, 0, 1.f, hmax, 1);
            }
            break;
            case WEST: {
                setBlockBounds(0, hmin, 0, wmin, hmax, 1);
            }
            break;
            case NORTH: {
                setBlockBounds(0, hmin, 0, 1, hmax, wmin);
            }
            break;
            case SOUTH: {
                setBlockBounds(0, hmin, wmax, 1, hmax, 1);
            }
            break;
            default: {
                setBlockBounds(0, 0, 0, 1, 1, 1);
            }
            break;
        }
    }

    @Override
    public void setBlockBoundsForItemRender() {
        setBlockBounds(0.875f, 0.375f, 0.f, 1.f, 0.875f, 1.f);
    }

    @Override
    public BlockWarehouseStockViewer setIcon(RelativeSide side, String texName) {
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.planks.getIcon(0, 0);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileWarehouseStockViewer();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof IInteractableTile && ((IInteractableTile) te).onBlockClicked(player);
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int a, int b) {
        super.onBlockEventReceived(world, x, y, z, a, b);
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(a, b);
    }

}
