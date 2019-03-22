package net.shadowmage.ancientwarfare.structure.block;

import codechicken.lib.model.ModelRegistryHelper;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockCoffin;
import net.shadowmage.ancientwarfare.structure.render.CoffinRenderer;
import net.shadowmage.ancientwarfare.structure.tile.TileCoffin;

import javax.annotation.Nullable;
import java.util.Map;

public class BlockCoffin extends BlockBaseStructure {
	public static final PropertyBool UPRIGHT = PropertyBool.create("upright");
	public static final PropertyEnum<CoffinDirection> DIRECTION = PropertyEnum.<CoffinDirection>create("direction", CoffinDirection.class);

	public BlockCoffin() {
		super(Material.WOOD, "coffin");
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, UPRIGHT, DIRECTION);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(UPRIGHT, (meta & 1) == 1).withProperty(DIRECTION, CoffinDirection.fromMeta(meta >> 1));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(UPRIGHT) ? 1 : 0) | state.getValue(DIRECTION).getMeta() << 1;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileCoffin();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
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
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		boolean upright = !ItemBlockCoffin.canPlaceHorizontal(worldIn, pos, facing, placer);
		return getDefaultState().withProperty(UPRIGHT, upright)
				.withProperty(DIRECTION, upright ? CoffinDirection.fromYaw(placer.rotationYaw) : CoffinDirection.fromFacing(placer.getHorizontalFacing()));
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		//TODO set variants as well set invisible blocks
		// invisible blocks should have render type set as such rather than TESR rendering exiting exiting early
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, CoffinRenderer.MODEL_LOCATION);

		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return CoffinRenderer.MODEL_LOCATION;
			}
		});

		ClientRegistry.bindTileEntitySpecialRenderer(TileCoffin.class, new CoffinRenderer());

		ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new CoffinRenderer());
	}

	public enum CoffinDirection implements IStringSerializable {
		NORTH(0, 0, "north"),
		EAST(1, 90, "east"),
		SOUTH(2, 180, "south"),
		WEST(3, 270, "west"),
		NORTH_EAST(4, 45, "north_east"),
		SOUTH_EAST(5, 135, "south_east"),
		SOUTH_WEST(6, 225, "south_west"),
		NORTH_WEST(7, 315, "north_west");

		private int meta;
		private int rotationAngle;
		private String name;

		CoffinDirection(int meta, int rotationAngle, String name) {
			this.meta = meta;
			this.rotationAngle = rotationAngle;
			this.name = name;
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

		@Override
		public String getName() {
			return name;
		}

		private static final Map<Integer, CoffinDirection> META_VALUES;
		private static final Map<Integer, CoffinDirection> ROTATION_VALUES;

		static {
			ImmutableMap.Builder<Integer, CoffinDirection> builder = ImmutableMap.<Integer, CoffinDirection>builder();
			ImmutableMap.Builder<Integer, CoffinDirection> builderRotation = ImmutableMap.<Integer, CoffinDirection>builder();
			for (CoffinDirection coffinDirection : values()) {
				builder.put(coffinDirection.getMeta(), coffinDirection);
				builderRotation.put(coffinDirection.getRotationAngle(), coffinDirection);
			}
			META_VALUES = builder.build();
			ROTATION_VALUES = builderRotation.build();
		}

		private Integer getMeta() {
			return meta;
		}

		public static CoffinDirection fromMeta(int meta) {
			return META_VALUES.getOrDefault(meta, NORTH);
		}

		static CoffinDirection fromRotation(int rotationAngle) {
			return ROTATION_VALUES.getOrDefault(rotationAngle, NORTH);
		}
	}
}
