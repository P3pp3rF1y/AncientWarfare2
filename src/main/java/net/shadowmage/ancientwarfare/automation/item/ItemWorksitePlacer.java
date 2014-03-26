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
  BlockPosition hit = BlockTools.getBlockClickedOn(player, player.worldObj, true);
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
    //send chat message
    }
  else if(stack.getTagCompound().hasKey("pos1"))
    {
    AWLog.logDebug("has pos1, setting pos2");
    NBTTagCompound tag = new NBTTagCompound();
    hit.writeToNBT(tag);
    stack.getTagCompound().setTag("pos2", tag);
    }
  else
    {
    AWLog.logDebug("setting pos1");
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
  metadata = BlockIconRotationMap.getBlockMetaForPlacement(player);
  AWLog.logDebug("placing block with metadata of: "+metadata);
  
  boolean val = super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
  if(val)
    {
    TileWorksiteBase worksite = (TileWorksiteBase) world.getTileEntity(x, y, z);
    BlockPosition p1 = new BlockPosition(stack.getTagCompound().getCompoundTag("pos1"));
    BlockPosition p2 = new BlockPosition(stack.getTagCompound().getCompoundTag("pos2"));
    worksite.setOwningPlayer(player.getCommandSenderName());
    worksite.setWorkBounds(BlockTools.getMin(p1, p2), BlockTools.getMax(p1, p2));
    stack.getTagCompound().removeTag("pos1");
    stack.getTagCompound().removeTag("pos2");
    }
  return val;
  }

}
