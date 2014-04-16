package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.research.ResearchData;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

public class PacketResearchInit extends PacketBase
{

NBTTagCompound researchDataTag;

public PacketResearchInit(ResearchData data)
  {
  researchDataTag = new NBTTagCompound();
  data.writeToNBT(researchDataTag);
  }

public PacketResearchInit()
  {
  //reflection constructor
  }

@Override
protected void writeToStream(ByteBuf data)
  {
  ByteBufOutputStream bbos = new ByteBufOutputStream(data);
  try
    {
    CompressedStreamTools.writeCompressed(researchDataTag, bbos);
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  }

@Override
protected void readFromStream(ByteBuf data)
  {
  try
    {
    researchDataTag = CompressedStreamTools.readCompressed(new ByteBufInputStream(data));
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  }

@Override
protected void execute()
  {
  ResearchTracker.instance().onClientResearchReceived(researchDataTag);
  }

}
