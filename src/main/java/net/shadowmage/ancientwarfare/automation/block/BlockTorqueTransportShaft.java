package net.shadowmage.ancientwarfare.automation.block;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportConduit;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportShaft;

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
  return new TileTorqueTransportShaft();
  case 1:
  return new TileTorqueTransportShaft();
  case 2:
  return new TileTorqueTransportShaft();
  }  
  return new TileTorqueTransportConduit();
  }

@SuppressWarnings({ "rawtypes", "unchecked" })
@Override
public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List list)
  {
  list.add(new ItemStack(Item.getItemFromBlock(this),1,0));
  list.add(new ItemStack(Item.getItemFromBlock(this),1,1));
  list.add(new ItemStack(Item.getItemFromBlock(this),1,2));
  }
}
