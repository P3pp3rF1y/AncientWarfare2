package net.shadowmage.ancientwarfare.structure.render.statue;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.shadowmage.ancientwarfare.structure.tile.EntityStatueInfo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class GuardianStatueModel extends StatueModelBase<ModelGuardian> {
	private Map<String, EntityStatueInfo.Transform> baseTransforms = new HashMap<>();

	public GuardianStatueModel() {
		super(new ModelGuardian());
		initRenderers();
	}

	private void initRenderers() {
		float[] afloat = new float[] {1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
		float[] afloat1 = new float[] {0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
		float[] afloat2 = new float[] {0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
		float[] afloat3 = new float[] {0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
		float[] afloat4 = new float[] {-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
		float[] afloat5 = new float[] {8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};

		for (int i = 0; i < 12; ++i) {
			EntityStatueInfo.Transform transform = new EntityStatueInfo.Transform();
			transform.setRotationX((float) Math.PI * afloat[i]);
			transform.setRotationY((float) Math.PI * afloat1[i]);
			transform.setRotationZ((float) Math.PI * afloat2[i]);
			baseTransforms.put("Spine " + (i + 1), transform);
			getSpines()[i].rotationPointX = afloat3[i] * (1.0F + MathHelper.cos((float) i) * 0.01F - 0.55F);
			getSpines()[i].rotationPointY = 16.0F + afloat4[i] * (1.0F + MathHelper.cos((float) i) * 0.01F - 0.55F);
			getSpines()[i].rotationPointZ = afloat5[i] * (1.0F + MathHelper.cos((float) i) * 0.01F - 0.55F);
		}

		getEye().rotationPointZ = -8.25F;

		getTail()[1].rotationPointX = -1.5F;
		getTail()[1].rotationPointY = 0.5F;
		getTail()[1].rotationPointZ = 14.0F;
		getTail()[2].rotationPointX = 0.5F;
		getTail()[2].rotationPointY = 0.5F;
		getTail()[2].rotationPointZ = 6.0F;
	}

	@Override
	protected Map<String, ModelRenderer> getNameRendererMap() {
		return new ImmutableMap.Builder<String, ModelRenderer>()
				.put("Body", getBody())
				.put("Eye", getEye())
				.put("Spine 1", getSpines()[0])
				.put("Spine 2", getSpines()[1])
				.put("Spine 3", getSpines()[2])
				.put("Spine 4", getSpines()[3])
				.put("Spine 5", getSpines()[4])
				.put("Spine 6", getSpines()[5])
				.put("Spine 7", getSpines()[6])
				.put("Spine 8", getSpines()[7])
				.put("Spine 9", getSpines()[8])
				.put("Spine 10", getSpines()[9])
				.put("Spine 11", getSpines()[10])
				.put("Spine 12", getSpines()[11])
				.put("Tail 1", getTail()[0])
				.put("Tail 2", getTail()[1])
				.put("Tail 3", getTail()[2])
				.build();
	}

	@Override
	public Map<String, EntityStatueInfo.Transform> getBaseTransforms() {
		return baseTransforms;
	}

	private ModelRenderer getBody() {
		return getObfuscatedRenderer(model, BODY_FIELD);
	}

	@Override
	public void render(float scale) {
		getBody().render(scale);
	}

	private ModelRenderer[] getTail() {
		return getObfuscatedRendererArray(model, TAIL_FIELD);
	}

	private ModelRenderer getEye() {
		return getObfuscatedRenderer(model, EYE_FIELD);
	}

	private ModelRenderer[] getSpines() {
		return getObfuscatedRendererArray(model, SPINES_FIELD);
	}

	private static final Field BODY_FIELD = ObfuscationReflectionHelper.findField(ModelGuardian.class, "field_178710_a");
	private static final Field EYE_FIELD = ObfuscationReflectionHelper.findField(ModelGuardian.class, "field_178708_b");
	private static final Field SPINES_FIELD = ObfuscationReflectionHelper.findField(ModelGuardian.class, "field_178709_c");
	private static final Field TAIL_FIELD = ObfuscationReflectionHelper.findField(ModelGuardian.class, "field_178707_d");

}
