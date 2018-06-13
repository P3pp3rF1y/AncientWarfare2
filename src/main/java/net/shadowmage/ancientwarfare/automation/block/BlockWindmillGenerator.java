package net.shadowmage.ancientwarfare.automation.block;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.generation.IBakery;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.automation.render.WindmillGeneratorRenderer;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileWindmillController;

public class BlockWindmillGenerator extends BlockTorqueGenerator implements IBakeryProvider {

	public BlockWindmillGenerator(String regName) {
		super(regName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return WindmillGeneratorRenderer.INSTANCE.handleState((IExtendedBlockState) state, world, pos);
	}

	@Override
	public boolean invertFacing() {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileWindmillController();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBakery getBakery() {
		return WindmillGeneratorRenderer.INSTANCE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return WindmillGeneratorRenderer.MODEL_LOCATION;
			}
		});

		ModelRegistryHelper.register(WindmillGeneratorRenderer.MODEL_LOCATION, new CCBakeryModel() {
			@Override
			@SideOnly(Side.CLIENT)
			public TextureAtlasSprite getParticleTexture() {
				return WindmillGeneratorRenderer.INSTANCE.sprite;
			}
		});
	}

}
