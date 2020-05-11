package net.shadowmage.ancientwarfare.structure.block;

import codechicken.lib.model.ModelRegistryHelper;
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
import net.shadowmage.ancientwarfare.structure.item.ItemBlockCoffin;
import net.shadowmage.ancientwarfare.structure.render.CoffinRenderer;
import net.shadowmage.ancientwarfare.structure.render.ParticleOnlyModel;
import net.shadowmage.ancientwarfare.structure.tile.TileWoodenCoffin;

public class BlockWoodenCoffin extends BlockCoffin<TileWoodenCoffin> {
	public BlockWoodenCoffin() {
		super(Material.WOOD, "coffin", TileWoodenCoffin::new, TileWoodenCoffin.class);
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (int variant = 1; variant <= 6; variant++) {
			items.add(ItemBlockCoffin.getVariantStack(variant));
		}
	}

	@Override
	protected void setPlacementProperties(World world, BlockPos pos, EntityLivingBase placer, ItemStack stack, TileWoodenCoffin te) {
		boolean upright = !ItemBlockCoffin.canPlaceHorizontal(world, pos, placer.getHorizontalFacing(), placer);
		te.setUpright(upright);
		te.setVariant(ItemBlockCoffin.getVariant(stack));
	}

	protected ItemStack getVariantStack(int variant) {
		return ItemBlockCoffin.getVariantStack(variant);
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
}
