package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

public class TestPacket extends FMLProxyPacket
{

public TestPacket()
  {
  super(Unpooled.buffer(), "AWCORE");
  }



}
