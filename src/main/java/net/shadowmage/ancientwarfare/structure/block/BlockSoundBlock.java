package net.shadowmage.ancientwarfare.structure.block;

import codechicken.lib.block.property.unlisted.UnlistedStringProperty;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.model.bakery.generation.IBakery;
import codechicken.lib.texture.TextureUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.render.BlockStateKeyGenerator;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.structure.gui.GuiSoundBlock;
import net.shadowmage.ancientwarfare.structure.render.SoundBlockRenderer;
import net.shadowmage.ancientwarfare.structure.tile.TileSoundBlock;

import javax.annotation.Nonnull;

public class BlockSoundBlock extends BlockBaseStructure implements IBakeryProvider {

    public static final IUnlistedProperty<String> DISGUISE_BLOCK = new UnlistedStringProperty("disguise");

    public BlockSoundBlock() {
        super(Material.ROCK, "sound_block");
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(DISGUISE_BLOCK).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return SoundBlockRenderer.INSTANCE.handleState((IExtendedBlockState) state, world, pos);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileSoundBlock();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        @Nonnull ItemStack itemStack = player.getHeldItem(hand);
        if(!itemStack.isEmpty() && itemStack.getItem() instanceof ItemBlock){
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof TileSoundBlock) {
                ((TileSoundBlock)tileEntity).setDisguiseState(itemStack);
            }
        }
        if (!world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SOUND_BLOCK, pos);
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerClient() {
        ModelLoader.setCustomStateMapper(this, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return SoundBlockRenderer.MODEL_LOCATION;
            }
        });
        ModelRegistryHelper.register(SoundBlockRenderer.MODEL_LOCATION, new CCBakeryModel() {
            @Override
            public TextureAtlasSprite getParticleTexture() {
                return TextureUtils.getTexture("minecraft:blocks/jukebox_side");
            }
        });

        ModelLoaderHelper.registerItem(this, SoundBlockRenderer.MODEL_LOCATION);

        ModelBakery.registerBlockKeyGenerator(this,
                new BlockStateKeyGenerator.Builder().addKeyProperties(DISGUISE_BLOCK).build());

        NetworkHandler.registerGui(NetworkHandler.GUI_SOUND_BLOCK, GuiSoundBlock.class);
    }

    @Override
    public IBakery getBakery() {
        return SoundBlockRenderer.INSTANCE;
    }
}
