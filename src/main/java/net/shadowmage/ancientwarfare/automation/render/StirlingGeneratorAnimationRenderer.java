package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.model.bakery.ModelBakery;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileStirlingGenerator;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;

public class StirlingGeneratorAnimationRenderer extends FastTESR<TileStirlingGenerator> {
    protected static BlockRendererDispatcher blockRenderer;

    @Override
    public void renderTileEntityFast(TileStirlingGenerator te, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder renderer) {
        if(blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
        BlockPos pos = te.getPos();
        IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
        IBlockState state = world.getBlockState(pos);
        if(state instanceof IExtendedBlockState)
        {
            IExtendedBlockState exState = (IExtendedBlockState)state;

            EnumFacing facing = te.getPrimaryFacing();
            exState = exState.withProperty(CoreProperties.UNLISTED_FACING, facing);
            exState = exState.withProperty(AutomationProperties.ROTATION, -te.getClientOutputRotation(facing, partial));
            exState = exState.withProperty(AutomationProperties.DYNAMIC, true);

            IBakedModel model = ModelBakery.getCachedModel(exState);

            renderer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());

            blockRenderer.getBlockModelRenderer().renderModel(world, model, exState, pos, renderer, false);
        }
    }
}
