package net.shadowmage.ancientwarfare.automation.container;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class ContainerWorksiteBlockSelection extends ContainerBase
{

public TileWorksiteBase worksite;

public Set<BlockPosition> targetBlocks = new HashSet<BlockPosition>();

public ContainerWorksiteBlockSelection(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);  
  worksite = (TileWorksiteBase) player.worldObj.getTileEntity(x, y, z);
  targetBlocks.addAll(worksite.getUserSetTargets());
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
    worksite.setUserSetTargets(set);
    worksite.markForUpdate();
    }
  if(tag.hasKey("closeGUI"))
    {
    worksite.onBlockClicked(player);//hack to open the worksites GUI
    }
  }

public void removeTarget(BlockPosition target)
  {
  targetBlocks.remove(target);  
  }

}
