package net.shadowmage.ancientwarfare.automation.block;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBakery;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.common.property.PropertyFloat;
import net.shadowmage.ancientwarfare.automation.render.WindmillBladeRenderer;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileWindmillBlade;
import net.shadowmage.ancientwarfare.core.render.BlockStateKeyGenerator;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.UNLISTED_HORIZONTAL_FACING;

public class BlockWindmillBlade extends BlockBaseAutomation implements IBakeryProvider {
    public static final IUnlistedProperty<Boolean> FORMED = Properties.toUnlisted(PropertyBool.create("formed"));
    public static final IUnlistedProperty<Boolean> IS_CONTROL = Properties.toUnlisted(PropertyBool.create("is_control"));
    public static final IUnlistedProperty<Integer> SIZE = Properties.toUnlisted(PropertyInteger.create("height", 0, 30));
    public static final IUnlistedProperty<Float> ROTATION = new PropertyFloat("rotation");

    public BlockWindmillBlade(String regName) {
        super(Material.WOOD, regName);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this)
                .add(FORMED, IS_CONTROL, SIZE, ROTATION, UNLISTED_HORIZONTAL_FACING, AutomationProperties.DYNAMIC)
                .build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return WindmillBladeRenderer.INSTANCE.handleState((IExtendedBlockState) state, world, pos);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        TileWindmillBlade te = (TileWindmillBlade) world.getTileEntity(pos);
        te.blockPlaced();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileWindmillBlade te = (TileWindmillBlade) world.getTileEntity(pos);
        super.breakBlock(world, pos, state);
        te.blockBroken();//have to call post block-break so that the tile properly sees the block/tile as gone //TODO invalidate?
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileWindmillBlade();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 60;
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return 20;
    }

    @Override
    public void registerClient() {
        ModelLoaderHelper.registerItem(this, WindmillBladeRenderer.MODEL_LOCATION);

        ModelBakery.registerBlockKeyGenerator(this, new BlockStateKeyGenerator.Builder()
                .addKeyProperties(FORMED, IS_CONTROL, SIZE, UNLISTED_HORIZONTAL_FACING, AutomationProperties.DYNAMIC)
                .addKeyProperties(o -> String.format("%.6f", o), ROTATION).build());

        ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
            @Override protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return WindmillBladeRenderer.MODEL_LOCATION;
            }
        });

        ModelRegistryHelper.register(WindmillBladeRenderer.MODEL_LOCATION, new CCBakeryModel() {
            @Override
            public TextureAtlasSprite getParticleTexture() {
                return WindmillBladeRenderer.INSTANCE.cubeSprite;
            }
        });


    }

    @Override
    public IBakery getBakery() {
        return WindmillBladeRenderer.INSTANCE;
    }
}
