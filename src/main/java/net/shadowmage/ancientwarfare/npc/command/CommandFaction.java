package net.shadowmage.ancientwarfare.npc.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;

public class CommandFaction implements ICommand
{

private int permissionLevel = 2;

public int compareTo(ICommand par1ICommand)
  {
  return this.getCommandName().compareTo(par1ICommand.getCommandName());
  }

@Override
public int compareTo(Object par1Obj)
  {
  return this.compareTo((ICommand)par1Obj);
  }

@Override
public String getCommandName()
  {
  return "awfaction";
  }

@Override
public String getCommandUsage(ICommandSender var1)
  {
  return "command.aw.faction.usage";
  }

@SuppressWarnings("rawtypes")
@Override
public List getCommandAliases()
  {
  return null;
  }

@Override
public void processCommand(ICommandSender var1, String[] var2)
  {
  if(var2.length < 2)
    {
    throw new WrongUsageException(getCommandUsage(var1), new Object[0]);
    }
  String cmd = var2[0];
  String playerName = var2[1];
  if(cmd.toLowerCase().equals("set"))
    {
    if(var2.length<4){throw new WrongUsageException("command.aw.faction.set.usage", new Object[0]);}
    String faction = var2[2];
    if(!isFactionNameValid(faction)){throw new WrongUsageException("command.aw.faction.set.usage", new Object[0]);}
    String amount = var2[3];     
    int amt = 0;
    try{amt = Integer.parseInt(amount);}
    catch(NumberFormatException e){throw new WrongUsageException("command.aw.faction.set.usage", new Object[0]);}
    FactionTracker.INSTANCE.setStandingFor(var1.getEntityWorld(), playerName, faction, amt);
    var1.addChatMessage(new ChatComponentTranslation("command.aw.faction.set", playerName, faction, amt));
    }
  else if(cmd.toLowerCase().equals("setall"))
    {
    if(var2.length<3){throw new WrongUsageException("command.aw.faction.setall.usage", new Object[0]);}
    String amount = var2[2];
    int amt = 0;
    try{amt = Integer.parseInt(amount);}
    catch(NumberFormatException e){throw new WrongUsageException("command.aw.faction.setall.usage", new Object[0]);}    
    for(String faction : AWNPCStatics.factionNames)
      {
      FactionTracker.INSTANCE.setStandingFor(var1.getEntityWorld(), playerName, faction, amt);
      var1.addChatMessage(new ChatComponentTranslation("command.aw.faction.set", playerName, faction, amt));
      }  
    }
  else if(cmd.toLowerCase().equals("get"))
    {
    World world = var1.getEntityWorld();
    var1.addChatMessage(new ChatComponentTranslation("command.aw.faction.status.player", playerName));
    for(String faction : AWNPCStatics.factionNames)
      {
      int standing = FactionTracker.INSTANCE.getStandingFor(world, playerName, faction);
      var1.addChatMessage(new ChatComponentTranslation("command.aw.faction.status.value", faction, standing));
      }     
    }
  }

private boolean isFactionNameValid(String factionName)
  {
  for(String name : AWNPCStatics.factionNames)
    {
    if(name.equalsIgnoreCase(factionName))
      {
      return true;
      }
    }
  return false;
  }

@Override
public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
  {
  return par1ICommandSender.canCommandSenderUseCommand(this.permissionLevel, this.getCommandName());
  }

@SuppressWarnings("rawtypes")
@Override
public List addTabCompletionOptions(ICommandSender var1, String[] var2)
  {
  if(var2.length==1)//the command
    {
    return CommandBase.getListOfStringsMatchingLastWord(var2, "set", "setall", "get");
    }
  else if(var2.length==2)//would be a player name
    {
    return CommandBase.getListOfStringsMatchingLastWord(var2, MinecraftServer.getServer().getAllUsernames());
    }
  else if(var2.length==3)//would be a faction name for the set command
    {
    if(var2[0].toLowerCase().equals("set"))
      {
      return CommandBase.getListOfStringsMatchingLastWord(var2, "bandit", "viking", "desert", "jungle", "pirate", "custom_1", "custom_2", "custom_3");
      }
    }
  else if(var2.length==4)//would be a number for the set command value
    {
    
    }
  return null;
  }

@Override
public boolean isUsernameIndex(String[] var1, int var2)
  {
  return var2==1;
  }

}
