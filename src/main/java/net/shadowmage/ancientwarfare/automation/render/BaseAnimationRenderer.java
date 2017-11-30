package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
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
import net.shadowmage.ancientwarfare.core.render.BaseBakery;

import java.util.Collection;
import java.util.Map;

public abstract class BaseAnimationRenderer<T extends TileEntity> extends FastTESR<T> {
    private final ITESRRenderer bakery;

    public BaseAnimationRenderer(ITESRRenderer bakery) {
        this.bakery = bakery;
    }

    @Override
    public void renderTileEntityFast(T te, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder renderer) {
        BlockPos pos = te.getPos();
        IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
        IBlockState state = world.getBlockState(pos);
        if(state instanceof IExtendedBlockState)
        {
            IExtendedBlockState exState = (IExtendedBlockState)state;
            exState = handleState(te, partialTicks, exState);
            renderer.setTranslation(x, y, z);
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.reset();
            ccrs.bind(renderer);
            ccrs.setBrightness(world, pos);

            bakery.renderTransformedBlockModels(ccrs, exState);
        }
    }

    protected abstract IExtendedBlockState handleState(T te, float partialTicks, IExtendedBlockState state);
}
