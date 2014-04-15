package net.shadowmage.ancientwarfare.core.network;

import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import io.netty.buffer.ByteBuf;

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
