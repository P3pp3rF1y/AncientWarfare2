package net.shadowmage.ancientwarfare.core.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class TextureFileBased extends SimpleTexture
{

private File file;

public TextureFileBased(ResourceLocation par1ResourceLocation, File file)
  {
  super(par1ResourceLocation);
  this.file = file;
  }

@Override
public void loadTexture(IResourceManager par1ResourceManager)
  {
  BufferedImage image;
  try
    {
    image = ImageIO.read(file);
    TextureUtil.uploadTextureImage(getGlTextureId(), image);
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  }

}
