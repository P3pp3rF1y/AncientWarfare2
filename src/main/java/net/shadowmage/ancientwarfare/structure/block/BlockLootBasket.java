package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.tile.TileLootBasket;

import javax.annotation.Nullable;
import java.util.Optional;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class BlockLootBasket extends BlockBaseStructure {
	private static final PropertyBool DOUBLE = PropertyBool.create("double");
	private static final PropertyBool VISIBLE = PropertyBool.create("visible");

	public BlockLootBasket() {
		super(Material.GRASS, "loot_basket");
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, DOUBLE, VISIBLE);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return getDoubleDirection(worldIn, pos)
				.map(facing -> facing == EnumFacing.NORTH || facing == EnumFacing.WEST ?
						state.withProperty(DOUBLE, false).withProperty(VISIBLE, false) :
						state.withProperty(FACING, facing.rotateY()).withProperty(DOUBLE, true))
				.orElse(state.withProperty(DOUBLE, false));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex();
	}

	@Override
	public boolean isFullCube(IBlockState state) {
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
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileLootBasket();
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		int basketsAround = 0;
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			BlockPos offsetPos = pos.offset(facing);
			if (world.getBlockState(offsetPos).getBlock() == this) {
				if (isDoubleBasket(world, offsetPos)) {
					return false;
				}
				basketsAround++;
				if (basketsAround > 1) {
					return false;
				}
			}
		}

		return true;
	}

	private Optional<EnumFacing> getDoubleDirection(IBlockAccess world, BlockPos pos) {
		for (EnumFacing facing : EnumFacing.HORIZONTALS) {
			if (world.getBlockState(pos.offset(facing)).getBlock() == this) {
				return Optional.of(facing);
			}
		}
		return Optional.empty();
	}

	private boolean isDoubleBasket(World world, BlockPos pos) {
		return getDoubleDirection(world, pos).isPresent();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			playerIn.openGui();
			getContainer(worldIn, pos).ifPresent(playerIn::displayGUIChest);
		}
		return true;
	}

	public Optional<ILockableContainer> getContainer(World worldIn, BlockPos pos) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (!(tileentity instanceof TileLootBasket)) {
			return Optional.empty();
		} else {
			ILockableContainer container = (TileLootBasket) tileentity;

			for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
				BlockPos blockpos = pos.offset(enumfacing);
				Block block = worldIn.getBlockState(blockpos).getBlock();

				if (block == this) {
					TileEntity tile = worldIn.getTileEntity(blockpos);

					container = updateContainerIfDoubleBasket(container, enumfacing, tile);
				}
			}

			return Optional.of(container);
		}
	}

	private ILockableContainer updateContainerIfDoubleBasket(ILockableContainer container, EnumFacing enumfacing, @Nullable TileEntity tile) {
		if (tile instanceof TileLootBasket) {
			if (enumfacing != EnumFacing.WEST && enumfacing != EnumFacing.NORTH) {
				container = new InventoryLargeChest("container.chestDouble", container, (TileLootBasket) tile);
			} else {
				container = new InventoryLargeChest("container.chestDouble", (TileLootBasket) tile, container);
			}
		}
		return container;
	}
}
