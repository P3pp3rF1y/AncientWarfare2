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
import net.minecraft.entity.Entity;
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

public class BlockStretchingRack extends BlockBaseStructure {
	private static final PropertyEnum<Part> PART = PropertyEnum.create("part", Part.class);

	public BlockStretchingRack() {
		super(Material.WOOD, "stretching_rack");
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
		EnumFacing facing = placer.getHorizontalFacing();
		IBlockState placeState = state.withProperty(FACING, placer.getHorizontalFacing().getOpposite());
		world.setBlockState(pos, placeState.withProperty(PART, Part.SOUTH).withProperty(VISIBLE, true));
		world.setBlockState(pos.offset(facing), placeState.withProperty(PART, Part.SOUTH).withProperty(VISIBLE, false));
		world.setBlockState(pos.offset(facing, 2), placeState.withProperty(PART, Part.NORTH).withProperty(VISIBLE, true));
		world.setBlockState(pos.offset(facing, 3), placeState.withProperty(PART, Part.NORTH).withProperty(VISIBLE, false));
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		EnumFacing facing = state.getValue(FACING).getOpposite();
		if (state.getValue(PART) != Part.SOUTH || !state.getValue(VISIBLE)) {
			IBlockState currentState = state;
			BlockPos currentPos = pos;
			for (int i = 1; i < 4 && (currentState.getValue(PART) !=  Part.SOUTH || !currentState.getValue(VISIBLE)); i++) {
				currentPos = pos.offset(facing.getOpposite(), i);
				currentState = world.getBlockState(currentPos);
				if (currentState.getBlock() != this) {
					return;
				}
			}
			if (currentState.getValue(PART) ==  Part.SOUTH && currentState.getValue(VISIBLE)) {
				currentState.getBlock().breakBlock(world, currentPos, currentState);
				return;
			}
		}
		world.setBlockToAir(pos);
		world.setBlockToAir(pos.offset(facing));
		world.setBlockToAir(pos.offset(facing, 2));
		world.setBlockToAir(pos.offset(facing, 3));
		super.breakBlock(world, pos, state);
	}

	private static final Map<EnumFacing, List<AxisAlignedBB>> SOUTH_AABBs = ImmutableMap.of(
			EnumFacing.NORTH, ImmutableList.of(
					new AxisAlignedBB(1 / 16D, 0, 0, 15 / 16D, 17/ 16D, 1)),
			EnumFacing.SOUTH, ImmutableList.of(
					new AxisAlignedBB(1 / 16D, 0, 0, 15/ 16D, 17/ 16D, 1)),
			EnumFacing.EAST, ImmutableList.of(
					new AxisAlignedBB(0, 0, 1 / 16D, 1, 17/ 16D, 15/ 16D)),
			EnumFacing.WEST, ImmutableList.of(
					new AxisAlignedBB(0, 0, 1 / 16D, 1, 17/ 16D, 15/ 16D))
	);

	private static final Map<EnumFacing, List<AxisAlignedBB>> MID_AABBs = ImmutableMap.of(
			EnumFacing.NORTH, ImmutableList.of(
					new AxisAlignedBB(1 / 16D, 13 / 16D, 0, 15 / 16D, 17/ 16D, 1)),
			EnumFacing.SOUTH, ImmutableList.of(
					new AxisAlignedBB(1 / 16D, 13 / 16D, 0, 15/ 16D, 17/ 16D, 1)),
			EnumFacing.EAST, ImmutableList.of(
					new AxisAlignedBB(0, 13 / 16D, 1 / 16D, 1, 17/ 16D, 15/ 16D)),
			EnumFacing.WEST, ImmutableList.of(
					new AxisAlignedBB(0, 13 / 16D, 1 / 16D, 1, 17/ 16D, 15/ 16D))
	);

	private static final Map<EnumFacing,List<AxisAlignedBB>> NORTH_AABBs = ImmutableMap.of(
			EnumFacing.NORTH, ImmutableList.of(
					new AxisAlignedBB(0, 0, 0, 1, 17/ 16D, 8 / 16D)),
			EnumFacing.SOUTH, ImmutableList.of(
					new AxisAlignedBB(0, 0, 8 / 16D, 1, 17/ 16D, 1)),
			EnumFacing.EAST, ImmutableList.of(
					new AxisAlignedBB(8 / 16D, 0, 0, 1, 17/ 16D, 1)),
			EnumFacing.WEST, ImmutableList.of(
					new AxisAlignedBB(0, 0, 0, 8 / 16D, 17/ 16D, 1))
	);

	@Nullable
	@Override
	public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
		if (state.getValue(PART) == Part.SOUTH) {
			if (state.getValue(VISIBLE)) {
				return RayTraceUtils.raytraceMultiAABB(SOUTH_AABBs.get(state.getValue(FACING)), pos, start, end, (rtr, aabb) -> rtr);
			} else {
				return RayTraceUtils.raytraceMultiAABB(MID_AABBs.get(state.getValue(FACING)), pos, start, end, (rtr, aabb) -> rtr);
			}
		} else {
			if (state.getValue(VISIBLE)) {
				return RayTraceUtils.raytraceMultiAABB(MID_AABBs.get(state.getValue(FACING)), pos, start, end, (rtr, aabb) -> rtr);
			} else {
				return RayTraceUtils.raytraceMultiAABB(NORTH_AABBs.get(state.getValue(FACING)), pos, start, end, (rtr, aabb) -> rtr);
			}
		}
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes,
			@Nullable Entity entityIn, boolean isActualState) {
		NORTH_AABBs.get(state.getValue(FACING)).forEach(aabb -> addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		if (state.getValue(PART) == Part.SOUTH) {
			if (state.getValue(VISIBLE)) {
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				return RayTraceUtils.getSelectedBoundingBox(SOUTH_AABBs.get(state.getValue(FACING)), pos, player);
			} else {
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				return RayTraceUtils.getSelectedBoundingBox(MID_AABBs.get(state.getValue(FACING)), pos, player);
			}
		} else {
			if (state.getValue(VISIBLE)) {
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				return RayTraceUtils.getSelectedBoundingBox(MID_AABBs.get(state.getValue(FACING)), pos, player);
			} else {
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				return RayTraceUtils.getSelectedBoundingBox(NORTH_AABBs.get(state.getValue(FACING)), pos, player);
			}
		}
	}

	public enum Part implements IStringSerializable {
		NORTH("north", 0),
		SOUTH("south", 1);

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
