package net.shadowmage.ancientwarfare.structure.render.statue;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

import java.util.Map;

public class BipedStatueModel<T extends ModelBiped> extends StatueModelBase<T> {
	BipedStatueModel(T model) {super(model);}

	@Override
	protected Map<String, ModelRenderer> getNameRendererMap() {
		return new ImmutableMap.Builder<String, ModelRenderer>()
				.put("Head", model.bipedHead)
				.put("Headwear", model.bipedHeadwear)
				.put("Body", model.bipedBody)
				.put("Right Arm", model.bipedRightArm)
				.put("Left Arm", model.bipedLeftArm)
				.put("Right Leg", model.bipedRightLeg)
				.put("Left Leg", model.bipedLeftLeg)
				.build();
	}
}
