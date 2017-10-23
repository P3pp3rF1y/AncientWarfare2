package net.shadowmage.ancientwarfare.automation.block;

import codechicken.lib.model.bakery.ModelBakery;
import com.google.common.collect.ImmutableMap;
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
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

import java.util.Optional;

public abstract class BlockTorqueBase extends BlockBaseAutomation implements IRotatableBlock {

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
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
        addProperties(builder);

        return builder
                .add(CoreProperties.UNLISTED_FACING,
                        AutomationProperties.ACTIVE,
                        AutomationProperties.DYNAMIC,
                        AutomationProperties.ROTATIONS[0],
                        AutomationProperties.ROTATIONS[1],
                        AutomationProperties.ROTATIONS[2],
                        AutomationProperties.ROTATIONS[3],
                        AutomationProperties.ROTATIONS[4],
                        AutomationProperties.ROTATIONS[5])
                .build();
    }

    protected void addProperties(BlockStateContainer.Builder builder) {

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
        EnumFacing rotatedFacing = facing;
        if (axis.getAxis() == EnumFacing.Axis.Y || getRotationType() == BlockRotationHandler.RotationType.SIX_WAY) {
            rotatedFacing = facing.rotateAround(axis.getAxis());
        }
        if (facing != rotatedFacing) {
            tt.setPrimaryFacing(rotatedFacing);
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

    @Override
    public void registerClient() {
        ModelLoaderHelper.registerItem(this, "automation", "normal");

        ModelBakery.registerBlockKeyGenerator(this, state -> state.getBlock().getRegistryName().toString()
                + "," + state.getValue(CoreProperties.UNLISTED_FACING).toString()
                + "," + state.getValue(AutomationProperties.DYNAMIC)
                + getRotationKeyPart(state)
        );
    }

    protected String getRotationKeyPart(IExtendedBlockState state) {
        ImmutableMap<IUnlistedProperty<?>, Optional<?>> properties = state.getUnlistedProperties();
        StringBuilder ret = new StringBuilder();
        for(EnumFacing facing : EnumFacing.VALUES) {
            if (properties.containsKey(AutomationProperties.ROTATIONS[facing.ordinal()])) {
                ret.append(",");
                ret.append(String.format("%.6f", state.getValue(AutomationProperties.ROTATIONS[facing.ordinal()])));
            }
        }

        return ret.toString();
    }
}
