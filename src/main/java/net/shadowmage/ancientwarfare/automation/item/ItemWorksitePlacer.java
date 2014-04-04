package net.shadowmage.ancientwarfare.automation.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.tile.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.block.BlockIconRotationMap;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

public class ItemWorksitePlacer extends ItemBlock implements IItemKeyInterface
{

public ItemWorksitePlacer(Block p_i45328_1_)
  {
  super(p_i45328_1_);
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack)
  {
  BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, player.isSneaking());
  if(hit==null)
    {
    return;
    }
  if(!stack.hasTagCompound())
    {
    stack.setTagCompound(new NBTTagCompound());
    }
  if(stack.getTagCompound().hasKey("pos2") && stack.getTagCompound().hasKey("pos1"))
    {
    AWLog.logDebug("has pos1 and pos2..should send chat message");
    //TODO send chat message
    }
  else if(stack.getTagCompound().hasKey("pos1"))
    {
    NBTTagCompound tag = new NBTTagCompound();
    hit.writeToNBT(tag);
    stack.getTagCompound().setTag("pos2", tag);
    }
  else
    {
    NBTTagCompound tag = new NBTTagCompound();
    hit.writeToNBT(tag);
    stack.getTagCompound().setTag("pos1", tag);
    }
  }

@Override
public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
  {
  if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("pos1") || !stack.getTagCompound().hasKey("pos2"))
    {
    return false;
    }
  
  BlockPosition pos1 = new BlockPosition(stack.getTagCompound().getCompoundTag("pos1"));
  BlockPosition pos2 = new BlockPosition(stack.getTagCompound().getCompoundTag("pos2"));
  BlockPosition min = BlockTools.getMin(pos1, pos2);
  BlockPosition max = BlockTools.getMax(pos1, pos2);
  if(x<min.x-1 || z<min.x-1 || y<min.y-1 || x>max.x+1 || z>max.z+1 || y>max.z+1)
    {
    AWLog.logDebug("too far away from bounds to place block..");
    /**
     * TODO output chat message saying block is too far away from work bounds
     */
    return false;
    }
  else if((x>=min.x && x<=max.x)&&(y>=min.y && y<=max.y)&&(z>=min.z && z<=max.z))
    {
    AWLog.logDebug("cannot place block within work bounds");
    /**
     * TODO output chat message saying block cannot be inside work bounds
     */
    return false;
    }
  /**
   * TODO validate that block is not inside work bounds of any other nearby worksites
   */
  metadata = BlockIconRotationMap.getBlockMetaForPlacement(player);
  
  boolean val = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
  if(val)
    {
    TileWorksiteBase worksite = (TileWorksiteBase) world.getTileEntity(x, y, z);
    BlockPosition p1 = new BlockPosition(stack.getTagCompound().getCompoundTag("pos1"));
    BlockPosition p2 = new BlockPosition(stack.getTagCompound().getCompoundTag("pos2"));
    worksite.setOwningPlayer(player.getCommandSenderName());
    worksite.setWorkBounds(BlockTools.getMin(p1, p2), BlockTools.getMax(p1, p2));
    worksite.initWorkSite();
    worksite.markDirty();
    world.markBlockForUpdate(x, y, z);
    stack.getTagCompound().removeTag("pos1");
    stack.getTagCompound().removeTag("pos2");
    }
  return val;
  }

}
