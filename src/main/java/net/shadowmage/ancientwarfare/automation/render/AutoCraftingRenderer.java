package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.render.RotatableBlockRenderer;

public class AutoCraftingRenderer extends RotatableBlockRenderer {
	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.modID + ":automation/auto_crafting", "normal");
	public static final AutoCraftingRenderer INSTANCE = new AutoCraftingRenderer();

	private AutoCraftingRenderer() {
		super("automation/auto_crafting.obj");
	}
}
