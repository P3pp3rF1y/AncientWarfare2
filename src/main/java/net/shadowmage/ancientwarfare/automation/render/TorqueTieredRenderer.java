package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.vec.uv.IconTransformation;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.block.TorqueTier;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;

import java.util.Map;

public abstract class TorqueTieredRenderer<T extends TileTorqueBase> extends BaseTorqueRenderer<T> {
	public Map<TorqueTier, TextureAtlasSprite> sprites = Maps.newHashMap();
	public Map<TorqueTier, IconTransformation> iconTransforms = Maps.newHashMap();

	protected TorqueTieredRenderer(String modelPath) {
		super(modelPath);
	}

	public void setSprite(TorqueTier torqueTier, TextureAtlasSprite sprite) {
		sprites.put(torqueTier, sprite);
		iconTransforms.put(torqueTier, new IconTransformation(sprite));
	}

	@Override
	protected IconTransformation getIconTransform(IExtendedBlockState state) {
		return iconTransforms.get(state.getValue(AutomationProperties.TIER));
	}

	@Override
	protected IconTransformation getIconTransform(ItemStack stack) {
		return iconTransforms.get(TorqueTier.values()[stack.getMetadata()]);
	}

	public TextureAtlasSprite getSprite(TorqueTier torqueTier) {
		return sprites.get(torqueTier);
	}
}
