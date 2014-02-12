package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class NetworkHandler
{

public static final NetworkHandler INSTANCE = new NetworkHandler();

private SimpleNetworkWrapper wrap;


public void registerNetwork()
  {
  wrap = NetworkRegistry.INSTANCE.newSimpleChannel("AWCORE");
  }

public static void sendToServer(IMessage pkt)
  {
  INSTANCE.wrap.sendToServer(pkt);
  }

public static void sendToPlayer(EntityPlayerMP player, IMessage pkt)
  {
  INSTANCE.wrap.sendTo(pkt, player);
  }

public static void sendToAllPlayers(IMessage pkt)
  {
  INSTANCE.wrap.sendToAll(pkt);
  }

public static void sendToAllNear(World world, int x, int y, int z, double range, IMessage pkt)
  {
  INSTANCE.wrap.sendToAllAround(pkt, new TargetPoint(world.provider.dimensionId, x, y, z, range));
  }

}
