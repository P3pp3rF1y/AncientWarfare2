package net.shadowmage.ancientwarfare.core.render;

import codechicken.lib.model.PerspectiveAwareModelProperties;
import codechicken.lib.model.bakery.generation.ISimpleBlockBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCModelState;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public abstract class BaseStationRenderer implements ISimpleBlockBakery {
    public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess access, BlockPos pos) {
        return state;
    }

    protected abstract CCModel getModel();

    protected abstract IconTransformation getIconTransform();

    protected EnumFacing getFacing(IExtendedBlockState state) {
        return EnumFacing.NORTH;
    }

    private static final PerspectiveAwareModelProperties MODEL_PROPERTIES;

    static {
        TRSRTransformation thirdPerson = TransformUtils.get(0, 2.5f, 0, 75, 225, 0, 0.375f);
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> defaultBlockBuilder = ImmutableMap.builder();
        defaultBlockBuilder.put(ItemCameraTransforms.TransformType.GUI, TransformUtils.get(0, 0, 0, 30, 225, 0, 0.625f));
        defaultBlockBuilder.put(ItemCameraTransforms.TransformType.GROUND, TransformUtils.get(0, 3, 0, 0, 0, 0, 0.25f));
        defaultBlockBuilder.put(ItemCameraTransforms.TransformType.FIXED, TransformUtils.get(0, 0, 0, 0, 0, 0, 0.5f));
        defaultBlockBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
        defaultBlockBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TransformUtils.leftify(thirdPerson));
        defaultBlockBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, TransformUtils.get(0, 0, 0, 0, 315, 0, 0.4f));
        defaultBlockBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, TransformUtils.get(0, 0, 0, 0, 315, 0, 0.4f));
        MODEL_PROPERTIES = new PerspectiveAwareModelProperties(new CCModelState(defaultBlockBuilder.build()), true, true);
    }

    @Nonnull
    @Override
    public List<BakedQuad> bakeQuads(@Nullable EnumFacing face, IExtendedBlockState state) {
        if (face != null) {
            return Collections.emptyList();
        }

        BakingVertexBuffer buffer = BakingVertexBuffer.create();
        buffer.begin(7, DefaultVertexFormats.ITEM);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.bind(buffer);

        getModel().copy().apply(Rotation.quarterRotations[(getFacing(state).getHorizontalIndex() + 2) & 3].at(Vector3.center)).render(ccrs, getIconTransform());

        buffer.finishDrawing();
        return buffer.bake();
    }

    @Nonnull
    @Override
    public List<BakedQuad> bakeItemQuads(@Nullable EnumFacing face, ItemStack stack) {
        BakingVertexBuffer buffer = BakingVertexBuffer.create();
        buffer.begin(7, DefaultVertexFormats.ITEM);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.bind(buffer);

        getModel().render(ccrs, getIconTransform());

        buffer.finishDrawing();
        return buffer.bake();
    }

    @Override
    public PerspectiveAwareModelProperties getModelProperties(ItemStack stack) {
        return MODEL_PROPERTIES;
    }
}
