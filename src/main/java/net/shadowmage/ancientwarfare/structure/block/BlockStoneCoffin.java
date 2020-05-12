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
import net.shadowmage.ancientwarfare.core.util.ParticleUtils;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockStoneCoffin;
import net.shadowmage.ancientwarfare.structure.render.ParticleOnlyModel;
import net.shadowmage.ancientwarfare.structure.render.StoneCoffinRenderer;
import net.shadowmage.ancientwarfare.structure.tile.TileStoneCoffin;

import java.util.Map;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class BlockStoneCoffin extends BlockCoffin<TileStoneCoffin> {
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

	private static final Map<Integer, Integer> PARTICLES = ImmutableMap.of(
			1, 1,
			2, 24,
			3, 168,
			4, 112
	);

	@Override
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	public enum Variant implements IVariant {
		STONE("stone"),
		SANDSTONE("sandstone"),
		PRISMARINE("prismarine"),
		DEMONIC("demonic");

		private String name;

		Variant(String name) {
			this.name = name;
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
	}
}
