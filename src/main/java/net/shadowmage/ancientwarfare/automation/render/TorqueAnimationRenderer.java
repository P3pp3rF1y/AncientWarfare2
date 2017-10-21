package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.model.bakery.ModelBakery;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
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
import net.minecraftforge.common.property.IUnlistedProperty;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class TorqueAnimationRenderer extends FastTESR<TileTorqueBase> {
    protected static BlockRendererDispatcher blockRenderer;

    private Map<Integer, Integer> counter = Maps.newHashMap();

    @Override
    public void renderTileEntityFast(TileTorqueBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder renderer) {
        if(blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
        BlockPos pos = te.getPos();
        IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
        IBlockState state = world.getBlockState(pos);
        if(state instanceof IExtendedBlockState)
        {
            IExtendedBlockState exState = (IExtendedBlockState)state;

            EnumFacing facing = te.getPrimaryFacing();
            exState = exState.withProperty(CoreProperties.UNLISTED_FACING, facing);
            ImmutableMap<IUnlistedProperty<?>, Optional<?>> properties = exState.getUnlistedProperties();
            float[] rotations = new float[6];
            for(EnumFacing f : EnumFacing.VALUES) {
                if(properties.containsKey(AutomationProperties.ROTATIONS[f.getIndex()])) {
                    float rotation = te.getClientOutputRotation(f, partial);
                    rotations[f.getIndex()] = rotation;
                    exState = exState.withProperty(AutomationProperties.ROTATIONS[f.getIndex()], rotation);

                } else {
                    exState = exState.withProperty(AutomationProperties.ROTATIONS[f.getIndex()], 0f);
                }
            }
            exState = exState.withProperty(AutomationProperties.DYNAMIC, true);

            int hashcode = Arrays.hashCode(rotations);
            int count = counter.containsKey(hashcode) ? counter.get(hashcode) : 0;
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
}
