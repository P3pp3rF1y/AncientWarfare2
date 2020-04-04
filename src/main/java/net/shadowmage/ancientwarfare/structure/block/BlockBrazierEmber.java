package net.shadowmage.ancientwarfare.structure.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.NBTBuilder;

import java.util.Random;

public class BlockBrazierEmber extends BlockBaseStructure {
	public static final String LIT_TAG = "lit";
	private static final PropertyBool LIT = PropertyBool.create(LIT_TAG);

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.88D, 1D);

	public BlockBrazierEmber() {
		super(Material.ROCK, "brazier_ember");
		setHardness(2);
		setTickRandomly(true);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, LIT);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		Item item = Item.getItemFromBlock(this);
		ItemStack stack = new ItemStack(item);
		stack.setTagCompound(new NBTBuilder().setBoolean(LIT_TAG, false).build());
		items.add(stack);
		stack = new ItemStack(item);
		stack.setTagCompound(new NBTBuilder().setBoolean(LIT_TAG, true).build());
		items.add(stack);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(LIT, (meta == 1));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(LIT) ? 1 : 0;
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
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}


	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) { return AABB; }

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(LIT) ? 6 : 0;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return;
		}
		//noinspection ConstantConditions
		state = state.withProperty(LIT, stack.getTagCompound().getBoolean(LIT_TAG));
		worldIn.setBlockState(pos, state);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		ItemStack pickStack = new ItemStack(Item.getItemFromBlock(this));
		pickStack.setTagCompound(new NBTBuilder().setBoolean(LIT_TAG, state.getValue(LIT)).build());
		return pickStack;
	}
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (!state.getValue(LIT) && isFireStarter(stack)) {
			if (world.isRemote) {
				return true;
			}

			world.setBlockState(pos, state.withProperty(LIT, true));
			if (stack.getItem().isDamageable()) {
				stack.damageItem(1, player);
			}
			world.playSound(null, pos, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.PLAYERS, 1, 1);

			return true;
		} else if (state.getValue(LIT) && stack.getItem() == Items.WATER_BUCKET) {
			if (world.isRemote) {
				return true;
			}

			world.setBlockState(pos, state.withProperty(LIT, false));
			if (!player.capabilities.isCreativeMode) {
				player.setHeldItem(hand, new ItemStack(Items.BUCKET));
			}
			world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5f, 1);
		}

		return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
	}

	private boolean isFireStarter(ItemStack heldItem) {
		return heldItem.getItem() == Items.FLINT_AND_STEEL || heldItem.getItem() == Item.getItemFromBlock(Blocks.TORCH);
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ItemStack BlockBrazierEmber = new ItemStack(this);
		BlockBrazierEmber.setTagCompound(new NBTBuilder().setBoolean(LIT_TAG, false).build());
		drops.add(BlockBrazierEmber);
	}

	@Override
	public int tickRate(World worldIn) {
		return 30;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (!stateIn.getValue(LIT)) {
			return;
		}

		if (rand.nextInt(10) == 0) {
			worldIn.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
		}

		for (int i = 0; i < 3; ++i) {
			double x = pos.getX() + 0.25D + rand.nextDouble() * 0.5D;
			double y = pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
			double z = pos.getZ() + 0.25D + rand.nextDouble() * 0.5D;
			worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D);
		}
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

		String modelPropString = "lit=%b";

		ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(this), stack -> {
			if (!stack.hasTagCompound()) {
				return new ModelResourceLocation(baseLocation, String.format(modelPropString, true));
			}
			NBTTagCompound tag = stack.getTagCompound();
			//noinspection ConstantConditions
			boolean lit = tag.getBoolean(LIT_TAG);
			return new ModelResourceLocation(baseLocation, String.format(modelPropString, lit));
		});

		ModelLoader.registerItemVariants(Item.getItemFromBlock(this),
				new ModelResourceLocation(baseLocation, String.format(modelPropString, true)));
	}

}
