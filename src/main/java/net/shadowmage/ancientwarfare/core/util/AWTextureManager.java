package net.shadowmage.ancientwarfare.core.util;

import java.io.File;
import java.util.HashMap;

import net.shadowmage.ancientwarfare.modeler.gui.TextureManager;

public class AWTextureManager
{

public static AWTextureManager instance(){return instance;}
private static AWTextureManager instance = new AWTextureManager();
private AWTextureManager(){}

private TextureManager boundTexture;

private HashMap<String, TextureManager> loadedTextures = new HashMap<String, TextureManager>();

public TextureManager getTexture(String name)
  {
  return loadedTextures.get(name);
  }

public TextureManager loadTexture(String name, File file)
  {
  return null;
  }

public void addLoadedTexture(String name, TextureManager tex)
  {
  this.loadedTextures.put(name, tex);
  }

public void bindTexture(String name)
  {
  resetBoundTexture();
  if(loadedTextures.containsKey(name))
    {
    boundTexture = loadedTextures.get(name);
    boundTexture.bindTexture();
    } 
  }

public void resetBoundTexture()
  {
  if(boundTexture!=null)
    {
    boundTexture.resetBoundTexture();
    boundTexture = null;
    }
  }

}
