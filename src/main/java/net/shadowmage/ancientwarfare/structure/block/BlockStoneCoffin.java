package net.shadowmage.ancientwarfare.structure.block;

import codechicken.lib.model.ModelRegistryHelper;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockStoneCoffin;
import net.shadowmage.ancientwarfare.structure.render.ParticleOnlyModel;
import net.shadowmage.ancientwarfare.structure.render.StoneCoffinRenderer;
import net.shadowmage.ancientwarfare.structure.tile.TileStoneCoffin;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class BlockStoneCoffin extends BlockCoffin<TileStoneCoffin> {
	private static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0D, 0D, 1D, 14.1 / 16D, 1D);

	public BlockStoneCoffin() {
		super(Material.ROCK, "stone_coffin", TileStoneCoffin::new, TileStoneCoffin.class);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (Variant variant : Variant.values()) {
			items.add(ItemBlockStoneCoffin.getVariantStack(variant));
		}
	}

	@Override
	protected List<IProperty> getAdditionalProperties() {
		return Collections.singletonList(VARIANT);
	}

	@Override
	protected void setPlacementProperties(World world, BlockPos pos, EntityLivingBase placer, ItemStack stack, TileStoneCoffin te) {
		te.setVariant(ItemBlockStoneCoffin.getVariant(stack));
	}

	@Override
	protected ItemStack getVariantStack(IVariant variant) {
		return ItemBlockStoneCoffin.getVariantStack(variant);
	}

	@Override
	protected IVariant getDefaultVariant() {
		return Variant.getDefault();
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.withProperty(VARIANT, WorldTools.getTile(world, pos, TileStoneCoffin.class).map(TileStoneCoffin::getVariant).orElse(Variant.STONE));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		Map<Variant, ModelResourceLocation> variantModels = new EnumMap<>(Variant.class);
		for (Variant variant : Variant.values()) {
			ModelResourceLocation modelLocation = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":structure/wooden_coffin", variant.getName());
			variantModels.put(variant, modelLocation);
			ModelRegistryHelper.register(modelLocation, new ParticleOnlyModel(variant.getBlockTexture()));
		}

		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return variantModels.get(state.getValue(VARIANT));
			}
		});
		ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new StoneCoffinRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileStoneCoffin.class, new StoneCoffinRenderer());
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	public enum Variant implements IVariant {
		STONE("stone", "stone"),
		SANDSTONE("sandstone", "sandstone_top"),
		PRISMARINE("prismarine", "prismarine_bricks"),
		DEMONIC("demonic", "nether_brick");

		private String name;
		private String blockTexture;

		Variant(String name, String blockTexture) {
			this.name = name;
			this.blockTexture = blockTexture;
		}

		@Override
		public String getName() {
			return name;
		}

		public static Variant getDefault() {
			return STONE;
		}

		private static final ImmutableMap<String, Variant> NAME_TO_VARIANT;

		static {
			ImmutableMap.Builder<String, Variant> builder = new ImmutableMap.Builder<>();
			for (Variant variant : values()) {
				builder.put(variant.name, variant);
			}
			NAME_TO_VARIANT = builder.build();
		}

		public static Variant fromName(String name) {
			return NAME_TO_VARIANT.getOrDefault(name, STONE);
		}

		public String getBlockTexture() {
			return blockTexture;
		}
	}
}
