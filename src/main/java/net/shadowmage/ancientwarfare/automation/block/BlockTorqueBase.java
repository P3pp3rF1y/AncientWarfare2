package net.shadowmage.ancientwarfare.automation.block;

import codechicken.lib.model.bakery.ModelBakery;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.render.BlockStateKeyGenerator;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public abstract class BlockTorqueBase extends BlockBaseAutomation implements IRotatableBlock {

	protected BlockTorqueBase(Material material, String regName) {
		super(material, regName);
		setHardness(2.f);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return WorldTools.clickInteractableTileWithHand(world, pos, player, hand);
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

		return builder.add(CoreProperties.UNLISTED_FACING, AutomationProperties.ACTIVE, AutomationProperties.DYNAMIC, AutomationProperties.ROTATIONS[0], AutomationProperties.ROTATIONS[1], AutomationProperties.ROTATIONS[2], AutomationProperties.ROTATIONS[3], AutomationProperties.ROTATIONS[4], AutomationProperties.ROTATIONS[5]).build();
	}

	protected void addProperties(BlockStateContainer.Builder builder) {

	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		EnumFacing facing = WorldTools.getTile(world, pos, TileTorqueBase.class).map(TileTorqueBase::getPrimaryFacing).orElse(EnumFacing.NORTH);
		return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(CoreProperties.UNLISTED_FACING, facing);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		WorldTools.getTile(world, pos, TileTorqueBase.class).ifPresent(TileTorqueBase::onNeighborTileChanged);
		super.onNeighborChange(world, pos, neighbor);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		WorldTools.getTile(world, pos, TileTorqueBase.class).ifPresent(TileTorqueBase::onNeighborTileChanged);
		super.neighborChanged(state, world, pos, block, fromPos);
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		if (world.isRemote) { //TODO is this needed or it's always server side?
			return false;
		}
		//noinspection ConstantConditions
		IRotatableTile tt = WorldTools.getTile(world, pos, IRotatableTile.class).get();
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
		return WorldTools.sendClientEventToTile(world, pos, id, param);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, "automation", "normal");

		ModelBakery.registerBlockKeyGenerator(this, new BlockStateKeyGenerator.Builder().addKeyProperties(CoreProperties.UNLISTED_FACING, AutomationProperties.DYNAMIC).addKeyProperties(o -> String.format("%.6f", o), AutomationProperties.ROTATIONS).build());
	}
}
