package net.shadowmage.ancientwarfare.structure.block;

import codechicken.lib.model.ModelRegistryHelper;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.ParticleUtils;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockStoneCoffin;
import net.shadowmage.ancientwarfare.structure.render.ParticleOnlyModel;
import net.shadowmage.ancientwarfare.structure.render.StoneCoffinRenderer;
import net.shadowmage.ancientwarfare.structure.tile.TileStoneCoffin;

import java.util.Map;

public class BlockStoneCoffin extends BlockMulti<TileStoneCoffin> {

	protected static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0D, 0D, 1D, 14.1 / 16D, 1D);

	public BlockStoneCoffin() {
		super(Material.ROCK, "stone_coffin", TileStoneCoffin::new, TileStoneCoffin.class);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (int variant = 1; variant <= 4; variant++) {
			items.add(ItemBlockStoneCoffin.getVariantStack(variant));
		}
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return state.getValue(INVISIBLE) ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
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
	protected void setPlacementProperties(World world, BlockPos pos, EntityLivingBase placer, ItemStack stack, TileStoneCoffin te) {
		te.setVariant(ItemBlockStoneCoffin.getVariant(stack));
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos);
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (player.capabilities.isCreativeMode) {
			return;
		}

		WorldTools.getTile(world, pos, TileStoneCoffin.class)
				.ifPresent(te -> InventoryTools.dropItemInWorld(world, ItemBlockStoneCoffin.getVariantStack(te.getVariant()), pos));
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return ItemBlockStoneCoffin.getVariantStack(WorldTools.getTile(world, pos, TileStoneCoffin.class).map(TileStoneCoffin::getVariant).orElse(1));
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		//drops handled in onBlockHarvested
	}

	private static final Map<Integer, Integer> PARTICLES = ImmutableMap.of(
			1, 1,
			2, 24,
			3, 168,
			4, 112
	);

	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return StoneCoffinRenderer.MODEL_LOCATION;
			}
		});
		ModelRegistryHelper.register(StoneCoffinRenderer.MODEL_LOCATION, ParticleOnlyModel.INSTANCE);
		ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new StoneCoffinRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileStoneCoffin.class, new StoneCoffinRenderer());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
		int particle = 1;
		if (WorldTools.getTile(world, pos, TileStoneCoffin.class).isPresent()) {
			particle = PARTICLES.get(WorldTools.getTile(world, pos, TileStoneCoffin.class).get().getVariant());
		} else {

		}
		ParticleUtils.playDestroyEffects(world, pos, particle);
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		WorldTools.getTile(world, pos, TileStoneCoffin.class).ifPresent(te -> te.open(player));
		return true;
	}

	public enum CoffinDirection implements IStringSerializable {
		NORTH(0, "north", EnumFacing.NORTH),
		EAST(90, "east", EnumFacing.EAST),
		SOUTH(180, "south", EnumFacing.SOUTH),
		WEST(270, "west", EnumFacing.WEST);

		private int rotationAngle;
		private String name;
		private EnumFacing facing;

		CoffinDirection(int rotationAngle, String name, EnumFacing facing) {
			this.rotationAngle = rotationAngle;
			this.name = name;
			this.facing = facing;
		}

		public int getRotationAngle() {
			return rotationAngle;
		}

		public static BlockStoneCoffin.CoffinDirection fromFacing(EnumFacing facing) {
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

		public EnumFacing getFacing() {
			return facing;
		}

		@Override
		public String getName() {
			return name;
		}

		private static final Map<String, BlockStoneCoffin.CoffinDirection> NAME_VALUES;
		private static final Map<Integer, BlockStoneCoffin.CoffinDirection> ROTATION_VALUES;

		static {
			ImmutableMap.Builder<String, BlockStoneCoffin.CoffinDirection> builder = ImmutableMap.builder();
			ImmutableMap.Builder<Integer, BlockStoneCoffin.CoffinDirection> builderRotation = ImmutableMap.builder();
			for (BlockStoneCoffin.CoffinDirection coffinDirection : values()) {
				builder.put(coffinDirection.getName(), coffinDirection);
				builderRotation.put(coffinDirection.getRotationAngle(), coffinDirection);
			}
			NAME_VALUES = builder.build();
			ROTATION_VALUES = builderRotation.build();
		}

		public static BlockStoneCoffin.CoffinDirection fromName(String name) {
			return NAME_VALUES.getOrDefault(name, NORTH);
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}
}
