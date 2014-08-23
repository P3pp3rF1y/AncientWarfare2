package net.shadowmage.ancientwarfare.automation.container;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteUserBlocks;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class ContainerWorksiteBlockSelection extends ContainerBase
{

public TileWorksiteUserBlocks worksite;

public Set<BlockPosition> targetBlocks = new HashSet<BlockPosition>();

public ContainerWorksiteBlockSelection(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);  
  worksite = (TileWorksiteUserBlocks) player.worldObj.getTileEntity(x, y, z);
  targetBlocks.addAll(worksite.getUserSetTargets());
  }

@Override
public void sendInitData()
  {  
  NBTTagList list = new NBTTagList();
  NBTTagCompound blockTag;
  for(BlockPosition pos : targetBlocks)
    {
    blockTag = new NBTTagCompound();
    pos.writeToNBT(blockTag);
    list.appendTag(blockTag);
    }  
  NBTTagCompound outer = new NBTTagCompound();
  outer.setTag("userBlocks", list);
  AWLog.logDebug("sending init data set of: "+outer);
  sendDataToClient(outer);
  }

public void sendTargetsToServer()
  {
  NBTTagList list = new NBTTagList();
  NBTTagCompound blockTag;
  for(BlockPosition pos : targetBlocks)
    {
    blockTag = new NBTTagCompound();
    pos.writeToNBT(blockTag);
    list.appendTag(blockTag);
    }  
  NBTTagCompound outer = new NBTTagCompound();
  outer.setTag("userBlocks", list);
  sendDataToServer(outer);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  refreshGui();
  if(tag.hasKey("userBlocks"))
    {
    Set<BlockPosition> set = new HashSet<BlockPosition>();
    NBTTagList list = tag.getTagList("userBlocks", Constants.NBT.TAG_COMPOUND);
    BlockPosition pos;
    for(int i = 0; i < list.tagCount(); i++)
      {
      pos = new BlockPosition(list.getCompoundTagAt(i));
      set.add(pos);
      }
    targetBlocks.clear();
    targetBlocks.addAll(set);
    if(!player.worldObj.isRemote)
      {
      worksite.setUserSetTargets(set);      
      }
    }
  if(tag.hasKey("closeGUI"))
    {
    worksite.onBlockClicked(player);//hack to open the worksites GUI
    } 
  if(tag.hasKey("moveBounds"))
    {
    BlockPosition pos = new BlockPosition(tag.getCompoundTag("moveBounds"));
    }
  if(tag.hasKey("adjustBounds"))
    {
    BlockPosition pos = new BlockPosition(tag.getCompoundTag("adjustBounds"));
    }
  }

public void removeTarget(BlockPosition target)
  {
  targetBlocks.remove(target);  
  }

}
