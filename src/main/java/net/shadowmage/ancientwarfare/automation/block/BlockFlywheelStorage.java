package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileFlywheelStorage;

public class BlockFlywheelStorage extends Block {

    public BlockFlywheelStorage(String regName) {
        super(Material.ROCK);
        this.setUnlocalizedName(regName);
        this.setCreativeTab(AWAutomationItemLoader.automationTab);
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
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

/*
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        switch (meta) {
            case 0: {
                return Blocks.planks.getIcon(side, 0);
            }
            case 1: {
                return Blocks.iron_block.getIcon(side, 0);
            }
            case 2: {
                //TODO change this to steel block icon...once I make a steel block...
                return Blocks.iron_block.getIcon(side, 0);
            }
        }
        return Blocks.iron_block.getIcon(side, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
    }

*/

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileFlywheelStorage te = (TileFlywheelStorage) world.getTileEntity(pos);
        te.blockPlaced();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileFlywheelStorage te = (TileFlywheelStorage) world.getTileEntity(pos);
        super.breakBlock(world, pos, state);
        te.blockBroken();//have to call post block-break so that the controller properly sees the block as gone //TODO this should probably be invalidate
    }

    @Override
    public int damageDropped(IBlockState state) {
        return meta;// needs property, but which one - just the type one from BlockFlyWheel??
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileFlywheelStorage();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public void getSubBlocks(CreativeTabs creativeTab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(Item.getItemFromBlock(this), 1, 0));
        list.add(new ItemStack(Item.getItemFromBlock(this), 1, 1));
        list.add(new ItemStack(Item.getItemFromBlock(this), 1, 2));
    }

}
