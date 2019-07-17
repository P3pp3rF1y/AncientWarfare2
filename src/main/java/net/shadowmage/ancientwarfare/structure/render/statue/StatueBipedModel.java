package net.shadowmage.ancientwarfare.structure.render.statue;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

public class StatueBipedModel implements IStatueModel {
	private static final Set<String> PARTS = ImmutableSet.of("Head", "Headwear", "Body", "Right Arm", "Left Arm", "Right Leg", "Left Leg");

	private final ModelBiped model;

	public StatueBipedModel(ModelBiped model) {this.model = model;}

	@Override
	public ModelBase getModel() {
		return model;
	}

	@Override
	public Set<String> getModelPartNames() {
		return PARTS;
	}

	@Override
	public ModelRenderer getModelPart(String name) {
		switch (name) {
			case "Head":
				return model.bipedHead;
			case "Headwear":
				return model.bipedHeadwear;
			case "Body":
				return model.bipedBody;
			case "Right Arm":
				return model.bipedRightArm;
			case "Left Arm":
				return model.bipedLeftArm;
			case "Right Leg":
				return model.bipedRightLeg;
			case "Left Leg":
			default:
				return model.bipedLeftLeg;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float scale) {
		GlStateManager.pushMatrix();

		model.bipedHead.render(scale);
		model.bipedBody.render(scale);
		model.bipedRightArm.render(scale);
		model.bipedLeftArm.render(scale);
		model.bipedRightLeg.render(scale);
		model.bipedLeftLeg.render(scale);
		model.bipedHeadwear.render(scale);
		GlStateManager.popMatrix();
	}
}
