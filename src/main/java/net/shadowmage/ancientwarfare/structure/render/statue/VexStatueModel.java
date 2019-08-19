package net.shadowmage.ancientwarfare.structure.render.statue;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelVex;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Map;

public class VexStatueModel extends BipedStatueModel<ModelVex> {
	public VexStatueModel() {
		super(new ModelVex());
	}

	@Override
	protected Map<String, ModelRenderer> getNameRendererMap() {
		return new ImmutableMap.Builder<String, ModelRenderer>()
				.putAll(super.getNameRendererMap())
				.put("Left Wing", getObfuscatedRenderer(model, LEFT_WING_FIELD))
				.put("Right Wing", getObfuscatedRenderer(model, RIGHT_WING_FIELD))
				.build();
	}

	private static final Field LEFT_WING_FIELD = ObfuscationReflectionHelper.findField(ModelVex.class, "field_191229_a");
	private static final Field RIGHT_WING_FIELD = ObfuscationReflectionHelper.findField(ModelVex.class, "field_191230_b");
}
