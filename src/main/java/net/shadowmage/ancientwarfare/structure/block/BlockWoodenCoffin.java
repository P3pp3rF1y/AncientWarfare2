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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockWoodenCoffin;
import net.shadowmage.ancientwarfare.structure.render.ParticleOnlyModel;
import net.shadowmage.ancientwarfare.structure.render.WoodenCoffinRenderer;
import net.shadowmage.ancientwarfare.structure.tile.TileWoodenCoffin;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class BlockWoodenCoffin extends BlockCoffin<TileWoodenCoffin> {
	private static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

	public BlockWoodenCoffin() {
		super(Material.WOOD, "wooden_coffin", TileWoodenCoffin::new, TileWoodenCoffin.class);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (Variant variant : Variant.values()) {
			items.add(ItemBlockWoodenCoffin.getVariantStack(variant));
		}
	}

	@Override
	protected List<IProperty> getAdditionalProperties() {
		return Collections.singletonList(VARIANT);
	}

	@Override
	protected void setPlacementProperties(World world, BlockPos pos, EntityLivingBase placer, ItemStack stack, TileWoodenCoffin te) {
		boolean upright = !ItemBlockWoodenCoffin.canPlaceHorizontal(world, pos, placer.getHorizontalFacing(), placer);
		te.setUpright(upright);
		te.setVariant(ItemBlockWoodenCoffin.getVariant(stack));
	}

	protected ItemStack getVariantStack(IVariant variant) {
		return ItemBlockWoodenCoffin.getVariantStack(variant);
	}

	@Override
	protected IVariant getDefaultVariant() {
		return Variant.getDefault();
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.withProperty(VARIANT, WorldTools.getTile(world, pos, TileWoodenCoffin.class).map(TileWoodenCoffin::getVariant).orElse(Variant.OAK));
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

		ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new WoodenCoffinRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWoodenCoffin.class, new WoodenCoffinRenderer());
	}

	public enum Variant implements IVariant {
		OAK("oak", "planks_oak"),
		BIRCH("birch", "planks_birch"),
		SPRUCE("spruce", "planks_spruce"),
		JUNGLE("jungle", "planks_jungle"),
		DARK_OAK("dark_oak", "planks_big_oak"),
		ACACIA("acacia", "planks_acacia");

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
			return OAK;
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
			return NAME_TO_VARIANT.getOrDefault(name, OAK);
		}

		public String getBlockTexture() {
			return blockTexture;
		}
	}
}
