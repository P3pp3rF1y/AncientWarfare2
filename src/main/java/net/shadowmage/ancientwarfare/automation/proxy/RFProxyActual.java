package net.shadowmage.ancientwarfare.automation.proxy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;

public class RFProxyActual extends RFProxy
{

protected RFProxyActual()
  {
  
  }

@Override
public boolean isRFTile(TileEntity te)
  {
  return false;
  }

@Override
public void transferPower(World world, int x, int y, int z, ITorqueTile generator, ForgeDirection from)
  {
  
  }

}
