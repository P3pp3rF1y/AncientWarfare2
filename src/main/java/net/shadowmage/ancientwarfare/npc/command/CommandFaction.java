package net.shadowmage.ancientwarfare.npc.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
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
    if(var2.length<4){throw new WrongUsageException(getCommandUsage(var1), new Object[0]);}
    String faction = var2[2];
    String amount = var2[3];    
    AWLog.logDebug("set faction for player: "+playerName + " faction: "+faction+ " faction"+ " amount: "+amount);
    }
  else if(cmd.toLowerCase().equals("setall"))
    {
    if(var2.length<3){throw new WrongUsageException(getCommandUsage(var1), new Object[0]);}
    String amount = var2[2];
    AWLog.logDebug("setall faction for player: "+playerName + " amount: "+amount);    
    }
  else if(cmd.toLowerCase().equals("get"))
    {
    AWLog.logDebug("get faction for player: "+playerName);
    World world = var1.getEntityWorld();
    int faction = FactionTracker.INSTANCE.getStandingFor(world, playerName, "bandit");
    
    }
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
  if(var2.length==1)
    {
    return CommandBase.getListOfStringsMatchingLastWord(var2, "set", "setall", "get");
    }
  if(var2.length==2)
    {
    }//would be a player name
  if(var2.length==3)
    {
    if(var2[0].toLowerCase().equals("set") || var2[0].toLowerCase().equals("get"))
      {
      return CommandBase.getListOfStringsMatchingLastWord(var2, "bandit", "viking", "desert", "jungle", "pirate", "custom_1", "custom_2", "custom_3");
      }
    else if(var2[0].toLowerCase().equals("setall"))
      {
      return null;
      }
    }
  return null;
  }

@Override
public boolean isUsernameIndex(String[] var1, int var2)
  {
  String input = "";
  for(int i = 0; i < var1.length; i++)
    {
    input = input + " " + var1[i];
    }
  AWLog.logDebug("is username index: "+var2+" "+input);
  return var2==1;
  }

}
