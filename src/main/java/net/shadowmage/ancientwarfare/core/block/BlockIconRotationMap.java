package net.shadowmage.ancientwarfare.core.block;

import java.util.HashMap;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

/**
 * icon storage class for meta-rotatable blocks<br>
 * does not support animation or swapping of textures during run-time (e.g. furnace lit/unlit)<br> * 
 *<br>
 * relative side metadata directional map<br>
 * meta=direction that front of block faces towards (e.g. north==visible when facing south)<br>
 * 0=south<br>
 * 1=west<br>
 * 2=north<br>
 * 3=east<br>
 * 4=up:top=south<br>
 * 5=up:top=west<br>
 * 6=up:top=north<br>
 * 7=up:top=east<br>
 * 8=down:top=south<br>
 * 9=down:top=west<br>
 * 10=down:top=north<br>
 * 11=down:top=east<br>
 *
 * @author Shadowmage
 */
public class BlockIconRotationMap
{
HashMap<RelativeSide, IIcon> iconMap = new HashMap<RelativeSide, IIcon>();
HashMap<RelativeSide, String> iconTexMap = new HashMap<RelativeSide, String>();

public void setIconTexture(RelativeSide relativeSide, String texName)
  {
  iconTexMap.put(relativeSide, texName);
  }

public void registerIcons(IIconRegister reg)
  {
  String tex;
  IIcon icon;
  for(RelativeSide key : iconTexMap.keySet())
    {
    tex = iconTexMap.get(key);
    icon = reg.registerIcon(tex);
    iconMap.put(key, icon);
    }
  }

public IIcon getIconFor(int mcSide, int meta)
  {
  return iconMap.get(RelativeSide.getRelativeSide(mcSide, meta));
  }

public static int getBlockMetaForPlacement(EntityPlayer player)
  {
  boolean invert = player.isSneaking();
  int face = BlockTools.getPlayerFacingFromYaw(player.rotationYaw);
  if(player.isSneaking())
    {
    face = (face+2)%4;
    }
  if(player.rotationPitch<-45)
    {
    return face + (invert? 4 : 8);
    }  
  else if(player.rotationPitch>45)
    {
    return face + (invert? 8 : 4);
    }
  else
    {
    return (face+2)%4;
    }
  }


}
