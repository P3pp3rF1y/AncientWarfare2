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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.lang.reflect.Constructor;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class BlockWorksiteBase extends BlockBaseAutomation implements IRotatableBlock {

    public int maxWorkSize = 16;
    public int maxWorkSizeVertical = 1;
    private Constructor<? extends TileEntity> tile;

    public BlockWorksiteBase(String regName) {
        super(Material.WOOD, regName);
        setHardness(2.f);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);
        return te != null && te instanceof IRotatableTile ? state.withProperty(FACING, ((IRotatableTile) te).getPrimaryFacing()) : state;
    }

    public BlockWorksiteBase setWorkSize(int size) {
        this.maxWorkSize = size;
        return this;
    }

    public BlockWorksiteBase setWorkVerticalSize(int size) {
        this.maxWorkSizeVertical = size;
        return this;
    }

    public BlockWorksiteBase setTileEntity(Class<? extends TileEntity> clzz) {
        try {
            //TODO replace with factory method and ::new
            tile = clzz.getConstructor();
        } catch (Exception e) {
        }
        return this;
    }

    /*
     * made into a generic method so that farm blocks are easier to setup
     * returned tiles must implement IWorksite (for team reference) and IInteractableTile (for interaction callback) if they wish to receive onBlockActivated calls<br>
     * returned tiles must implement IBoundedTile if they want workbounds set from ItemBlockWorksite<br>
     * returned tiles must implement IOwnable if they want owner-name set from ItemBlockWorksite<br>
     */
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        try {
            return tile.newInstance();
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return tile != null;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof IInteractableTile) {
            boolean canClick = false;
            if(te instanceof IOwnable && ((IOwnable) te).isOwner(player))
                canClick = true;
            else if(te instanceof IWorkSite) {
                IWorkSite site = ((IWorkSite) te);
                if ((player.getTeam() != null) && (player.getTeam() == site.getTeam()))
                    canClick = true;
                //TODO ftbutils integration
//                if (ModAccessors.FTBU.areFriends(player.getName(), site.getOwnerName()))
//                    canClick = true;
            }
            if (canClick) {
                return ((IInteractableTile) te).onBlockClicked(player, hand);
            }
        }
        return false;
    }

    @Override
    public final RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public boolean invertFacing() {
        return true;
    }

    @Override
    public final boolean rotateBlock(World world, BlockPos pos, EnumFacing facing) {
        if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof IRotatableTile) {
                if(!world.isRemote) {
                    EnumFacing o = ((IRotatableTile) te).getPrimaryFacing().rotateAround(facing.getAxis());
                    ((IRotatableTile) te).setPrimaryFacing(o);//twb will send update packets / etc
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public final EnumFacing[] getValidRotations(World world, BlockPos pos) {
        return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP};
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof IInventory) {
            InventoryTools.dropInventoryInWorld(world, (IInventory) te, pos);
        }
        if (te instanceof IWorkSite) {
            ((IWorkSite) te).onBlockBroken();
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 5;
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 20;
    }
}
