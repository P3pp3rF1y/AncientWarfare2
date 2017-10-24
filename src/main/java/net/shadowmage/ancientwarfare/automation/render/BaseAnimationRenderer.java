package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.model.bakery.ModelBakery;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.common.property.IExtendedBlockState;

import java.util.Map;

public abstract class BaseAnimationRenderer<T extends TileEntity> extends FastTESR<T> {
    private static BlockRendererDispatcher blockRenderer;

    private Map<Integer, Integer> counter = Maps.newHashMap();

    @Override
    public void renderTileEntityFast(T te, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder renderer) {
        if(blockRenderer == null) {
            blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
        }
        BlockPos pos = te.getPos();
        IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
        IBlockState state = world.getBlockState(pos);
        if(state instanceof IExtendedBlockState)
        {
            IExtendedBlockState exState = (IExtendedBlockState)state;

            exState = handleState(te, partialTicks, exState);

            int hashcode = getModelHashCode(exState);
            int count = counter.getOrDefault(hashcode, 0);
            IBakedModel model;

            //making sure that we don't overfill cache with models that are not going to be used anymore
            if (count > 10) {
                model = ModelBakery.getCachedModel(exState);
            } else {
                counter.put(hashcode, count + 1);
                model = ModelBakery.generateModel(exState);
            }

            renderer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());

            blockRenderer.getBlockModelRenderer().renderModel(world, model, exState, pos, renderer, false);
        }
    }

    protected abstract IExtendedBlockState handleState(T te, float partialTicks, IExtendedBlockState state);

    protected abstract int getModelHashCode(IExtendedBlockState exState);
}
