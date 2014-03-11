package net.shadowmage.ancientwarfare.core.network;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;

public final class NetworkHandler implements IGuiHandler
{

public static final String CHANNELNAME = "AWCORE";
public static final NetworkHandler INSTANCE = new NetworkHandler();

public static final int PACKET_TEST = 0;
public static final int PACKET_GUI = 1;
public static final int PACKET_STRUCTURE = 2;

public static final int GUI_TEST = 0;
public static final int GUI_SCANNER = 1;
public static final int GUI_BUILDER = 2;
public static final int GUI_SPAWNER = 3;

private FMLEventChannel channel;

private HashMap<Integer, Class<? extends ContainerBase>> containerClasses = new HashMap<Integer, Class<? extends ContainerBase>>();
private HashMap<Integer, Class<? extends GuiContainerBase>> guiClasses = new HashMap<Integer, Class<? extends GuiContainerBase>>();

public final void registerNetwork()
  {
  channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNELNAME);
  channel.register(new PacketHandlerServer());
  PacketBase.registerPacketType(PACKET_TEST, TestPacket.class);
  PacketBase.registerPacketType(PACKET_GUI, PacketGui.class);
  NetworkRegistry.INSTANCE.registerGuiHandler(AncientWarfareCore.instance, this);
  }

/**
 * should be called from core-client-proxy to register client-side packet handler
 * @param o
 */
public final static void registerClientHandler(Object o)
  {
  INSTANCE.channel.register(o);
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
public final Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
  {
  ContainerBase container = null;
  Class<? extends ContainerBase> clz = containerClasses.get(ID);
  if(clz!=null)
    {
    try
      {
      container = clz.getConstructor(EntityPlayer.class, int.class, int.class, int.class).newInstance(player, x, y, z);
      }
    catch (InstantiationException e)
      {
      e.printStackTrace();
      } 
    catch (IllegalAccessException e)
      {
      e.printStackTrace();
      } 
    catch (IllegalArgumentException e)
      {
      e.printStackTrace();
      } 
    catch (InvocationTargetException e)
      {
      e.printStackTrace();
      } 
    catch (NoSuchMethodException e)
      {
      e.printStackTrace();
      } 
    catch (SecurityException e)
      {
      e.printStackTrace();
      }
    }
  return container;  
  }

@Override
public final Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
  {
  GuiContainerBase gui = null;  
  Class<? extends GuiContainerBase> clz = this.guiClasses.get(ID);
  if(clz!=null)
    {
    ContainerBase container = (ContainerBase) getServerGuiElement(ID, player, world, x, y, z);
    try
      {
      gui = clz.getConstructor(ContainerBase.class).newInstance(container);
      } 
    catch (InstantiationException e)
      {
      e.printStackTrace();
      } 
    catch (IllegalAccessException e)
      {
      e.printStackTrace();
      } 
    catch (IllegalArgumentException e)
      {
      e.printStackTrace();
      } 
    catch (InvocationTargetException e)
      {
      e.printStackTrace();
      } 
    catch (NoSuchMethodException e)
      {
      e.printStackTrace();
      } 
    catch (SecurityException e)
      {
      e.printStackTrace();
      }
    }
  return gui;
  }

public final void registerContainer(int id, Class <? extends ContainerBase> containerClazz)
  {
  this.containerClasses.put(id, containerClazz);
  }

public final void registerGui(int id, Class <? extends GuiContainerBase> guiClazz)
  {
  this.guiClasses.put(id, guiClazz);
  }

public final void openGui(EntityPlayer player, int id, int x, int y, int z)
  {
  if(player.worldObj.isRemote)
    {
    PacketGui pkt = new PacketGui();
    pkt.setOpenGui(id, x, y, z);
    sendToServer(pkt);
    }
  else
    {
    FMLNetworkHandler.openGui(player, AncientWarfareCore.instance, id, player.worldObj, x, y, z);
    }
  }
}
