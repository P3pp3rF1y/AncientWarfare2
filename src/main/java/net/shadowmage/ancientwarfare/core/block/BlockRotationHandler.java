package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.interfaces.IRotatableBlock.RotationType;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

public class BlockRotationHandler
{

public static int getRotatedMeta(IRotatableBlock block, int meta, ForgeDirection axis)
  {
  RotationType t = block.getRotationType();
  if(t==RotationType.FOUR_WAY)
    {
    ForgeDirection d = ForgeDirection.getOrientation(meta+2);
    d = d.getRotation(ForgeDirection.UP);
    return d.ordinal()-2;
    }
  else if(t==RotationType.SIX_WAY)
    {
    
    }
  return meta;
  }

public static int getMetaForPlacement(EntityPlayer player, IRotatableBlock block, int sideHit)
  {
  int f = BlockTools.getPlayerFacingFromYaw(player.rotationYaw); 
  
  if(block.getRotationType()==RotationType.FOUR_WAY)
    {
    ForgeDirection face = BlockTools.getForgeDirectionFromFacing(f);
    return face.ordinal();
    }
  else if(block.getRotationType()==RotationType.SIX_WAY)
    {
    
    }  
  return 0;
  }

}
