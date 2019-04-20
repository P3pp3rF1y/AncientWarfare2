package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
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
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;

import java.util.HashMap;
import java.util.Map;

public class BlockStool extends BlockSeat {
	private static final String VARIANT_TAG = "variant";
	private static final PropertyEnum<Variant> VARIANT = PropertyEnum.create(VARIANT_TAG, Variant.class);
	private static final Vec3d SEAT_OFFSET = new Vec3d(0.5, 0.35, 0.5);
	private static final AxisAlignedBB STOOL_AABB = new AxisAlignedBB(3 / 16D, 0D, 3 / 16D, 13 / 16D, 9 / 16D, 13 / 16D);

	public BlockStool() {
		super(Material.WOOD, "stool");
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (Variant variant : Variant.values()) {
			ItemStack stack = new ItemStack(this);
			setStackVariant(variant, stack);
			items.add(stack);
		}
	}

	private void setStackVariant(Variant variant, ItemStack stack) {
		stack.setTagInfo(VARIANT_TAG, new NBTTagString(variant.getName()));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return;
		}
		world.setBlockState(pos, state.withProperty(VARIANT, getVariant(stack)));
	}

	private static Variant getVariant(ItemStack stack) {
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(VARIANT_TAG) ? Variant.byName(stack.getTagCompound().getString(VARIANT_TAG)) : Variant.OAK;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		ItemStack stack = new ItemStack(this);
		setStackVariant(state.getValue(VARIANT), stack);
		return stack;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT, Variant.byMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).getMeta();
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
	protected Vec3d getSeatOffset() {
		return SEAT_OFFSET;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return STOOL_AABB;
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

		String modelPropString = "variant=%s";

		ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(this), stack -> {
			if (!stack.hasTagCompound()) {
				return new ModelResourceLocation(baseLocation, String.format(modelPropString, Variant.OAK.getName().toLowerCase()));
			}
			Variant variant = getVariant(stack);
			return new ModelResourceLocation(baseLocation, String.format(modelPropString, variant.getName().toLowerCase()));
		});

		for (Variant variant : Variant.values()) {
			ModelLoader.registerItemVariants(Item.getItemFromBlock(this),
					new ModelResourceLocation(baseLocation, String.format(modelPropString, variant.getName().toLowerCase())));
		}
	}

	public static class Item extends ItemBlockBase {
		public Item(Block block) {
			super(block);
		}

		@Override
		public String getUnlocalizedName(ItemStack stack) {
			return super.getUnlocalizedName(stack) + "_" + getVariant(stack).getName();
		}
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack drop = new ItemStack(this);
		setStackVariant(state.getValue(VARIANT), drop);
		drops.add(drop);
	}

	public enum Variant implements IStringSerializable {
		OAK(0, "oak"),
		SPRUCE(1, "spruce"),
		BIRCH(2, "birch"),
		JUNGLE(3, "jungle"),
		ACACIA(4, "acacia"),
		DARK_OAK(5, "dark_oak");

		private int meta;
		private String name;

		Variant(int meta, String name) {
			this.meta = meta;
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getMeta() {
			return meta;
		}

		private static final Map<Integer, Variant> META_TO_VARIANT = new HashMap<>();
		private static final Map<String, Variant> NAME_TO_VARIANT = new HashMap<>();

		static {
			for (Variant variant : Variant.values()) {
				META_TO_VARIANT.put(variant.meta, variant);
				NAME_TO_VARIANT.put(variant.name, variant);
			}
		}

		public static Variant byMeta(int meta) {
			return META_TO_VARIANT.getOrDefault(meta, OAK);
		}

		public static Variant byName(String name) {
			return NAME_TO_VARIANT.getOrDefault(name, OAK);
		}
	}
}
