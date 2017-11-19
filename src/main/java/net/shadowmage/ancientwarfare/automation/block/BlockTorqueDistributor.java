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
import net.shadowmage.ancientwarfare.automation.render.TorqueDistributorRenderer;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileDistributorHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileDistributorLight;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileDistributorMedium;
import net.shadowmage.ancientwarfare.core.render.BlockStateKeyGenerator;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

public class BlockTorqueDistributor extends BlockTorqueTransportSided implements IBakeryProvider {
    protected BlockTorqueDistributor(String regName) {
        super(regName);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return TorqueDistributorRenderer.INSTANCE.handleState((IExtendedBlockState) state, world, pos);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        switch (state.getValue(AutomationProperties.TIER)) {
            case LIGHT:
                return new TileDistributorLight();
            case MEDIUM:
                return new TileDistributorMedium();
            case HEAVY:
                return new TileDistributorHeavy();
        }
        return new TileDistributorLight();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerClient() {
        ModelLoaderHelper.registerItem(this, "automation", "light", false); //the actual switch for itemstack types is processed by renderer

        ModelBakery.registerBlockKeyGenerator(this, new BlockStateKeyGenerator.Builder()
                .addKeyProperties(AutomationProperties.TIER)
                .addKeyProperties(CoreProperties.UNLISTED_FACING, AutomationProperties.DYNAMIC)
                .addKeyProperties(BlockTorqueTransportSided.CONNECTIONS)
                .addKeyProperties(o -> String.format("%.6f",o), AutomationProperties.ROTATIONS).build());

        ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
            @Override protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                switch(state.getValue(AutomationProperties.TIER)) {
                    case LIGHT:
                        return TorqueDistributorRenderer.LIGHT_MODEL_LOCATION;
                    case MEDIUM:
                        return TorqueDistributorRenderer.MEDIUM_MODEL_LOCATION;
                    default:
                        return TorqueDistributorRenderer.HEAVY_MODEL_LOCATION;
                }
            }
        });

        ModelRegistryHelper.register(TorqueDistributorRenderer.LIGHT_MODEL_LOCATION, new CCBakeryModel() {
            @Override
            public TextureAtlasSprite getParticleTexture() {
                return TorqueDistributorRenderer.INSTANCE.getSprite(TorqueTier.LIGHT);
            }
        });

        ModelRegistryHelper.register(TorqueDistributorRenderer.MEDIUM_MODEL_LOCATION, new CCBakeryModel() {
            @Override
            public TextureAtlasSprite getParticleTexture() {
                return TorqueDistributorRenderer.INSTANCE.getSprite(TorqueTier.MEDIUM);
            }
        });

        ModelRegistryHelper.register(TorqueDistributorRenderer.HEAVY_MODEL_LOCATION, new CCBakeryModel() {
            @Override
            public TextureAtlasSprite getParticleTexture() {
                return TorqueDistributorRenderer.INSTANCE.getSprite(TorqueTier.HEAVY);
            }
        });
    }

    @Override
    public IBakery getBakery() {
        return TorqueDistributorRenderer.INSTANCE;
    }
}
