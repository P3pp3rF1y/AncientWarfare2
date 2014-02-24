package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.container.ContainerTest;
import net.shadowmage.ancientwarfare.core.gui.GuiTest;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class NetworkHandler implements IGuiHandler
{

public static final String CHANNELNAME = "AWCORE";
public static final NetworkHandler INSTANCE = new NetworkHandler();

public static final int GUI_TEST = 0;

private FMLEventChannel channel;

public final void registerNetwork()
  {
  channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNELNAME);
  channel.register(new PacketHandler());
  PacketBase.registerPacketType(0, TestPacket.class);
  PacketBase.registerPacketType(1, PacketGui.class);
  NetworkRegistry.INSTANCE.registerGuiHandler(AncientWarfareCore.instance, this);
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

@Override
public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
  {
  switch(ID)
  {
  case GUI_TEST:
    {
    return new ContainerTest(player, x, y, z);
    }  
  }
  return null;
  }

@Override
public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
  {
  switch(ID)
  {
  case GUI_TEST:
    {
    return new GuiTest(new ContainerTest(player, x, y, z));
    }  
  }
  return null;
  }

}
