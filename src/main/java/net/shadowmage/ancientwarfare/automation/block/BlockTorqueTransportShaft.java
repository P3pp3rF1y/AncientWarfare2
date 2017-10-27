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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.common.property.PropertyFloat;
import net.shadowmage.ancientwarfare.automation.render.TorqueShaftRenderer;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaft;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaftHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaftLight;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaftMedium;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

public class BlockTorqueTransportShaft extends BlockTorqueTransportConduit implements IBakeryProvider {
    public static final IUnlistedProperty<Boolean> HAS_PREVIOUS = Properties.toUnlisted(PropertyBool.create("has_previous"));
    public static final IUnlistedProperty<Boolean> HAS_NEXT = Properties.toUnlisted(PropertyBool.create("has_next"));
    public static final IUnlistedProperty<Boolean> USE_INPUT = Properties.toUnlisted(PropertyBool.create("use_input"));
    public static final IUnlistedProperty<Float> INPUT_ROTATION = new PropertyFloat("input_rotation");

    public BlockTorqueTransportShaft(String regName) {
        super(regName);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        switch (state.getValue(TYPE)) {
            case LIGHT:
                return new TileTorqueShaftLight();
            case MEDIUM:
                return new TileTorqueShaftMedium();
            case HEAVY:
                return new TileTorqueShaftHeavy();
        }
        return new TileTorqueShaftLight();
    }

    @Override
    protected void addProperties(BlockStateContainer.Builder builder) {
        super.addProperties(builder);
        builder.add(HAS_PREVIOUS, HAS_NEXT, USE_INPUT, INPUT_ROTATION);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return TorqueShaftRenderer.INSTANCE.handleState((IExtendedBlockState) state, world, pos);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        //TODO static AABBs that are used to put together the total

        float min = 0.1875f, max = 0.8125f;
        float x1 = min, y1 = min, z1 = min, x2 = max, y2 = max, z2 = max;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileTorqueShaft) {
            TileTorqueShaft tile = (TileTorqueShaft) world.getTileEntity(pos);
            EnumFacing facing = tile.getPrimaryFacing();
            int s1 = facing.ordinal();
            switch (facing.getAxis()) {
                case X:
                    x1 = 0;
                    x2 = 1;
                    break;
                case Y:
                    y1 = 0;
                    y2 = 1;
                    break;
                case Z:
                    z1 = 0;
                    z2 = 1;
                    break;
            }
        }
        return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public void registerClient() {
        ModelLoaderHelper.registerItem(this, "automation", "light", true); //the actual switch for itemstack types is processed by renderer

        ModelBakery.registerBlockKeyGenerator(this, state -> state.getBlock().getRegistryName().toString()
                + "," + state.getValue(CoreProperties.UNLISTED_FACING).toString()
                + "," + state.getValue(AutomationProperties.DYNAMIC)
                + "," + state.getValue(TYPE)
                + "," + state.getValue(HAS_PREVIOUS)
                + "," + state.getValue(HAS_NEXT)
                + "," + state.getValue(INPUT_ROTATION)
                + getRotationKeyPart(state)
        );

        ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
            @Override protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                switch(state.getValue(TYPE)) {
                    case LIGHT:
                        return TorqueShaftRenderer.LIGHT_MODEL_LOCATION;
                    case MEDIUM:
                        return TorqueShaftRenderer.MEDIUM_MODEL_LOCATION;
                    default:
                        return TorqueShaftRenderer.HEAVY_MODEL_LOCATION;
                }
            }
        });

        ModelRegistryHelper.register(TorqueShaftRenderer.LIGHT_MODEL_LOCATION, new CCBakeryModel() {
            @Override
            public TextureAtlasSprite getParticleTexture() {
                return TorqueShaftRenderer.INSTANCE.getSprite(Type.LIGHT);
            }
        });

        ModelRegistryHelper.register(TorqueShaftRenderer.MEDIUM_MODEL_LOCATION, new CCBakeryModel() {
            @Override
            public TextureAtlasSprite getParticleTexture() {
                return TorqueShaftRenderer.INSTANCE.getSprite(Type.MEDIUM);
            }
        });

        ModelRegistryHelper.register(TorqueShaftRenderer.HEAVY_MODEL_LOCATION, new CCBakeryModel() {
            @Override
            public TextureAtlasSprite getParticleTexture() {
                return TorqueShaftRenderer.INSTANCE.getSprite(Type.HEAVY);
            }
        });
    }

    @Override
    public IBakery getBakery() {
        return TorqueShaftRenderer.INSTANCE;
    }
}
