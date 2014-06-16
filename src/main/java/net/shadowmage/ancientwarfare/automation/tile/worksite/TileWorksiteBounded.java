package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
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
  }

private final void setWorkBoundsMin(BlockPosition min)
  {
  bbMin = min;
  }

private final void setWorkBoundsMax(BlockPosition max)
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
public final Packet getDescriptionPacket()
  {
  NBTTagCompound tag = new NBTTagCompound();
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
  return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, tag);
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
