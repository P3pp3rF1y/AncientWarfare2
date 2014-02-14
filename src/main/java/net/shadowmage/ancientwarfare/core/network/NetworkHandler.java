package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class NetworkHandler
{

public static final String CHANNELNAME = "AWCORE";
public static final NetworkHandler INSTANCE = new NetworkHandler();

private FMLEventChannel channel;

public final void registerNetwork()
  {
  channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNELNAME);
  channel.register(new PacketHandler());
  PacketBase.registerPacketType(0, TestPacket.class);
  }

public final static void sendToServer(PacketBase pkt)
  {
  INSTANCE.channel.sendToServer(pkt.getFMLPacket());
  }

public final static void sendToPlayer(EntityPlayerMP player, PacketBase pkt)
  {
  INSTANCE.channel.sendTo(pkt.getFMLPacket(), player);
  }

public final static void sendToAllPlayers(PacketBase pkt)
  {
  INSTANCE.channel.sendToAll(pkt.getFMLPacket());
  }

public final static void sendToAllNear(World world, int x, int y, int z, double range, PacketBase pkt)
  {
  INSTANCE.channel.sendToAllAround(pkt.getFMLPacket(), new TargetPoint(world.provider.dimensionId, x, y, z, range));
  }

}
