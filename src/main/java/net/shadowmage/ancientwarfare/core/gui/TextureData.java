package net.shadowmage.ancientwarfare.core.gui;

import net.minecraft.util.ResourceLocation;

public class TextureData {
	private final ResourceLocation texture;
	private final int textureU;
	private final int textureV;
	private final int textureWidth;
	private final int textureHeight;
	private final int partWidth;
	private final int partHeight;

	public TextureData(ResourceLocation texture, int textureWidth, int textureHeight, int textureU, int textureV, int partWidth, int partHeight) {
		this.texture = texture;
		this.textureU = textureU;
		this.textureV = textureV;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.partWidth = partWidth;
		this.partHeight = partHeight;
	}

	public int getTextureWidth() {
		return textureWidth;
	}

	public ResourceLocation getTexture() {
		return texture;
	}

	public int getTextureU() {
		return textureU;
	}

	public int getTextureV() {
		return textureV;
	}

	public int getTextureHeight() {
		return textureHeight;
	}

	public int getPartWidth() {
		return partWidth;
	}

	public int getPartHeight() {
		return partHeight;
	}
}
