package net.shadowmage.ancientwarfare.automation.block;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBakery;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
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
import net.shadowmage.ancientwarfare.automation.render.WaterwheelGeneratorRenderer;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileWaterwheelGenerator;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

public class BlockWaterwheelGenerator extends BlockTorqueGenerator implements IBakeryProvider {
    public static final PropertyBool VALID_SETUP = PropertyBool.create("valid_setup");

    public BlockWaterwheelGenerator(String regName) {
        super(regName);
    }

    @Override
    protected void addProperties(BlockStateContainer.Builder builder) {
        builder.add(VALID_SETUP);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return WaterwheelGeneratorRenderer.INSTANCE.handleState((IExtendedBlockState) state, world, pos);
    }

    @Override
    public boolean invertFacing() {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileWaterwheelGenerator();
    }

    @Override
    public void registerClient() {
        ModelLoaderHelper.registerItem(this, "automation", "normal");

        ModelBakery.registerBlockKeyGenerator(this, state -> state.getBlock().getRegistryName().toString()
                + "," + state.getValue(CoreProperties.UNLISTED_FACING).toString()
                + "," + state.getValue(AutomationProperties.DYNAMIC)
                + getRotationKeyPart(state)
                + "," + state.getValue(VALID_SETUP)
        );

        ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
            @Override protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return WaterwheelGeneratorRenderer.MODEL_LOCATION;
            }
        });

        ModelRegistryHelper.register(WaterwheelGeneratorRenderer.MODEL_LOCATION, new CCBakeryModel() {
            @Override
            public TextureAtlasSprite getParticleTexture() {
                return WaterwheelGeneratorRenderer.INSTANCE.sprite;
            }
        });
    }

    @Override
    public IBakery getBakery() {
        return WaterwheelGeneratorRenderer.INSTANCE;
    }
}
