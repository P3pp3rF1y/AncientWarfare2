package net.shadowmage.ancientwarfare.core.block;

import java.util.HashMap;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockIconMap
{

HashMap<Integer, IIcon> iconMap = new HashMap<Integer, IIcon>();
HashMap<Integer, String> iconTexMap = new HashMap<Integer, String>();

public void setIconTexture(int side, int meta, String texName)
  {
  iconTexMap.put(side + meta*16, texName);
  }

public void registerIcons(IIconRegister reg)
  {
  String tex;
  IIcon icon;
  for(Integer key : iconTexMap.keySet())
    {
    tex = iconTexMap.get(key);
    icon = reg.registerIcon(tex);
    iconMap.put(key, icon);
    }
  }

public IIcon getIconFor(int mcSide, int meta)
  {
  return iconMap.get(mcSide + meta*16);
  }

}
