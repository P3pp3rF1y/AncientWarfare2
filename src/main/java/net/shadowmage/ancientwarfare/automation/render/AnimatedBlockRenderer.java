package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Transformation;
import com.google.common.collect.Maps;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.core.render.RotatableBlockRenderer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AnimatedBlockRenderer extends RotatableBlockRenderer {
    protected AnimatedBlockRenderer(String modelPath) {
        super(modelPath);
    }

    protected Map<String, CCModel> removeGroups(Function<String, Boolean> filter) {
        Map<String, CCModel> ret = Maps.newHashMap();

        Iterator<Map.Entry<String, CCModel>> iterator = groups.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, CCModel> entry = iterator.next();

            if(filter.apply(entry.getKey())) {
                ret.put(entry.getKey(), entry.getValue());
                iterator.remove();
            }
        }

        return ret;
    }

    protected Collection<CCModel> rotateModels(Collection<CCModel> groups, EnumFacing frontFacing, Transformation transform) {
        return groups.stream().map(e -> rotateFacing(e.copy().apply(transform), frontFacing)).collect(Collectors.toSet());
    }
}
