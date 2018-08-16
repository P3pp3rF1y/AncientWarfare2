package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

@SideOnly(Side.CLIENT)
public class TextureUtils {
	private TextureUtils() {}

	public static ResourceLocation getTextureLocation(String path) {
		String overridePath = AWCoreStatics.configPathForFiles + path;
		ResourceLocation locationOverride = new ResourceLocation(AncientWarfareCore.MOD_ID, overridePath);
		if (textureLoaded(locationOverride)) {
			return locationOverride;
		}
		ResourceLocation locationMain = new ResourceLocation(AncientWarfareCore.MOD_ID, path);
		if (textureLoaded(locationMain)) {
			return locationMain;
		}

		if (loadTexture(locationOverride, overridePath)) {
			return locationOverride;
		}

		if (loadTextureFromAssets(locationMain, path)) {
			return locationMain;
		}

		return TextureMap.LOCATION_MISSING_TEXTURE;
	}

	private static boolean textureLoaded(ResourceLocation loc) {
		//noinspection ConstantConditions - getTexture isn't marked as nullable but can return null
		return Minecraft.getMinecraft().getTextureManager().getTexture(loc) != null;
	}

	private static boolean loadTexture(ResourceLocation loc, String path) {
		File file = new File(path);
		return file.exists() && loadTexture(loc, file);
	}

	private static boolean loadTexture(ResourceLocation loc, File file) {
		try {
			BufferedImage image = ImageIO.read(file);
			Minecraft.getMinecraft().renderEngine.loadTexture(loc, new TextureImageBased(loc, image));

			return true;
		}
		catch (IOException e) {
			//noop
		}
		return false;
	}

	private static boolean loadTextureFromAssets(ResourceLocation loc, String path) {
		//noinspection ConstantConditions
		String fullPath = "assets/" + AncientWarfareCore.MOD_ID + "/" + path;
		File source = Loader.instance().activeModContainer().getSource();
		if (source.isFile()) {
			try (FileSystem fs = FileSystems.newFileSystem(source.toPath(), null)) {
				InputStream inputstream = fs.provider().newInputStream(fs.getPath(fullPath));
				Minecraft.getMinecraft().renderEngine.loadTexture(loc, new TextureImageBased(loc, ImageIO.read(inputstream)));
				return true;
			}
			catch (IOException e) {
				//noop
			}
		} else if (source.isDirectory()) {
			File file = source.toPath().resolve(fullPath).toFile();
			if (loadTexture(loc, file)) {
				return true;
			}
		}
		return false;
	}
}
