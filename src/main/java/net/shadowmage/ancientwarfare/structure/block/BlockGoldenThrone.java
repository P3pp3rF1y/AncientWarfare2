package net.shadowmage.ancientwarfare.structure.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.structure.util.RotationLimit;

import java.util.Map;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class BlockGoldenThrone extends BlockSeat {
	private static final PropertyEnum<Part> PART = PropertyEnum.create("part", Part.class);

	public BlockGoldenThrone() {
		super(Material.IRON, "golden_throne");
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, PART);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(PART, Part.byMeta((meta >> 2) & 1));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() | state.getValue(PART).getMeta() << 2;
	}

	private static final Map<EnumFacing, AxisAlignedBB> TOP_AABBs = ImmutableMap.of(
			EnumFacing.NORTH, new AxisAlignedBB(0, 0, 13 / 16D, 1, 1, 15 / 16D),
			EnumFacing.SOUTH, new AxisAlignedBB(0, 0, 1 / 16D, 1, 1, 3 / 16D),
			EnumFacing.EAST, new AxisAlignedBB(1 / 16D, 0, 0, 3 / 16D, 1, 1),
			EnumFacing.WEST, new AxisAlignedBB(13 / 16D, 0, 0, 15 / 16D, 1, 1)
	);

	private static final Map<EnumFacing, AxisAlignedBB> BOTTOM_AABBs = ImmutableMap.of(
			EnumFacing.NORTH, new AxisAlignedBB(0, 0, 1 / 16D, 1, 1, 15 / 16D),
			EnumFacing.SOUTH, new AxisAlignedBB(0, 0, 1 / 16D, 1, 1, 15 / 16D),
			EnumFacing.EAST, new AxisAlignedBB(1 / 16D, 0, 0, 15 / 16D, 1, 1),
			EnumFacing.WEST, new AxisAlignedBB(1 / 16D, 0, 0, 15 / 16D, 1, 1)
	);

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return state.getValue(PART) == Part.BOTTOM ? BOTTOM_AABBs.get(state.getValue(FACING)) : TOP_AABBs.get(state.getValue(FACING));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos.up(), state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(PART, Part.TOP));
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(PART, Part.BOTTOM));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (state.getValue(PART) == Part.BOTTOM) {
			return super.onBlockActivated(world, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
		}
		IBlockState stateDown = world.getBlockState(pos.down());
		return stateDown.getBlock().onBlockActivated(world, pos.down(), stateDown, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public RotationLimit getRotationLimit(World world, BlockPos seatPos, IBlockState state) {
		return new RotationLimit.FacingQuarter(state.getValue(FACING));
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		BlockPos otherPos = state.getValue(PART) == Part.BOTTOM ? pos.up() : pos.down();
		if (!world.isAirBlock(otherPos)) {
			world.setBlockToAir(otherPos);
		}
		super.breakBlock(world, pos, state);
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
}
