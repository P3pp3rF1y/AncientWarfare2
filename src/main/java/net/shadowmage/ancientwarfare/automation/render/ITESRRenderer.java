package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.render.CCRenderState;
import net.minecraftforge.common.property.IExtendedBlockState;

public interface ITESRRenderer {
	void renderTransformedBlockModels(CCRenderState ccrs, IExtendedBlockState state);
}
