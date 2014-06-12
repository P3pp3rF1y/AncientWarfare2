package net.shadowmage.ancientwarfare.core.block;

import java.util.HashMap;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;

public class IconRotationMap
{
private HashMap<RelativeSide, String> texNames = new HashMap<RelativeSide, String>();
private HashMap<RelativeSide, IIcon> icons = new HashMap<RelativeSide, IIcon>(); 

public void setIcon(IRotatableBlock block, RelativeSide side, String texName)
  {
  RotationType t = block.getRotationType();
  if(t==RotationType.NONE)
    {
    //TODO throw error message about improper block-rotatation type, perhaps just register the string as ALL_SIDES
    }
  else if(t==RotationType.SIX_WAY)
    {
    if(side!=RelativeSide.TOP && side!=RelativeSide.BOTTOM && side!=RelativeSide.ANY_SIDE)
      {
      //TODO throw error message about improper block-rotation / cannot map specific sides on a six-way
      }
    }
  texNames.put(side, texName);
  }

public void registerIcons(IIconRegister register)
  {
  String name;
  for(RelativeSide key : texNames.keySet())
    {
    name = texNames.get(key);
    icons.put(key, register.registerIcon(name));
    }
  }

public IIcon getIcon(IRotatableBlock block, int meta, int side)
  {
  RelativeSide rSide = RelativeSide.getSideViewed(block.getRotationType(), meta, side);
  return icons.get(rSide);
  }

public IIcon getIcon(RelativeSide side)
  {
  return icons.get(side);
  }
}
