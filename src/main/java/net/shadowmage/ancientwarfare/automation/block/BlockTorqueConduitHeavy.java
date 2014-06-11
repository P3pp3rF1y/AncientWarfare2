package net.shadowmage.ancientwarfare.automation.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportConduitHeavy;

public class BlockTorqueConduitHeavy extends BlockTorqueConduit
{

public BlockTorqueConduitHeavy(String regName)
  {
  super(regName);  
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {
  return new TileTorqueTransportConduitHeavy();
  }

}
