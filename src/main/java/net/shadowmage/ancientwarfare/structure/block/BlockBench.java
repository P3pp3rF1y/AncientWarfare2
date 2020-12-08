package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.structure.util.RotationLimit;
import net.shadowmage.ancientwarfare.structure.util.WoodVariantHelper;

import static net.shadowmage.ancientwarfare.structure.render.property.StructureProperties.AXIS;
import static net.shadowmage.ancientwarfare.structure.util.BlockStateProperties.VARIANT;

public class BlockBench extends BlockSeat {
	private static final PropertyEnum<Legs> LEGS = PropertyEnum.create("legs", Legs.class);
	private static final Vec3d SEAT_OFFSET = new Vec3d(0.5, 0.35, 0.5);

	public BlockBench() {
		super(Material.WOOD, "bench");
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		WoodVariantHelper.getSubBlocks(this, items);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return;
		}
		EnumFacing.Axis perpendicularAxis = placer.getHorizontalFacing().getAxis() == EnumFacing.Axis.X ? EnumFacing.Axis.Z : EnumFacing.Axis.X;
		world.setBlockState(pos, state.withProperty(VARIANT, WoodVariantHelper.getVariant(stack)).withProperty(AXIS, perpendicularAxis));
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return WoodVariantHelper.getPickBlock(this, state);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT, AXIS, LEGS);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT, WoodVariant.byMeta(meta & 7)).withProperty(AXIS, ((meta >> 3) & 1) > 0 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).getMeta() | (state.getValue(AXIS) == EnumFacing.Axis.Z ? 1 : 0) << 3;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		EnumFacing.Axis axis = state.getValue(AXIS);

		Legs legs = Legs.NONE;
		if (axis == EnumFacing.Axis.X) {
			boolean west = isSameAxisBench(state, world, pos.west());
			boolean east = isSameAxisBench(state, world, pos.east());

			if (west && !east) {
				legs = Legs.LEFT;
			} else if (!west && east) {
				legs = Legs.RIGHT;
			} else if (!west) {
				legs = Legs.BOTH;
			}
		} else {
			boolean south = isSameAxisBench(state, world, pos.south());
			boolean north = isSameAxisBench(state, world, pos.north());

			if (north && !south) {
				legs = Legs.RIGHT;
			} else if (!north && south) {
				legs = Legs.LEFT;
			} else if (!north) {
				legs = Legs.BOTH;
			}
		}
		return state.withProperty(LEGS, legs);
	}

	private boolean isSameAxisBench(IBlockState thisState, IBlockAccess world, BlockPos neighborPos) {
		IBlockState neighborState = world.getBlockState(neighborPos);
		return neighborState.getBlock() == this && thisState.getValue(AXIS) == neighborState.getValue(AXIS);
	}

	@Override
	public RotationLimit getRotationLimit(World world, BlockPos seatPos, IBlockState state) {
		return RotationLimit.NO_LIMIT;
	}

	@Override
	protected Vec3d getSeatOffset() {
		return SEAT_OFFSET;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		WoodVariantHelper.getDrops(this, drops, state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		WoodVariantHelper.registerClient(this, propString -> "axis=x,legs=both," + propString);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return state.getValue(AXIS) == EnumFacing.Axis.X ? new AxisAlignedBB(0, 0, 3 / 16d, 1, 9 / 16d, 13 / 16d) :
				new AxisAlignedBB(3 / 16d, 0, 0, 13 / 16d, 9 / 16d, 1);
	}

	public enum Legs implements IStringSerializable {
		NONE("none"),
		LEFT("left"),
		RIGHT("right"),
		BOTH("both");

		private String name;

		Legs(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
