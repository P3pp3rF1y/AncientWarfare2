package net.shadowmage.ancientwarfare.structure.block;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.NBTBuilder;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockTotemPart;
import net.shadowmage.ancientwarfare.structure.tile.TileTotemPart;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class BlockTotemPart extends BlockBaseStructure {
	public static final String VARIANT_TAG = "variant";
	private static final PropertyEnum<Variant> VARIANT = PropertyEnum.create(VARIANT_TAG, Variant.class);
	private static final PropertyBool VISIBLE = PropertyBool.create("visible");

	public BlockTotemPart() {
		super(Material.WOOD, "totem_part");
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, VISIBLE, VARIANT);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (Variant variant : Variant.values()) {
			if (!variant.includeInSubBlocks()) {
				continue;
			}
			items.add(getVariantStack(variant));
		}
	}

	private ItemStack getVariantStack(Variant variant) {
		Item item = Item.getItemFromBlock(this);
		ItemStack stack = new ItemStack(item);
		stack.setTagCompound(new NBTBuilder().setByte(VARIANT_TAG, variant.getId()).build());
		return stack;
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.005F);

		InventoryTools.dropItemInWorld(world, getVariantStack((te instanceof TileTotemPart ? ((TileTotemPart) te).getDropVariant() : Variant.BASE)), pos);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		//drops handled in harvest block because access to tile entity is needed
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return getVariantStack(WorldTools.getTile(world, pos, TileTotemPart.class).map(TileTotemPart::getDropVariant).orElse(Variant.BASE));
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return state.getValue(VISIBLE) ? super.getRenderType(state) : EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(VISIBLE, ((meta >> 2) & 1) > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getHorizontalIndex() | (state.getValue(VISIBLE) ? 1 : 0) << 2;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return WorldTools.getTile(world, pos, TileTotemPart.class).map(te -> state.withProperty(VARIANT, te.getVariant())).orElse(state);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileTotemPart();
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(VISIBLE, true);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		Variant variant = ItemBlockTotemPart.getVariant(stack);
		WorldTools.getTile(world, pos, TileTotemPart.class).ifPresent(te -> te.setVariant(variant));
		variant.placeAdditionalParts(world, pos, placer.getHorizontalFacing());
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
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		//noinspection ConstantConditions
		ResourceLocation baseLocation = new ResourceLocation(AncientWarfareCore.MOD_ID, "structure/" + getRegistryName().getResourcePath());

		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation(baseLocation, getPropertyString(state.getProperties()));
			}
		});

		String modelPropString = "facing=west,variant=%s,visible=true";

		ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(this), stack -> {
			if (!stack.hasTagCompound()) {
				return new ModelResourceLocation(baseLocation, String.format(modelPropString, true, Variant.BASE.getName().toLowerCase(Locale.ENGLISH)));
			}
			NBTTagCompound tag = stack.getTagCompound();
			//noinspection ConstantConditions
			Variant variant = Variant.fromId(tag.getByte(VARIANT_TAG));
			return new ModelResourceLocation(baseLocation, String.format(modelPropString, variant.getName().toLowerCase(Locale.ENGLISH)));
		});

		for (Variant variant : Variant.values()) {
			ModelLoader.registerItemVariants(Item.getItemFromBlock(this),
					new ModelResourceLocation(baseLocation, String.format(modelPropString, variant.getName().toLowerCase(Locale.ENGLISH))));
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return WorldTools.getTile(source, pos, TileTotemPart.class).map(TileTotemPart::getVariant).orElse(Variant.BASE).getBoundingBox(state.getValue(FACING));
	}

	public enum Variant implements IStringSerializable {
		BASE(0) {
			@Override
			public Set<BlockPos> getAdditionalPartPositions(BlockPos pos, EnumFacing facing) {
				return ImmutableSet.of(pos.up());
			}

			@Override
			public void placeAdditionalParts(World world, BlockPos pos, EnumFacing facing) {
				world.setBlockState(pos.up(), AWStructureBlocks.TOTEM_PART.getDefaultState().withProperty(VISIBLE, false));
				WorldTools.getTile(world, pos.up(), TileTotemPart.class).ifPresent(t -> t.setMainBlockPos(pos));
			}
		},
		MID(1),
		MID_ALT(2),
		TOP(3) {
			@Override
			public Set<BlockPos> getAdditionalPartPositions(BlockPos pos, EnumFacing facing) {
				return ImmutableSet.of(pos.up(), pos.offset(facing.rotateY()), pos.offset(facing.rotateYCCW()));
			}

			@Override
			public void placeAdditionalParts(World world, BlockPos pos, EnumFacing facing) {
				world.setBlockState(pos.up(), AWStructureBlocks.TOTEM_PART.getDefaultState().withProperty(VISIBLE, false));
				WorldTools.getTile(world, pos.up(), TileTotemPart.class).ifPresent(t -> t.setMainBlockPos(pos));
				placeSideBlock(world, pos, pos.offset(facing.rotateY()), facing.getOpposite(), Variant.WINGS);
				placeSideBlock(world, pos, pos.offset(facing.rotateYCCW()), facing, Variant.WINGS);
			}
		},
		WINGS(4) {
			@Override
			public AxisAlignedBB getBoundingBox(EnumFacing facing) {
				return facing.getAxis() == EnumFacing.Axis.X ? WING_AABB_X : WING_AABB_Z;
			}
		},
		IRMINSUL_BASE(5) {
			@Override
			public AxisAlignedBB getBoundingBox(EnumFacing facing) {
				return facing.getAxis() == EnumFacing.Axis.X ? IRMINSUL_AABB_X : IRMINSUL_AABB_Z;
			}
		},
		IRMINSUL_MID(6) {
			@Override
			public AxisAlignedBB getBoundingBox(EnumFacing facing) {
				return facing.getAxis() == EnumFacing.Axis.X ? IRMINSUL_AABB_X : IRMINSUL_AABB_Z;
			}
		},
		IRMINSUL_TOP(7) {
			@Override
			public AxisAlignedBB getBoundingBox(EnumFacing facing) {
				return facing.getAxis() == EnumFacing.Axis.X ? IRMINSUL_TOP_AABB_X : IRMINSUL_TOP_AABB_Z;
			}

			@Override
			public Set<BlockPos> getAdditionalPartPositions(BlockPos pos, EnumFacing facing) {
				return ImmutableSet.of(pos.offset(facing.rotateY()), pos.offset(facing.rotateYCCW()));
			}

			@Override
			public void placeAdditionalParts(World world, BlockPos pos, EnumFacing facing) {
				placeSideBlock(world, pos, pos.offset(facing.rotateY()), facing.getOpposite(), Variant.IRMINSUL_SIDE);
				placeSideBlock(world, pos, pos.offset(facing.rotateYCCW()), facing, Variant.IRMINSUL_SIDE);
			}

		},
		IRMINSUL_SIDE(8) {
			@Override
			public AxisAlignedBB getBoundingBox(EnumFacing facing) {
				return facing.getAxis() == EnumFacing.Axis.X ? IRMINSUL_SIDE_AABB_X : IRMINSUL_SIDE_AABB_Z;
			}
		},
		ELEPHANT_LEFT(9) {
			@Override
			public AxisAlignedBB getBoundingBox(EnumFacing value) {
				return FULL_BLOCK_AABB;
			}

			@Override
			public boolean includeInSubBlocks() {
				return false;
			}
		},
		ELEPHANT_RIGHT(10) {
			@Override
			public AxisAlignedBB getBoundingBox(EnumFacing value) {
				return FULL_BLOCK_AABB;
			}

			@Override
			public boolean includeInSubBlocks() {
				return false;
			}
		},
		ELEPHANT_BODY(11) {
			@Override
			public void placeAdditionalParts(World world, BlockPos pos, EnumFacing facing) {
				BlockPos right = pos.offset(facing.rotateY());
				BlockPos left = pos.offset(facing.rotateYCCW());
				placeInvisibleBlock(world, pos, right.up(), ELEPHANT_RIGHT);
				placeSideBlock(world, pos, right, facing, ELEPHANT_RIGHT);
				placeInvisibleBlock(world, pos, left.up(), ELEPHANT_LEFT);
				placeSideBlock(world, pos, left, facing, ELEPHANT_LEFT);
				placeInvisibleBlock(world, pos, pos.up(), ELEPHANT_BODY);
			}

			@Override
			public Set<BlockPos> getAdditionalPartPositions(BlockPos pos, EnumFacing facing) {
				BlockPos right = pos.offset(facing.rotateY());
				BlockPos left = pos.offset(facing.rotateYCCW());
				return ImmutableSet.of(pos.up(), right, left, right.up(), left.up());
			}

			@Override
			public AxisAlignedBB getBoundingBox(EnumFacing value) {
				return FULL_BLOCK_AABB;
			}
		}, XOLTEC_IDOL_BODY_LEFT(12) {
			@Override
			public void placeAdditionalParts(World world, BlockPos pos, EnumFacing facing) {
				BlockPos right = pos.offset(facing.rotateY());
				placeSideBlock(world, pos, right, facing, XOLTEC_IDOL_BODY_RIGHT);
				placeSideBlock(world, pos, right.up(), facing, XOLTEC_IDOL_HEAD_RIGHT);
				placeInvisibleBlock(world, pos, right.up().up(), XOLTEC_IDOL_HEAD_RIGHT, facing);
				placeSideBlock(world, pos, pos.up(), facing, XOLTEC_IDOL_HEAD_LEFT);
				placeInvisibleBlock(world, pos, pos.up().up(), XOLTEC_IDOL_HEAD_LEFT, facing);
			}

			@Override
			public Set<BlockPos> getAdditionalPartPositions(BlockPos pos, EnumFacing facing) {
				BlockPos right = pos.offset(facing.rotateY());
				return ImmutableSet.of(right, right.up(), right.up().up(), pos.up(), pos.up().up());
			}

			@Override
			public AxisAlignedBB getBoundingBox(EnumFacing facing) {
				return getXoltecIdolAABB(facing);
			}
		}, XOLTEC_IDOL_BODY_RIGHT(13) {
			@Override
			public boolean includeInSubBlocks() {
				return false;
			}

			@Override
			public AxisAlignedBB getBoundingBox(EnumFacing facing) {
				return getXoltecIdolAABB(facing);
			}
		}, XOLTEC_IDOL_HEAD_LEFT(14) {
			@Override
			public boolean includeInSubBlocks() {
				return false;
			}

			@Override
			public AxisAlignedBB getBoundingBox(EnumFacing facing) {
				return getXoltecIdolAABB(facing);
			}
		}, XOLTEC_IDOL_HEAD_RIGHT(15) {
			@Override
			public boolean includeInSubBlocks() {
				return false;
			}

			@Override
			public AxisAlignedBB getBoundingBox(EnumFacing facing) {
				return getXoltecIdolAABB(facing);
			}
		};

		private static AxisAlignedBB getXoltecIdolAABB(EnumFacing facing) {
			switch (facing) {
				case NORTH:
					return XOLTEC_IDOL_NORTH_AABB;
				case SOUTH:
					return XOLTEC_IDOL_SOUTH_AABB;
				case WEST:
					return XOLTEC_IDOL_WEST_AABB;
				case EAST:
				default:
					return XOLTEC_IDOL_EAST_AABB;
			}
		}

		private int id;

		Variant(int id) {
			this.id = id;
		}

		protected static final AxisAlignedBB DEFAULT_AABB = new AxisAlignedBB(3D / 16D, 0D, 3D / 16D, (16D - 3D) / 16D, 1D, (16D - 3D) / 16D);
		protected static final AxisAlignedBB WING_AABB_X = new AxisAlignedBB(6D / 16D, 4D / 16D, 0D, (16D - 6D) / 16D, (16D - 1D) / 16D, 1D);
		protected static final AxisAlignedBB WING_AABB_Z = new AxisAlignedBB(0D, 4D / 16D, 6D / 16D, 1D, (16D - 1D) / 16D, (16D - 6D) / 16D);
		protected static final AxisAlignedBB IRMINSUL_AABB_X = new AxisAlignedBB(4D / 16D, 0D, 3D / 16D, (16D - 4D) / 16D, 1D, (16D - 3D) / 16D);
		protected static final AxisAlignedBB IRMINSUL_AABB_Z = new AxisAlignedBB(3D / 16D, 0D, 4D / 16D, (16D - 3D) / 16D, 1D, (16D - 4D) / 16D);
		protected static final AxisAlignedBB IRMINSUL_TOP_AABB_X = new AxisAlignedBB(4D / 16D, 0D, 0D, (16D - 4D) / 16D, (16D - 2D) / 16D, 1D);
		protected static final AxisAlignedBB IRMINSUL_TOP_AABB_Z = new AxisAlignedBB(0D, 0D, 4D / 16D, 1D, (16D - 2D) / 16D, (16D - 4D) / 16D);
		protected static final AxisAlignedBB IRMINSUL_SIDE_AABB_X = new AxisAlignedBB(4D / 16D, 3D / 16D, 0D, (16D - 4D) / 16D, (16D - 2D) / 16D, 1D);
		protected static final AxisAlignedBB IRMINSUL_SIDE_AABB_Z = new AxisAlignedBB(0D, 3D / 16D, 4D / 16D, 1D, (16D - 2D) / 16D, (16D - 4D) / 16D);
		protected static final AxisAlignedBB XOLTEC_IDOL_SOUTH_AABB = new AxisAlignedBB(0D, 0D, 8D / 16D, 1D, 1D, 1D);
		protected static final AxisAlignedBB XOLTEC_IDOL_NORTH_AABB = new AxisAlignedBB(0D, 0D, 0D, 1D, 1D, 8D / 16D);
		protected static final AxisAlignedBB XOLTEC_IDOL_WEST_AABB = new AxisAlignedBB(0D, 0D, 0D, 8D / 16D, 1D, 1D);
		protected static final AxisAlignedBB XOLTEC_IDOL_EAST_AABB = new AxisAlignedBB(8D / 16D, 0D, 0D, 1D, 1D, 1D);

		public int getId() {
			return id;
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}

		private static Map<Integer, Variant> variants = new HashMap<>();

		static {
			for (Variant variant : Variant.values()) {
				variants.put(variant.getId(), variant);
			}
		}

		@SuppressWarnings("squid:S1172") //actually used when overridden
		public Set<BlockPos> getAdditionalPartPositions(BlockPos pos, EnumFacing facing) {
			return new HashSet<>();
		}

		public boolean canPlace(World world, BlockPos pos, EntityPlayer placer) {
			if (!mayPlace(world, pos, placer)) {
				return false;
			}

			Set<BlockPos> positions = getAdditionalPartPositions(pos, placer.getHorizontalFacing());
			for (BlockPos addPos : positions) {
				if (!mayPlace(world, addPos, placer)) {
					return false;
				}
			}

			return true;
		}

		public void placeAdditionalParts(World world, BlockPos pos, EnumFacing facing) {
			//noop by default
		}

		private boolean mayPlace(World world, BlockPos pos, EntityPlayer placer) {
			return world.mayPlace(AWStructureBlocks.TOTEM_PART, pos, false, EnumFacing.UP, placer);
		}

		public static Variant fromId(int id) {
			return variants.get(id);
		}

		public AxisAlignedBB getBoundingBox(EnumFacing value) {
			return DEFAULT_AABB;
		}

		private static void placeInvisibleBlock(World world, BlockPos mainPos, BlockPos sidePos, Variant variant) {
			placeInvisibleBlock(world, mainPos, sidePos, variant, EnumFacing.NORTH);
		}

		private static void placeInvisibleBlock(World world, BlockPos mainPos, BlockPos sidePos, Variant variant, EnumFacing facing) {
			world.setBlockState(sidePos, AWStructureBlocks.TOTEM_PART.getDefaultState().withProperty(VISIBLE, false).withProperty(FACING, facing));
			setupTileData(world, mainPos, sidePos, variant);
		}

		private static void setupTileData(World world, BlockPos mainPos, BlockPos sidePos, Variant variant) {
			WorldTools.getTile(world, sidePos, TileTotemPart.class).ifPresent(t -> {
				t.setVariant(variant);
				t.setMainBlockPos(mainPos);
			});
		}

		private static void placeSideBlock(World world, BlockPos mainPos, BlockPos sidePos, EnumFacing sideFacing, Variant variant) {
			world.setBlockState(sidePos, AWStructureBlocks.TOTEM_PART.getDefaultState().withProperty(FACING, sideFacing));
			setupTileData(world, mainPos, sidePos, variant);
		}

		public boolean includeInSubBlocks() {
			return true;
		}
	}
}
