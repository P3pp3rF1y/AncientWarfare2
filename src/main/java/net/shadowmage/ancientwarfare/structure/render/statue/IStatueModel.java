package net.shadowmage.ancientwarfare.structure.render.statue;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.structure.tile.EntityStatueInfo;

import java.util.Map;
import java.util.Set;

@SideOnly(Side.CLIENT)
public interface IStatueModel {
	ModelBase getModel();

	Set<String> getModelPartNames();

	ModelRenderer getModelPart(String name);

	void render(float scale);

	Map<String, EntityStatueInfo.Transform> getBaseTransforms();
}
