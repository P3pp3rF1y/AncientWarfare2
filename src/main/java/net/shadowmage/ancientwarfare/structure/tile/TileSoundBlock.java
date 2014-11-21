package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.util.SongPlayData;

public class TileSoundBlock extends TileEntity
{

private SongPlayData tuneData;

public TileSoundBlock()
  {
  tuneData = new SongPlayData();
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  tuneData.readFromNBT(tag.getCompoundTag("tuneData"));
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setTag("tuneData", tuneData.writeToNBT(new NBTTagCompound()));
  }

public SongPlayData getTuneData()
  {
  return tuneData;
  }

}
