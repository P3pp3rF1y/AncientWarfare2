package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

public class PacketResearchUpdate extends PacketBase
{

int toAdd;

public PacketResearchUpdate(int toAdd)
  {
  this.toAdd = toAdd;
  }

public PacketResearchUpdate()
  {
  
  }

@Override
protected void writeToStream(ByteBuf data)
  {
  data.writeInt(toAdd);
  }

@Override
protected void readFromStream(ByteBuf data)
  {
  toAdd = data.readInt();
  }

@Override
protected void execute()
  {
  ResearchTracker.instance().addClientResearch(toAdd);
  }

}
