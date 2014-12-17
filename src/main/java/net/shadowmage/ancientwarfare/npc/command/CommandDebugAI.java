package net.shadowmage.ancientwarfare.npc.command;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.shadowmage.ancientwarfare.core.gamedata.WorldData;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;

public class CommandDebugAI extends CommandBase
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
  return "awnpcdebug";
  }

@Override
public String getCommandUsage(ICommandSender var1)
  {
  return "command.aw.npcdebug.usage";
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
  boolean debugMode = AWNPCStatics.npcAIDebugMode;
  debugMode = !debugMode;
  AWNPCStatics.npcAIDebugMode = debugMode;  
  WorldData d = (WorldData) var1.getEntityWorld().perWorldStorage.loadData(WorldData.class, WorldData.name);  
  d.set("NpcAIDebugMode", debugMode);  
  var1.addChatMessage(new ChatComponentText("command.aw.npcdebug.used"));  
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
  return null;
  }

@Override
public boolean isUsernameIndex(String[] var1, int var2)
  {
  return false;
  }

}
