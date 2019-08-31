package net.shadowmage.ancientwarfare.structure.block;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.NBTBuilder;
import net.shadowmage.ancientwarfare.npc.item.ItemCoin;
import net.shadowmage.ancientwarfare.structure.init.AWStructureSoundTypes;
import net.shadowmage.ancientwarfare.structure.init.AWStructureSounds;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BlockCoinStack extends BlockBaseStructure {
	private ItemCoin.CoinMetal coinMetal;

	public BlockCoinStack(String regName, ItemCoin.CoinMetal coinMetal) {
		super(Material.GROUND, regName);
		setHardness(0.3F);
		setResistance(0.4F);
		this.coinMetal = coinMetal;
	}

	private static final Map<Integer, AxisAlignedBB> STACK_SIZE_AABBs = new ImmutableMap.Builder<Integer, AxisAlignedBB>()
			.put(8, new AxisAlignedBB(0D, 0D, 0D, 1D, 0.10D, 1D))
			.put(16, new AxisAlignedBB(0D, 0D, 0D, 1D, 0.25D, 1D))
			.put(24, new AxisAlignedBB(0D, 0D, 0D, 1D, 0.37D, 1D))
			.put(32, new AxisAlignedBB(0D, 0D, 0D, 1D, 0.57D, 1D))
			.put(40, new AxisAlignedBB(0D, 0D, 0D, 1D, 0.81D, 1D))
			.put(48, new AxisAlignedBB(0D, 0D, 0D, 1D, 0.81D, 1D))
			.put(56, new AxisAlignedBB(0D, 0D, 0D, 1D, 0.95D, 1D))
			.put(64, new AxisAlignedBB(0D, 0D, 0D, 1D, 1.00D, 1D))
			.build();

	private static final String STACK_SIZE_TAG = "size";

	private static final PropertyStackSize STACK_SIZE = new PropertyStackSize();

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, STACK_SIZE);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (int stackSize = 8; stackSize < 65; stackSize = stackSize + 8) {
			ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
			stack.setTagCompound(new NBTBuilder().setInteger(STACK_SIZE_TAG, stackSize).build());
			items.add(stack);
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(STACK_SIZE, (meta + 1) * 8);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(STACK_SIZE) / 8) - 1;
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
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return;
		}
		worldIn.setBlockState(pos, state.withProperty(STACK_SIZE, getCoinStackSize(stack)));
	}

	private int getCoinStackSize(ItemStack stack) {
		//noinspection ConstantConditions
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger(STACK_SIZE_TAG) : 0;
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		drops.add(ItemCoin.getCoinStack(coinMetal, state.getValue(STACK_SIZE)));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) {
			return true;
		}
		if (ItemCoin.isSpecificCoin(stack, coinMetal)) {
			addToStack(world, pos, state, player, stack);
		} else {
			removeFromStack(world, pos, state, player);
		}
		return true;
	}

	private void removeFromStack(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		int blockstacksize = state.getValue(STACK_SIZE);

		//noinspection ConstantConditions
		InventoryTools.insertOrDropItem(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), ItemCoin.getCoinStack(coinMetal, 8), world, pos);
		if (blockstacksize > 8) {
			world.setBlockState(pos, state.withProperty(STACK_SIZE, blockstacksize - 8));
		} else {
			world.setBlockToAir(pos);
		}
		world.playSound(null, pos, AWStructureSounds.COIN_STACK_INTERACT, SoundCategory.PLAYERS, 0.5f, 1);
	}

	private void addToStack(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack stack) {
		if (stack.getCount() > 8) {
			int blockStackSize = state.getValue(STACK_SIZE);
			if (blockStackSize < 64) {
				world.setBlockState(pos, state.withProperty(STACK_SIZE, (blockStackSize + 8)));
				if (!player.capabilities.isCreativeMode) {
					stack.shrink(8);
				}
			}
		}
		world.playSound(null, pos, AWStructureSounds.COIN_STACK_INTERACT, SoundCategory.PLAYERS, 0.5f, 1);
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
		return AWStructureSoundTypes.COINSTACK;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(getCoinStackSize(stack) + " " + I18n.format("guistrings.structure.coins"));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return STACK_SIZE_AABBs.get(state.getValue(STACK_SIZE));
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

		String modelPropInteger = "size=%s";

		ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(this), stack -> {
			if (!stack.hasTagCompound()) {
				return new ModelResourceLocation(baseLocation, String.format(modelPropInteger, 8));
			}
			//noinspection ConstantConditions
			return new ModelResourceLocation(baseLocation, String.format(modelPropInteger, stack.getTagCompound().getInteger(STACK_SIZE_TAG)));
		});

		for (int stackSize = 8; stackSize < 65; stackSize = stackSize + 8) {
			ModelLoader.registerItemVariants(Item.getItemFromBlock(this),
					new ModelResourceLocation(baseLocation, String.format(modelPropInteger, stackSize)));
		}
	}

	public static class PropertyStackSize extends PropertyHelper<Integer> {
		private final ImmutableSet<Integer> allowedValues;

		private PropertyStackSize() {
			super(STACK_SIZE_TAG, Integer.class);
			this.allowedValues = ImmutableSet.of(8, 16, 24, 32, 40, 48, 56, 64);
		}

		public Collection<Integer> getAllowedValues() {
			return this.allowedValues;
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			} else if (other instanceof PropertyStackSize && super.equals(other)) {
				PropertyStackSize propertyinteger = (PropertyStackSize) other;
				return this.allowedValues.equals(propertyinteger.allowedValues);
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return 31 * super.hashCode() + this.allowedValues.hashCode();
		}

		@SuppressWarnings({"Guava", "squid:S4738"})
		public Optional<Integer> parseValue(String value) {
			try {
				Integer integer = Integer.valueOf(value);
				return this.allowedValues.contains(integer) ? Optional.of(integer) : Optional.absent();
			}
			catch (NumberFormatException var3) {
				return Optional.absent();
			}
		}

		public String getName(Integer value) {
			return value.toString();
		}
	}
}
