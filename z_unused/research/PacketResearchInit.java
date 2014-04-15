package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;

import java.util.HashSet;
import java.util.Set;

import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

public class PacketResearchInit extends PacketBase
{

private Set<Integer> research = new HashSet<Integer>();

public PacketResearchInit(Set<Integer> research)
  {
  research.addAll(research);
  }

public PacketResearchInit()
  {
  //reflection constructor
  }

@Override
protected void writeToStream(ByteBuf data)
  {
  data.writeShort((short)research.size());
  for(Integer i : research)
    {
    data.writeInt(i);
    }
  }

@Override
protected void readFromStream(ByteBuf data)
  {
  short len = data.readShort();
  for(int i = 0; i < len; i++)
    {
    research.add(data.readInt());
    }
  }

@Override
protected void execute()
  {
  ResearchTracker.instance().onClientResearchReceived(research);
  }

}
