package net.shadowmage.ancientwarfare.core.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.Maps;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;

import java.util.Map;

public abstract class RotatableStationRenderer extends BaseBakery {

    protected RotatableStationRenderer(String modelPath) {
        super(modelPath);
    }

    @Override
    protected Map<String, CCModel> applyModelTransforms(Map<String, CCModel> modelGroups, EnumFacing face, IExtendedBlockState state) {
        Map<String, CCModel> transformedGroups = Maps.newHashMap();

        for(Map.Entry<String, CCModel> group : modelGroups.entrySet()) {
            transformedGroups.put(group.getKey(), group.getValue().copy().apply(Rotation.quarterRotations[(getFacing(state).getHorizontalIndex() + 2) & 3].at(Vector3.center)));
        }

        return transformedGroups;
    }

    protected EnumFacing getFacing(IExtendedBlockState state) {
        return state.getValue(CoreProperties.UNLISTED_FACING);
    }
}
