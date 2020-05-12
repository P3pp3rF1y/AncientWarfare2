package net.shadowmage.ancientwarfare.structure.block;

import codechicken.lib.model.ModelRegistryHelper;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockWoodenCoffin;
import net.shadowmage.ancientwarfare.structure.render.CoffinRenderer;
import net.shadowmage.ancientwarfare.structure.render.ParticleOnlyModel;
import net.shadowmage.ancientwarfare.structure.tile.TileWoodenCoffin;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class BlockWoodenCoffin extends BlockCoffin<TileWoodenCoffin> {
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
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return CoffinRenderer.MODEL_LOCATION;
			}
		});
		ModelRegistryHelper.register(CoffinRenderer.MODEL_LOCATION, ParticleOnlyModel.INSTANCE);
		ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new CoffinRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWoodenCoffin.class, new CoffinRenderer());
	}

	public enum Variant implements IVariant {
		OAK("oak"),
		BIRCH("birch"),
		SPRUCE("spruce"),
		JUNGLE("jungle"),
		DARK_OAK("dark_oak"),
		ACACIA("acacia");

		private String name;

		Variant(String name) {
			this.name = name;
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
	}
}
