package net.shadowmage.ancientwarfare.automation.block;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaft;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaftHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaftLight;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaftMedium;

public class BlockTorqueTransportShaft extends BlockTorqueTransportConduit
{

public BlockTorqueTransportShaft(String regName)
  {
  super(regName);
  }

@Override
public TileEntity createTileEntity(World world, int metadata)
  {  
  switch(metadata)
  {
  case 0:
  return new TileTorqueShaftLight();
  case 1:
  return new TileTorqueShaftMedium();
  case 2:
  return new TileTorqueShaftHeavy();
  }  
  return new TileTorqueShaftLight();
  }

@SuppressWarnings({ "rawtypes", "unchecked" })
@Override
public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List list)
  {
  list.add(new ItemStack(Item.getItemFromBlock(this),1,0));
  list.add(new ItemStack(Item.getItemFromBlock(this),1,1));
  list.add(new ItemStack(Item.getItemFromBlock(this),1,2));
  }

@Override
public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
  {
  float min = 0.1875f, max = 0.8125f;
  float x1=min, y1=min, z1=min, x2=max, y2=max, z2=max;
  TileEntity te = world.getTileEntity(x, y, z);
  if(te instanceof TileTorqueShaft)
    {
    TileTorqueShaft tile = (TileTorqueShaft) world.getTileEntity(x, y, z);
    ForgeDirection d = tile.getPrimaryFacing();
    int s1 = d.ordinal();
    if(s1==0 || s1==1)//up/down
      {
      y1 = 0;
      y2 = 1;
      }
    if(s1==2 || s1==3)//north/south
      {
      z1 = 0;
      z2 = 1;
      }
    if(s1==4 || s1==5)//east/west
      {
      x1 = 0;
      x2 = 1;
      }
    }  
  setBlockBounds(x1, y1, z1, x2, y2, z2);
  }
}
