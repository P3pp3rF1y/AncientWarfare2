package net.shadowmage.ancientwarfare.automation.block;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
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
import net.shadowmage.ancientwarfare.automation.render.TorqueJunctionRenderer;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileConduitHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileConduitLight;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileConduitMedium;
import net.shadowmage.ancientwarfare.core.render.BlockStateKeyGenerator;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

public class BlockTorqueJunction extends BlockTorqueTransportSided implements IBakeryProvider {
	public BlockTorqueJunction(String regName) {
		super(regName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		return TorqueJunctionRenderer.INSTANCE.handleState((IExtendedBlockState) state, world, pos);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		switch (state.getValue(AutomationProperties.TIER)) {
			case LIGHT:
				return new TileConduitLight();
			case MEDIUM:
				return new TileConduitMedium();
			case HEAVY:
				return new TileConduitHeavy();
		}
		return new TileConduitLight();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, "automation", "light", false); //the actual switch for itemstack types is processed by renderer

		ModelBakery.registerBlockKeyGenerator(this, new BlockStateKeyGenerator.Builder().addKeyProperties(AutomationProperties.TIER).addKeyProperties(CoreProperties.UNLISTED_FACING, AutomationProperties.DYNAMIC).addKeyProperties(BlockTorqueTransportSided.CONNECTIONS).addKeyProperties(o -> String.format("%.6f", o), AutomationProperties.ROTATIONS).build());

		ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
			@Override
			@SideOnly(Side.CLIENT)
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				switch (state.getValue(AutomationProperties.TIER)) {
					case LIGHT:
						return TorqueJunctionRenderer.LIGHT_MODEL_LOCATION;
					case MEDIUM:
						return TorqueJunctionRenderer.MEDIUM_MODEL_LOCATION;
					default:
						return TorqueJunctionRenderer.HEAVY_MODEL_LOCATION;
				}
			}
		});

		ModelRegistryHelper.register(TorqueJunctionRenderer.LIGHT_MODEL_LOCATION, new CCBakeryModel() {
			@Override
			@SideOnly(Side.CLIENT)
			public TextureAtlasSprite getParticleTexture() {
				return TorqueJunctionRenderer.INSTANCE.getSprite(TorqueTier.LIGHT);
			}
		});

		ModelRegistryHelper.register(TorqueJunctionRenderer.MEDIUM_MODEL_LOCATION, new CCBakeryModel() {
			@Override
			@SideOnly(Side.CLIENT)
			public TextureAtlasSprite getParticleTexture() {
				return TorqueJunctionRenderer.INSTANCE.getSprite(TorqueTier.MEDIUM);
			}
		});

		ModelRegistryHelper.register(TorqueJunctionRenderer.HEAVY_MODEL_LOCATION, new CCBakeryModel() {
			@Override
			@SideOnly(Side.CLIENT)
			public TextureAtlasSprite getParticleTexture() {
				return TorqueJunctionRenderer.INSTANCE.getSprite(TorqueTier.HEAVY);
			}
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBakery getBakery() {
		return TorqueJunctionRenderer.INSTANCE;
	}
}
