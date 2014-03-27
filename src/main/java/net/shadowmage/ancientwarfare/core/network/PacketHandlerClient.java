package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;

public class PacketHandlerClient
{

@SubscribeEvent
public void onClientPacket(ClientCustomPacketEvent evt)
  {
  PacketBase packet = PacketBase.readPacket(evt.packet.payload());
  packet.setPlayer(Minecraft.getMinecraft().thePlayer);
  packet.execute();
  }

}
