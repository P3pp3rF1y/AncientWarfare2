package net.shadowmage.ancientwarfare.automation.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

public class ItemBlockWorksiteStatic extends ItemBlock
{

public ItemBlockWorksiteStatic(Block p_i45328_1_)
  {
  super(p_i45328_1_);
  }

@Override
public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
  {
  BlockPosition pos1 = new BlockPosition(x, y, z);
  BlockPosition pos2 = new BlockPosition(x, y, z);
  
  int face = BlockTools.getPlayerFacingFromYaw(player.rotationYaw);
  
  pos1.moveForward(face, 1);
  pos1.moveLeft(face, 2);
  pos2.reassign(pos1.x, pos1.y, pos1.z);
  pos2.moveForward(face, 4);
  pos2.moveRight(face, 4);  
  
  BlockPosition min = BlockTools.getMin(pos1, pos2);
  BlockPosition max = BlockTools.getMax(pos1, pos2);
  /**
   * TODO validate that block is not inside work bounds of any other nearby worksites ??
   * TODO validate that worksite does not intersect any others
   */
  metadata = BlockRotationHandler.getMetaForPlacement(player, (IRotatableBlock) field_150939_a, side);
  
  boolean val = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
  if(val)
    {
    TileEntity worksite = world.getTileEntity(x, y, z);
    if(worksite instanceof IWorkSite)
      {
      ((IWorkSite)worksite).setBounds(min, max);
      }
    if(worksite instanceof IOwnable)
      {
      ((IOwnable)worksite).setOwnerName(player.getCommandSenderName());
      }
    world.markBlockForUpdate(x, y, z);
    }
  return val;
  }

@Override
public int getDamage(ItemStack stack)
  {
  return 3;
  }

}
