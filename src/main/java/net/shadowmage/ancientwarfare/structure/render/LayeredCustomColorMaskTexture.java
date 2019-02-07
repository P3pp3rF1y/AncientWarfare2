package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class LayeredCustomColorMaskTexture extends AbstractTexture {
	/**
	 * Access to the Logger, for all your logging needs.
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	/**
	 * The location of the texture.
	 */
	private final ResourceLocation textureLocation;
	private final List<String> textures;
	private final List<Integer> colors;

	public LayeredCustomColorMaskTexture(ResourceLocation textureLocation, List<String> textures, List<Integer> colors) {
		this.textureLocation = textureLocation;
		this.textures = textures;
		this.colors = colors;
	}

	public void loadTexture(IResourceManager resourceManager) throws IOException {
		deleteGlTexture();
		IResource resource = null;
		BufferedImage bufferedimage;
		try {
			resource = resourceManager.getResource(textureLocation);
			BufferedImage bufferedimage1 = TextureUtil.readBufferedImage(resource.getInputStream());
			int i = bufferedimage1.getType();

			if (i == 0) {
				i = 6;
			}

			bufferedimage = new BufferedImage(bufferedimage1.getWidth(), bufferedimage1.getHeight(), i);
			Graphics graphics = bufferedimage.getGraphics();
			graphics.drawImage(bufferedimage1, 0, 0, null);
			int j = 0;

			while (j < 17 && j < textures.size() && j < colors.size()) {
				String texture = textures.get(j);
				int color = colors.get(j);

				overlayTexture(resourceManager, bufferedimage, bufferedimage1, texture, color);

				++j;
			}
			TextureUtil.uploadTextureImage(getGlTextureId(), bufferedimage);
		}
		catch (IOException ioexception) {
			LOGGER.error("Couldn't load layered image", (Throwable) ioexception);
		}
		finally {
			IOUtils.closeQuietly(resource);
		}
	}

	private void overlayTexture(IResourceManager resourceManager, BufferedImage bufferedimage, BufferedImage bufferedimage1, String textureName, int color)
			throws IOException {
		IResource iresource1 = null;

		try {

			if (textureName != null) {
				iresource1 = resourceManager.getResource(new ResourceLocation(textureName));
				BufferedImage bufferedimage2 = TextureUtil.readBufferedImage(iresource1.getInputStream());

				if (bufferedimage2.getWidth() == bufferedimage.getWidth() && bufferedimage2.getHeight() == bufferedimage.getHeight() && bufferedimage2.getType() == 6) {
					for (int l = 0; l < bufferedimage2.getHeight(); ++l) {
						for (int i1 = 0; i1 < bufferedimage2.getWidth(); ++i1) {
							int j1 = bufferedimage2.getRGB(i1, l);

							if ((j1 & -16777216) != 0) {
								int k1 = (j1 & 16711680) << 8 & -16777216;
								int l1 = bufferedimage1.getRGB(i1, l);
								int i2 = MathHelper.multiplyColor(l1, color) & 16777215;
								bufferedimage2.setRGB(i1, l, k1 | i2);
							}
						}
					}

					bufferedimage.getGraphics().drawImage(bufferedimage2, 0, 0, null);
				}
			}
		}
		finally {
			IOUtils.closeQuietly((Closeable) iresource1);
		}
	}
}