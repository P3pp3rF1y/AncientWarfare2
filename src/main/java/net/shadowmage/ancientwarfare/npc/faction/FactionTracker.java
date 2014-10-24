package net.shadowmage.ancientwarfare.npc.faction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.gamedata.FactionData;
import net.shadowmage.ancientwarfare.npc.network.PacketFactionUpdate;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class FactionTracker
{

private FactionTracker(){}
public static final FactionTracker INSTANCE = new FactionTracker();

FactionEntry clientEntry = new FactionEntry("client_entry");

@SubscribeEvent
public void onPlayerLogin(PlayerLoggedInEvent evt)
  {
  onPlayerLogin(evt.player);
  }

@SubscribeEvent
public void onClientConnect(ClientConnectedToServerEvent evt)
  {
  clientEntry = new FactionEntry("client_entry");
  }

@SubscribeEvent
public void onClientDisconnect(ClientDisconnectionFromServerEvent evt)
  {
  clientEntry = new FactionEntry("client_entry");
  }

private void onPlayerLogin(EntityPlayer player)
  {
  FactionData data = AWGameData.INSTANCE.getData(FactionData.name, player.worldObj, FactionData.class);
  data.onPlayerLogin(player);
  sendFactionEntry(player, data);
  }

public void adjustStandingFor(World world, String playerName, String factionName, int adjustment)
  {
  if(world.isRemote){throw new IllegalArgumentException("Cannot adjust standing on client world!");}
  FactionData data = AWGameData.INSTANCE.getData(FactionData.name, world, FactionData.class);
  data.adjustStandingFor(playerName, factionName, adjustment);  
  sendFactionUpdate(world, playerName, factionName, data);
  }

public void setStandingFor(World world, String playerName, String factionName, int setting)
  {
  if(world.isRemote){throw new IllegalArgumentException("Cannot set standing on client world!");}
  FactionData data = AWGameData.INSTANCE.getData(FactionData.name, world, FactionData.class);
  data.setStandingFor(playerName, factionName, setting);
  sendFactionUpdate(world, playerName, factionName, data);
  }

public int getStandingFor(World world, String playerName, String factionName)
  {
  if(world.isRemote)
    {
    if(clientEntry!=null)
      {
      return clientEntry.getStandingFor(factionName);
      }
    else
      {
      throw new RuntimeException("Client side faction data was null while attempting lookup for: "+playerName+" for faction: "+factionName+" for client world: "+world);
      }
    }
  else
    {
    FactionData data = AWGameData.INSTANCE.getData(FactionData.name, world, FactionData.class);
    return data.getStandingFor(playerName, factionName);
    }
  }

private void sendFactionEntry(EntityPlayer player, FactionData data)
  {
  FactionEntry entry = data.getEntryFor(player.getCommandSenderName());
  NBTTagCompound tag = new NBTTagCompound();
  NBTTagCompound initTag = entry.writeToNBT(new NBTTagCompound());
  tag.setTag("factionInit", initTag);  
  PacketFactionUpdate pkt = new PacketFactionUpdate(tag);
  NetworkHandler.sendToPlayer((EntityPlayerMP) player, pkt);
  }

private void sendFactionUpdate(World world, String playerName, String factionName, FactionData data)
  {
  EntityPlayer player = world.getPlayerEntityByName(playerName);
  if(player!=null && player instanceof EntityPlayerMP)
    {
    int standing = data.getStandingFor(playerName, factionName);
    NBTTagCompound tag = new NBTTagCompound();
    NBTTagCompound updateTag = new NBTTagCompound();
    updateTag.setString("faction", factionName);
    updateTag.setInteger("standing", standing);
    tag.setTag("factionUpdate", updateTag);
    PacketFactionUpdate pkt = new PacketFactionUpdate(tag);
    NetworkHandler.sendToPlayer((EntityPlayerMP) player, pkt);
    }
  }

public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("factionUpdate"))
    {
    handleClientFactionUpdate(tag.getCompoundTag("factionUpdate"));
    }
  if(tag.hasKey("factionInit"))
    {
    handleClientFactionInit(tag.getCompoundTag("factionInit"));
    }
  }

private void handleClientFactionUpdate(NBTTagCompound tag)
  {
  String faction = tag.getString("faction");
  int standing = tag.getInteger("standing");
  clientEntry.setStandingFor(faction, standing);
  }

private void handleClientFactionInit(NBTTagCompound tag)
  {
  this.clientEntry = new FactionEntry(tag);
  }

}
