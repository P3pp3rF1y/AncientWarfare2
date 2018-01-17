package net.shadowmage.ancientwarfare.core.block;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBakery;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.gui.research.GuiResearchStation;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegistrar;
import net.shadowmage.ancientwarfare.core.render.BlockStateKeyGenerator;
import net.shadowmage.ancientwarfare.core.render.ResearchStationRenderer;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;
import net.shadowmage.ancientwarfare.core.tile.TileResearchStation;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

public class BlockResearchStation extends BlockBaseCore implements IRotatableBlock, IBakeryProvider, IClientRegistrar {

    public BlockResearchStation() {
        super(Material.ROCK, "research_station");
        setHardness(2.f);

        AncientWarfareCore.proxy.addClientRegistrar(this);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(CoreProperties.UNLISTED_HORIZONTAL_FACING).build();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        EnumFacing facing = EnumFacing.NORTH;
        TileEntity tileentity = world.getTileEntity(pos);

        if (tileentity instanceof TileResearchStation) {
            facing = ((TileResearchStation) tileentity).getPrimaryFacing();
        }

        return ((IExtendedBlockState) super.getExtendedState(state, world, pos)).withProperty(CoreProperties.UNLISTED_HORIZONTAL_FACING, facing);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileResearchStation();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof IInteractableTile && ((IInteractableTile) te).onBlockClicked(player, hand);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
		InventoryTools.dropInventoryInWorld(world.getTileEntity(pos));
		super.breakBlock(world, pos, state);
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
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public boolean invertFacing() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerClient() {
        NetworkHandler.registerGui(NetworkHandler.GUI_RESEARCH_STATION, GuiResearchStation.class);

        ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
            @Override protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return ResearchStationRenderer.MODEL_LOCATION;
            }
        });

        ModelRegistryHelper.register(ResearchStationRenderer.MODEL_LOCATION, new CCBakeryModel() {
            @Override
            public TextureAtlasSprite getParticleTexture() {
                return ResearchStationRenderer.INSTANCE.sprite;
            }
        });

        ModelLoaderHelper.registerItem(this, ResearchStationRenderer.MODEL_LOCATION);

        ModelBakery.registerBlockKeyGenerator(this,
                new BlockStateKeyGenerator.Builder().addKeyProperties(CoreProperties.UNLISTED_HORIZONTAL_FACING).build());
    }

    @Override
    public IBakery getBakery() {
        return ResearchStationRenderer.INSTANCE;
    }
}
