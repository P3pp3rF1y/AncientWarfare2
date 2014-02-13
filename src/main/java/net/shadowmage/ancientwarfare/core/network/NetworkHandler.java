package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class NetworkHandler
{

public static final String CHANNELNAME = "AWCORE";
public static final NetworkHandler INSTANCE = new NetworkHandler();

FMLEventChannel channel;

public void registerNetwork()
  {
  channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNELNAME);
  channel.register(new PacketHandler());
  PacketBase.registerPacketType(0, TestPacket.class);
  }

public static void sendToServer(PacketBase pkt)
  {
  }

public static void sendToPlayer(EntityPlayerMP player, PacketBase pkt)
  {
  INSTANCE.channel.sendTo(pkt.getFMLPacket(), player);
  }

public static void sendToAllPlayers(PacketBase pkt)
  {
  }

public static void sendToAllNear(World world, int x, int y, int z, double range, PacketBase pkt)
  {
  }

}
