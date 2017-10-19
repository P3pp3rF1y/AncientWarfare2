package net.shadowmage.ancientwarfare.automation.block;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBakery;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.automation.render.AutoCraftingRenderer;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileAutoCrafting;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.render.BlockRenderProperties;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

public class BlockAutoCrafting extends BlockWorksiteBase implements IBakeryProvider {

    public BlockAutoCrafting(String regName) {
        super(regName);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(BlockRenderProperties.UNLISTED_FACING).build();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        EnumFacing facing = EnumFacing.NORTH;
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof BlockRotationHandler.IRotatableTile) {
            facing = ((BlockRotationHandler.IRotatableTile) tileentity).getPrimaryFacing();
        }

        return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(BlockRenderProperties.UNLISTED_FACING, facing);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileAutoCrafting();
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
    public void registerClient() {
        NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_AUTO_CRAFT, GuiWorksiteAutoCrafting.class);

        ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
            @Override protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return AutoCraftingRenderer.MODEL_LOCATION;
            }
        });

        ModelRegistryHelper.register(AutoCraftingRenderer.MODEL_LOCATION, new CCBakeryModel(AncientWarfareCore.modID + ":model/automation/tile_auto_crafting") {
            @Override
            public TextureAtlasSprite getParticleTexture() {
                return AutoCraftingRenderer.INSTANCE.sprite;
            }
        });

        ModelLoaderHelper.registerItem(this, "automation", "normal");

        ModelBakery.registerBlockKeyGenerator(this, state -> state.getBlock().getRegistryName().toString() + "," + state.getValue(BlockRenderProperties.UNLISTED_FACING).toString());

    }

    @Override
    public IBakery getBakery() {
        return AutoCraftingRenderer.INSTANCE;
    }
}
