package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;

public class TextureImageBased extends SimpleTexture {

	BufferedImage image;

	public TextureImageBased(ResourceLocation par1ResourceLocation, BufferedImage image) {
		super(par1ResourceLocation);
		this.image = image;
	}

	@Override
	public void loadTexture(IResourceManager par1ResourceManager) {
		TextureUtil.uploadTextureImage(getGlTextureId(), image);
	}

	public void reUploadImage() {
		TextureUtil.uploadTextureImage(getGlTextureId(), image);
	}

}
