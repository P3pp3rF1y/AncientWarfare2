package net.shadowmage.ancientwarfare.structure.block;

import codechicken.lib.model.ModelRegistryHelper;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockCoffin;
import net.shadowmage.ancientwarfare.structure.render.CoffinRenderer;
import net.shadowmage.ancientwarfare.structure.render.ParticleDummyModel;
import net.shadowmage.ancientwarfare.structure.tile.TileCoffin;

import java.util.Map;

public class BlockCoffin extends BlockMulti<TileCoffin> {
	public BlockCoffin() {
		super(Material.WOOD, "coffin", TileCoffin::new, TileCoffin.class);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (int variant = 1; variant <= 6; variant++) {
			items.add(ItemBlockCoffin.getVariantStack(variant));
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
	protected void setPlacementProperties(World world, BlockPos pos, EntityLivingBase placer, ItemStack stack, TileCoffin te) {
		boolean upright = !ItemBlockCoffin.canPlaceHorizontal(world, pos, placer.getHorizontalFacing(), placer);
		te.setUpright(upright);
		te.setVariant(ItemBlockCoffin.getVariant(stack));
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (player.capabilities.isCreativeMode) {
			return;
		}
		WorldTools.getTile(world, pos, TileCoffin.class)
				.ifPresent(te -> InventoryTools.dropItemInWorld(world, ItemBlockCoffin.getVariantStack(te.getVariant()), pos));
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		//drops handled in onBlockHarvested
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return CoffinRenderer.MODEL_LOCATION;
			}
		});
		ModelRegistryHelper.register(CoffinRenderer.MODEL_LOCATION, ParticleDummyModel.INSTANCE);
		ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new CoffinRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCoffin.class, new CoffinRenderer());
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		WorldTools.getTile(world, pos, TileCoffin.class).ifPresent(te -> te.open(player));
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
}
