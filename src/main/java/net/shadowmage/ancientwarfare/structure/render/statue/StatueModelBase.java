package net.shadowmage.ancientwarfare.structure.render.statue;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.tile.EntityStatueInfo;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public abstract class StatueModelBase<T extends ModelBase> implements IStatueModel {
	protected final T model;
	private Map<String, ModelRenderer> nameRenderer;

	public StatueModelBase(T model) {
		this.model = model;
		initiateNameRendererMap();
	}

	private void initiateNameRendererMap() {
		nameRenderer = getNameRendererMap();
	}

	protected static ModelRenderer getObfuscatedRenderer(ModelBase model, Field rendererField) {
		try {
			return (ModelRenderer) rendererField.get(model);
		}
		catch (IllegalAccessException e) {
			AncientWarfareStructure.LOG.error("Unable to get {} model renderer of {}: {}", rendererField.getName(), model.getClass().getSimpleName(), e);
			return new ModelRenderer(model);
		}
	}

	protected static ModelRenderer[] getObfuscatedRendererArray(ModelBase model, Field rendererField) {
		try {
			return (ModelRenderer[]) rendererField.get(model);
		}
		catch (IllegalAccessException e) {
			AncientWarfareStructure.LOG.error("Unable to get {} model renderer array of {}: {}", rendererField.getName(), model.getClass().getSimpleName(), e);
			return new ModelRenderer[] {new ModelRenderer(model)};
		}
	}

	@Override
	public ModelBase getModel() {
		return model;
	}

	protected abstract Map<String, ModelRenderer> getNameRendererMap();

	@Override
	public Set<String> getModelPartNames() {
		return nameRenderer.keySet();
	}

	@Override
	public ModelRenderer getModelPart(String name) {
		return nameRenderer.getOrDefault(name, new ModelRenderer(model));
	}

	@Override
	public void render(float scale) {
		nameRenderer.forEach((name, renderer) -> renderer.render(scale));
	}

	@Override
	public Map<String, EntityStatueInfo.Transform> getBaseTransforms() {
		return Collections.emptyMap();
	}
}
