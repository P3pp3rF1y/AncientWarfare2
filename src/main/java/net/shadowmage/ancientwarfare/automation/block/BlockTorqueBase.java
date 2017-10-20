package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.Block;
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
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public abstract class BlockTorqueBase extends BlockBaseAutomation implements IRotatableBlock {

    //HashMap<Integer, IconRotationMap> iconMaps = new HashMap<>();

    protected BlockTorqueBase(Material material, String regName) {
        super(material, regName);
        setHardness(2.f);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof IInteractableTile && ((IInteractableTile) te).onBlockClicked(player, hand);
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(CoreProperties.UNLISTED_FACING).build();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        EnumFacing facing = EnumFacing.NORTH;
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof TileTorqueBase) {
            facing = ((TileTorqueBase) tileentity).getPrimaryFacing();
        }

        return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(CoreProperties.UNLISTED_FACING, facing);
    }

/*
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        for (IconRotationMap map : this.iconMaps.values()) {
            map.registerIcons(register);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess block, int x, int y, int z, int side) {
        IRotatableTile tt = (IRotatableTile) block.getTileEntity(x, y, z);
        int meta = 2;
        if (tt != null) {
            EnumFacing d = tt.getPrimaryFacing();
            if (d != null) {
                meta = d.ordinal();
            }
        }
        IconRotationMap icr = iconMaps.get(meta);
        if (icr != null) {
            return icr.getIcon(this, meta, side);
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        IconRotationMap icr = iconMaps.get(meta);
        if (icr != null) {
            return icr.getIcon(this, meta, side);
        }
        return null;
    }

    @Override
    public BlockTorqueBase setIcon(RelativeSide side, String texName) {
        throw new UnsupportedOperationException("Cannot set side icons directly on torque block, need to use meta-sensitive version");
    }

    public BlockTorqueBase setIcon(int meta, RelativeSide side, String texName) {
        if (!this.iconMaps.containsKey(meta)) {
            this.iconMaps.put(meta, new IconRotationMap());
        }
        iconMaps.get(meta).setIcon(this, side, texName);
        return this;
    }

    public IIcon getIcon(int meta, RelativeSide side) {
        return iconMaps.get(meta).getIcon(side);
    }

*/

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileTorqueBase) {
            ((TileTorqueBase) te).onNeighborTileChanged();
        }
        super.onNeighborChange(world, pos, neighbor);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileTorqueBase) {
            ((TileTorqueBase) te).onNeighborTileChanged();
        }
        super.neighborChanged(state, world, pos, block, fromPos);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        if (world.isRemote) { //TODO is this needed or it's always server side?
            return false;
        }
        IRotatableTile tt = (IRotatableTile) world.getTileEntity(pos);
        EnumFacing facing = tt.getPrimaryFacing();
        EnumFacing rotatedFacing = facing.rotateAround(axis.getAxis());
        if (facing != rotatedFacing) {
            tt.setPrimaryFacing(rotatedFacing);
            BlockTools.notifyBlockUpdate(world, pos);
            return true;
        }
        return false;
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
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

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }
}
