package net.shadowmage.ancientwarfare.automation.tile.worksite;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;


public abstract class TileWorksiteBounded extends TileWorksiteBasic
{

/**
 * minimum position of the work area bounding box, or a single block position if bbMax is not set
 * must not be null if this block has a work-area
 */
BlockPosition bbMin;

/**
 * maximum position of the work bounding box.  May be null
 */
BlockPosition bbMax;

@Override
public EnumSet<WorksiteUpgrade> getValidUpgrades()
{
return EnumSet.of(
    WorksiteUpgrade.ENCHANTED_TOOLS_1,
    WorksiteUpgrade.ENCHANTED_TOOLS_2,
    WorksiteUpgrade.SIZE_MEDIUM,
    WorksiteUpgrade.SIZE_LARGE,
    WorksiteUpgrade.TOOL_QUALITY_1,
    WorksiteUpgrade.TOOL_QUALITY_2,
    WorksiteUpgrade.TOOL_QUALITY_3
    );
}

@Override
public final boolean hasWorkBounds()
  {
  return true;
  }

@Override
public final BlockPosition getWorkBoundsMin()
  {
  return bbMin;
  }

@Override
public final BlockPosition getWorkBoundsMax()
  {
  return bbMax;
  }

@Override
public final void setBounds(BlockPosition min, BlockPosition max)
  {  
  setWorkBoundsMin(BlockTools.getMin(min, max));
  setWorkBoundsMax(BlockTools.getMax(min, max));
  onBoundsSet();
  }

@Override
public int getBoundsMaxWidth()
  {
  return getUpgrades().contains(WorksiteUpgrade.SIZE_MEDIUM)? 9 : getUpgrades().contains(WorksiteUpgrade.SIZE_LARGE)? 16 : 5;
  }

@Override
public int getBoundsMaxHeight(){return 1;}

/**
 * Used by user-set-blocks tile to set all default harvest-checks to true when bounds are FIRST set 
 */
protected void onBoundsSet()
  {
  
  }

public void onBoundsAdjusted()
  {
  //TODO implement to check target blocks, clear invalid ones
  }

public boolean isInBounds(BlockPosition pos)
  {
  return pos.x>=bbMin.x && pos.x<=bbMax.x && pos.z>=bbMin.z && pos.z<=bbMax.z;
  }

protected void validateCollection(Collection<BlockPosition> blocks)
  {
  Iterator<BlockPosition> it = blocks.iterator();
  BlockPosition pos;
  while(it.hasNext() && (pos=it.next())!=null)
    {
    if(!isInBounds(pos)){it.remove();}
    }
  }

public final void setWorkBoundsMin(BlockPosition min)
  {
  bbMin = min;
  }

public final void setWorkBoundsMax(BlockPosition max)
  {
  bbMax = max;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  if(tag.hasKey("bbMin"))
    {
    bbMin = new BlockPosition();
    bbMin.read(tag.getCompoundTag("bbMin"));
    }
  if(tag.hasKey("bbMax"))
    {
    bbMax = new BlockPosition();
    bbMax.read(tag.getCompoundTag("bbMax"));
    }
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  if(bbMin!=null)
    {
    NBTTagCompound innerTag = new NBTTagCompound();
    bbMin.writeToNBT(innerTag);
    tag.setTag("bbMin", innerTag);
    }
  if(bbMax!=null)
    {
    NBTTagCompound innerTag = new NBTTagCompound();
    bbMax.writeToNBT(innerTag);
    tag.setTag("bbMax", innerTag);
    }
  }

@Override
public NBTTagCompound getDescriptionPacketTag(NBTTagCompound tag)
  {
  super.getDescriptionPacketTag(tag);
  if(bbMin!=null)
    {
    NBTTagCompound innerTag = new NBTTagCompound();
    bbMin.writeToNBT(innerTag);
    tag.setTag("bbMin", innerTag);
    }
  if(bbMax!=null)
    {
    NBTTagCompound innerTag = new NBTTagCompound();
    bbMax.writeToNBT(innerTag);
    tag.setTag("bbMax", innerTag);
    }  
  return tag;
  }

@Override
public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {
  super.onDataPacket(net, pkt);
  NBTTagCompound tag = pkt.func_148857_g();
  if(tag.hasKey("bbMin"))
    {
    bbMin = new BlockPosition();
    bbMin.read(tag.getCompoundTag("bbMin"));
    }
  if(tag.hasKey("bbMax"))
    {
    bbMax = new BlockPosition();
    bbMax.read(tag.getCompoundTag("bbMax"));
    }
  }

}
