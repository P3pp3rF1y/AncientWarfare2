package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class NetworkHandler
{

public static final NetworkHandler INSTANCE = new NetworkHandler();

FMLEventChannel channel;

public void registerNetwork()
  {
  channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("AWCORE");
  channel.register(new PacketHandler());
  }

public static void sendToServer(IMessage pkt)
  {
  }

public static void sendToPlayer(EntityPlayerMP player, FMLProxyPacket pkt)
  {
  INSTANCE.channel.sendTo(pkt, player);
  }

public static void sendToAllPlayers(IMessage pkt)
  {
  }

public static void sendToAllNear(World world, int x, int y, int z, double range, IMessage pkt)
  {
  }

}
