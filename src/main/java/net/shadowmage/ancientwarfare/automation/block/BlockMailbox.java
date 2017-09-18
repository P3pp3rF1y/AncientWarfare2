package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.TileMailbox;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import static net.shadowmage.ancientwarfare.core.render.BlockRenderProperties.FACING;

public class BlockMailbox extends BlockBaseAutomation implements IRotatableBlock {

//    IconRotationMap iconMap = new IconRotationMap();

    public BlockMailbox(String regName) {
        super(Material.ROCK, regName);
        setHardness(2.f);
//        String icon = "ancientwarfare:automation/"+regName;
//        setIcon(RelativeSide.TOP, icon);
//        setIcon(RelativeSide.FRONT, icon);
//        setIcon(RelativeSide.REAR, icon);
//        setIcon(RelativeSide.BOTTOM, icon);
//        setIcon(RelativeSide.LEFT, icon);
//        setIcon(RelativeSide.RIGHT, icon);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.VALUES[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).ordinal();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileMailbox();
    }

//    public BlockMailbox setIcon(RelativeSide relativeSide, String texName) {
//        this.iconMap.setIcon(this, relativeSide, texName+"_"+relativeSide);
//        return this;
//    }

/*
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        iconMap.registerIcons(p_149651_1_);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconMap.getIcon(this, meta, side);
    }

*/
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_MAILBOX_INVENTORY, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        if(world.isRemote)
            return false;
        IBlockState state = world.getBlockState(pos);
        EnumFacing facing = state.getValue(FACING);
        EnumFacing rotatedFacing = facing.rotateAround(axis.getAxis());
        if (facing != rotatedFacing) {
            world.setBlockState(pos, state.withProperty(FACING, rotatedFacing));
            return true;
        }
        return false;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public boolean invertFacing() {
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof IInventory) {
            InventoryTools.dropInventoryInWorld(world, (IInventory) te, pos);
        }
        super.breakBlock(world, pos, state);
    }

}
