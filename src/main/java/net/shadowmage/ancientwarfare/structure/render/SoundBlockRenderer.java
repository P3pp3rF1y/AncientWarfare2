package net.shadowmage.ancientwarfare.structure.render;

import codechicken.lib.model.bakery.generation.ISimpleBlockBakery;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.structure.block.BlockSoundBlock;
import net.shadowmage.ancientwarfare.structure.tile.TileSoundBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class SoundBlockRenderer implements ISimpleBlockBakery {

    public static ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(new ResourceLocation(AncientWarfareCore.modID, "structure/sound_block"), "normal");

    public static SoundBlockRenderer INSTANCE = new SoundBlockRenderer();
    private SoundBlockRenderer(){}

    @Nonnull
    @Override
    public List<BakedQuad> bakeQuads(@Nullable EnumFacing face, IExtendedBlockState state) {
        IBlockState disguiseState = Blocks.JUKEBOX.getDefaultState();
        String blockState = state.getValue(BlockSoundBlock.DISGUISE_BLOCK);
        if (blockState != null) {
            String[] parts = blockState.split("\\|");
            if (parts.length == 2) {
                disguiseState = Block.getBlockFromName(parts[0]).getStateFromMeta(Integer.valueOf(parts[1]));
            }
        }
        return Minecraft.getMinecraft().modelManager.getBlockModelShapes().getModelForState(disguiseState).getQuads(disguiseState, face, 0);
    }

    @Override
    public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess access, BlockPos pos) {

        TileEntity tileEntity = access.getTileEntity(pos);
        String registryName = String.join("|", Blocks.JUKEBOX.getRegistryName().toString(), "0");
        if(tileEntity instanceof TileSoundBlock) {
            IBlockState disguiseState = ((TileSoundBlock) tileEntity).getDisguiseState();
            if (disguiseState != null) {
                registryName = String.join("|", disguiseState.getBlock().getRegistryName().toString(), Integer.toString(disguiseState.getBlock().getMetaFromState(disguiseState)));
            }
        }
        return state.withProperty(BlockSoundBlock.DISGUISE_BLOCK, registryName);
    }

    @Nonnull
    @Override
    public List<BakedQuad> bakeItemQuads(@Nullable EnumFacing face, ItemStack stack) {
        IBlockState defaultState = Blocks.JUKEBOX.getDefaultState();
        return Minecraft.getMinecraft().modelManager.getBlockModelShapes().getModelForState(defaultState).getQuads(defaultState, face, 0);
    }
}
