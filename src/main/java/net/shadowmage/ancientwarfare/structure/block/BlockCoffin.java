package net.shadowmage.ancientwarfare.structure.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.tile.TileCoffin;

import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public abstract class BlockCoffin<T extends TileCoffin> extends BlockMulti<T> {
	public BlockCoffin(Material material, String regName, Supplier<T> instantiateTe, Class<T> teClass) {
		super(material, regName, instantiateTe, teClass);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return Boolean.TRUE.equals(state.getValue(INVISIBLE)) ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
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
	public boolean isSideSolid(IBlockState baseState, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (player.capabilities.isCreativeMode) {
			return;
		}
		WorldTools.getTile(world, pos, TileCoffin.class)
				.ifPresent(te -> InventoryTools.dropItemInWorld(world, getVariantStack(te.getVariant()), pos));
	}

	protected abstract ItemStack getVariantStack(IVariant variant);

	protected abstract IVariant getDefaultVariant();

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return getVariantStack(WorldTools.getTile(world, pos, TileCoffin.class).map(TileCoffin::getVariant).orElse(getDefaultVariant()));
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		//drops handled in onBlockHarvested
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		WorldTools.getTile(world, pos, TileCoffin.class).ifPresent(TileCoffin::open);
		return true;
	}

	public enum CoffinDirection implements IStringSerializable {
		NORTH(0, "north", EnumFacing.NORTH),
		EAST(90, "east", EnumFacing.EAST),
		SOUTH(180, "south", EnumFacing.SOUTH),
		WEST(270, "west", EnumFacing.WEST),
		NORTH_EAST(45, "north_east", EnumFacing.NORTH),
		SOUTH_EAST(135, "south_east", EnumFacing.NORTH),
		SOUTH_WEST(225, "south_west", EnumFacing.NORTH),
		NORTH_WEST(315, "north_west", EnumFacing.NORTH);

		private int rotationAngle;
		private String name;
		private EnumFacing facing;

		CoffinDirection(int rotationAngle, String name, EnumFacing facing) {
			this.rotationAngle = rotationAngle;
			this.name = name;
			this.facing = facing;
		}

		public static CoffinDirection fromYaw(float rotationYaw) {
			return fromRotation((int) Trig.wrapTo360(rotationYaw + 180 + 23) / 45 * 45);
		}

		public int getRotationAngle() {
			return rotationAngle;
		}

		public static CoffinDirection fromFacing(EnumFacing facing) {
			switch (facing) {
				case SOUTH:
					return SOUTH;
				case EAST:
					return EAST;
				case WEST:
					return WEST;
				default:
				case NORTH:
					return NORTH;
			}
		}

		public CoffinDirection rotateY() {
			switch (this) {
				case EAST:
					return SOUTH;
				case SOUTH:
					return WEST;
				case WEST:
					return NORTH;
				case NORTH_EAST:
					return SOUTH_EAST;
				case SOUTH_EAST:
					return SOUTH_WEST;
				case SOUTH_WEST:
					return NORTH_WEST;
				case NORTH_WEST:
					return NORTH_EAST;
				default:
				case NORTH:
					return EAST;
			}
		}

		public EnumFacing getFacing() {
			return facing;
		}

		@Override
		public String getName() {
			return name;
		}

		private static final Map<String, CoffinDirection> NAME_VALUES;
		private static final Map<Integer, CoffinDirection> ROTATION_VALUES;

		static {
			ImmutableMap.Builder<String, CoffinDirection> builder = ImmutableMap.builder();
			ImmutableMap.Builder<Integer, CoffinDirection> builderRotation = ImmutableMap.builder();
			for (CoffinDirection coffinDirection : values()) {
				builder.put(coffinDirection.getName(), coffinDirection);
				builderRotation.put(coffinDirection.getRotationAngle(), coffinDirection);
			}
			NAME_VALUES = builder.build();
			ROTATION_VALUES = builderRotation.build();
		}

		public static CoffinDirection fromName(String name) {
			return NAME_VALUES.getOrDefault(name, NORTH);
		}

		static CoffinDirection fromRotation(int rotationAngle) {
			return ROTATION_VALUES.getOrDefault(rotationAngle, NORTH);
		}
	}

	public interface IVariant extends IStringSerializable {
	}
}
