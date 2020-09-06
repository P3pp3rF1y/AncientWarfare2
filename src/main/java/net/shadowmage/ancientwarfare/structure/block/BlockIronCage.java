package net.shadowmage.ancientwarfare.structure.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;
import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.VISIBLE;

public class BlockIronCage extends BlockBaseStructure {
	private static final PropertyEnum<Part> PART = PropertyEnum.create("part", Part.class);

	public BlockIronCage() {
		super(Material.WOOD, "iron_cage");
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, VISIBLE, PART);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal((meta >> 2) & 3)).withProperty(VISIBLE, ((meta >> 1) & 1) == 1)
				.withProperty(PART, Part.byMeta(meta & 1));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() << 2 | (state.getValue(VISIBLE) ? 1 : 0) << 1 | state.getValue(PART).getMeta();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return state.getValue(VISIBLE) ? super.getRenderType(state) : EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
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
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		IBlockState placeState = state.withProperty(FACING, placer.getHorizontalFacing().getOpposite());
		world.setBlockState(pos, placeState.withProperty(PART, Part.BOTTOM).withProperty(VISIBLE, true));
		world.setBlockState(pos.up(), placeState.withProperty(PART, Part.BOTTOM).withProperty(VISIBLE, false));
		world.setBlockState(pos.up(2), placeState.withProperty(PART, Part.TOP).withProperty(VISIBLE, true));
		world.setBlockState(pos.up(3), placeState.withProperty(PART, Part.TOP).withProperty(VISIBLE, false));
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (state.getValue(PART) != Part.BOTTOM || !state.getValue(VISIBLE)) {
			IBlockState currentState = state;
			BlockPos currentPos = pos;
			for (int i = 1; i < 4 && (currentState.getValue(PART) != Part.BOTTOM || !currentState.getValue(VISIBLE)); i++) {
				currentPos = pos.down(i);
				currentState = world.getBlockState(currentPos);
				if (currentState.getBlock() != this) {
					return;
				}
			}
			if (currentState.getValue(PART) == Part.BOTTOM && currentState.getValue(VISIBLE)) {
				currentState.getBlock().breakBlock(world, currentPos, currentState);
				return;
			}
		}

		world.setBlockToAir(pos.up(3));
		world.setBlockToAir(pos.up(2));
		world.setBlockToAir(pos.up());
		world.setBlockToAir(pos);
		super.breakBlock(world, pos, state);
	}

	private static final Map<EnumFacing, List<AxisAlignedBB>> BOTTOM_BASE_AABBs = ImmutableMap.of(
			EnumFacing.NORTH, ImmutableList.of(
					new AxisAlignedBB(2 / 16D, 0, 0, 14 / 16D, 2 / 16D, 1)),
			EnumFacing.SOUTH, ImmutableList.of(
					new AxisAlignedBB(2 / 16D, 0, 0, 14 / 16D, 2 / 16D, 1)),
			EnumFacing.EAST, ImmutableList.of(
					new AxisAlignedBB(0, 0, 2 / 16D, 1, 2 / 16D, 14 / 16D)),
			EnumFacing.WEST, ImmutableList.of(
					new AxisAlignedBB(0, 0, 2 / 16D, 1, 2 / 16D, 14 / 16D))
	);

	private static final AxisAlignedBB MIDDLE_AABB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

	private static final Map<EnumFacing, List<AxisAlignedBB>> TOP_AABBs = ImmutableMap.of(
			EnumFacing.NORTH, ImmutableList.of(
					new AxisAlignedBB(0, 0, 0, 1, 8 / 16D, 1),
					new AxisAlignedBB(6 / 16D, 10 / 16D, 0, 10 / 16D, 14 / 16D, 1)),
			EnumFacing.SOUTH, ImmutableList.of(
					new AxisAlignedBB(0, 0, 0, 1, 8 / 16D, 1),
					new AxisAlignedBB(6 / 16D, 10 / 16D, 0, 10 / 16D, 14 / 16D, 1)),
			EnumFacing.EAST, ImmutableList.of(
					new AxisAlignedBB(0, 0, 0, 1, 8 / 16D, 1),
					new AxisAlignedBB(0, 10 / 16D, 6 / 16D, 1, 14 / 16D, 10 / 16D)),
			EnumFacing.WEST, ImmutableList.of(
					new AxisAlignedBB(0, 0, 0, 1, 8 / 16D, 1),
					new AxisAlignedBB(0, 10 / 16D, 6 / 16D, 1, 14 / 16D, 10 / 16D))
	);

	@Nullable
	@Override
	public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
		if (state.getValue(PART) == Part.BOTTOM) {
			if (state.getValue(VISIBLE)) {
				return RayTraceUtils.raytraceMultiAABB(BOTTOM_BASE_AABBs.get(state.getValue(FACING)), pos, start, end, (rtr, aabb) -> rtr);
			} else {
				return rayTrace(pos, start, end, MIDDLE_AABB);
			}
		} else {
			if (state.getValue(VISIBLE)) {
				return rayTrace(pos, start, end, MIDDLE_AABB);
			} else {
				return RayTraceUtils.raytraceMultiAABB(TOP_AABBs.get(state.getValue(FACING)), pos, start, end, (rtr, aabb) -> rtr);
			}
		}
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) {
		return MIDDLE_AABB;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		if (state.getValue(PART) == Part.BOTTOM) {
			if (state.getValue(VISIBLE)) {
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				return RayTraceUtils.getSelectedBoundingBox(BOTTOM_BASE_AABBs.get(state.getValue(FACING)), pos, player);
			} else {
				return MIDDLE_AABB.offset(pos);
			}
		} else {
			if (state.getValue(VISIBLE)) {
				return MIDDLE_AABB.offset(pos);
			} else {
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				return RayTraceUtils.getSelectedBoundingBox(TOP_AABBs.get(state.getValue(FACING)), pos, player);
			}
		}
	}

	public enum Part implements IStringSerializable {
		TOP("top", 0),
		BOTTOM("bottom", 1);

		private String name;
		private int meta;

		Part(String name, int meta) {
			this.name = name;
			this.meta = meta;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getMeta() {
			return meta;
		}

		private static final Map<Integer, Part> META_TO_PART;

		static {
			ImmutableMap.Builder<Integer, Part> builder = new ImmutableMap.Builder<>();
			for (Part part : values()) {
				builder.put(part.getMeta(), part);
			}
			META_TO_PART = builder.build();
		}

		public static Part byMeta(int meta) {
			return META_TO_PART.get(meta);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		final ResourceLocation assetLocation = new ResourceLocation(AncientWarfareCore.MOD_ID, "structure/" + getRegistryName().getResourcePath());
		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation(assetLocation, getPropertyString(state.getProperties()));
			}
		});

		ModelLoaderHelper.registerItem(this, "structure", "inventory");
	}
}
