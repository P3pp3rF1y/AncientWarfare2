package net.shadowmage.ancientwarfare.automation.tile.worksite;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;


public abstract class TileWorksiteUserBlocks extends TileWorksiteBlockBased
{

private Set<BlockPosition> userTargetBlocks = new HashSet<BlockPosition>();

public TileWorksiteUserBlocks()
  {
  
  }

public boolean hasUserSetTargets()
  {
  return true;
  }

public Set<BlockPosition> getUserSetTargets()
  {
  return userTargetBlocks;
  }

public void setUserSetTargets(Set<BlockPosition> targets)
  {
  userTargetBlocks.clear();
  userTargetBlocks.addAll(targets);
  }

public void addUserBlock(BlockPosition pos)
  {
  userTargetBlocks.add(pos);
  }

public void removeUserBlock(BlockPosition pos)
  {
  this.userTargetBlocks.remove(pos);
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  if(!userTargetBlocks.isEmpty())
    {
    NBTTagList list = new NBTTagList();
    NBTTagCompound posTag;
    for(BlockPosition pos : userTargetBlocks)
      {
      posTag = new NBTTagCompound();
      pos.writeToNBT(posTag);
      list.appendTag(posTag);
      }    
    tag.setTag("userBlocks", list);
    }
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  if(tag.hasKey("userBlocks"))
    {
    NBTTagList list = tag.getTagList("userBlocks", Constants.NBT.TAG_COMPOUND);
    BlockPosition pos;
    for(int i = 0; i < list.tagCount(); i++)
      {
      pos = new BlockPosition(list.getCompoundTagAt(i));
      userTargetBlocks.add(pos);
      }
    }
  }


}
