package net.shadowmage.ancientwarfare.structure.block;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.structure.render.ParticleOnlyModel;
import net.shadowmage.ancientwarfare.structure.render.ProtectionFlagRenderer;
import net.shadowmage.ancientwarfare.structure.tile.TileDecorativeFlag;

import javax.annotation.Nullable;

public class BlockDecorativeFlag extends BlockFlag {
	public BlockDecorativeFlag() {
		super(Material.WOOD, "decorative_flag");
		setResistance(5.0F);
		setHardness(2.0F);
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileDecorativeFlag();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		//noinspection ConstantConditions
		ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "normal");
		ModelRegistryHelper.registerItemRenderer(Item.getItemFromBlock(this), new ProtectionFlagRenderer());
		ModelRegistryHelper.register(modelLocation, ParticleOnlyModel.INSTANCE);
		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return modelLocation;
			}
		});

		ClientRegistry.bindTileEntitySpecialRenderer(TileDecorativeFlag.class, new ProtectionFlagRenderer());
	}
}
