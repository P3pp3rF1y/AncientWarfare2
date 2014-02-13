package net.shadowmage.ancientwarfare.core.network;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import io.netty.buffer.ByteBuf;

public class TestPacket extends PacketBase
{

public TestPacket()
  {
  
  }

@Override
protected void writeToStream(ByteBuf data)
  {
  // TODO Auto-generated method stub
  
  }

@Override
protected void readFromStream(ByteBuf data)
  {
  // TODO Auto-generated method stub
  
  }

@Override
protected void execute()
  {
  AWLog.logDebug("executing test packet");
  }



}
