package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;

import java.util.function.Supplier;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class BlockWorksiteBase extends BlockBaseAutomation implements IRotatableBlock {

	public int maxWorkSize = 16;
	public int maxWorkSizeVertical = 1;
	private Supplier<TileEntity> renderFactory;

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
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world instanceof ChunkCache ? ((ChunkCache) world).getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK) : world.getTileEntity(pos);
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

	public BlockWorksiteBase setTileFactory(Supplier<TileEntity> renderFactory) {
		this.renderFactory = renderFactory;
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
		return renderFactory.get();
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return renderFactory != null;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY,
			float hitZ) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IInteractableTile) {
			boolean canClick = false;
			if (te instanceof IOwnable && ((IOwnable) te).isOwner(player))
				canClick = true;
			else if (te instanceof IWorkSite) {
				IWorkSite site = ((IWorkSite) te);
				if ((player.getTeam() != null) && (player.getTeam() == site.getTeam()))
					canClick = true;
				if (ModAccessors.FTBU.areTeamMates(player.getUniqueID(), site.getOwnerUuid()))
					canClick = true;
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
				if (!world.isRemote) {
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
		return new EnumFacing[] {EnumFacing.DOWN, EnumFacing.UP};
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
