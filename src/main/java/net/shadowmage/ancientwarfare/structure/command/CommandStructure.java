package net.shadowmage.ancientwarfare.structure.command;

import java.io.File;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateLoader;

public class CommandStructure implements ICommand
{

private int permissionLevel = 2;

public CommandStructure()
  {
  }

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
  return "awstructure";
  }

@Override
public String getCommandUsage(ICommandSender var1)
  {
  return "command.aw.structure.usage";
  }

@Override
public List getCommandAliases()
  {
  return null;
  }

@Override
public void processCommand(ICommandSender var1, String[] var2)
  {
  if(var2.length==0)
    {
    throw new WrongUsageException(getCommandUsage(var1), new Object[0]);
    }
  String cmd = var2[0];
  if(cmd.toLowerCase().equals("delete"))
    {
    if(var2.length<2)
      {
      throw new WrongUsageException(getCommandUsage(var1), new Object[0]);
      }
    String name = var2[1];
    boolean flag = StructureTemplateManager.instance().removeTemplate(name);
    if(flag)//check if var2.len>=3, pull string of end...if string==true, try delete template file for name
      {
      ChatComponentTranslation txt = new ChatComponentTranslation("command.aw.structure.template_removed", name);
      var1.addChatMessage(txt);
      AWLog.logDebug("template removed successfully...");//TODO send chat message
      if(var2.length>=3)
        {
        boolean shouldDelete = var2[2].toLowerCase().equals("true");
        if(shouldDelete)
          {
          if(deleteTemplateFile(name))
            {
            txt = new ChatComponentTranslation("command.aw.structure.file_deleted", name);
            var1.addChatMessage(txt);
            AWLog.logDebug("deleted template file of: "+name);
            }
          else
            {
            txt = new ChatComponentTranslation("command.aw.structure.file_not_found", name);
            var1.addChatMessage(txt);
            AWLog.logDebug("could not locate template file for: "+name+" it is probably not in the root include/ directory");
            }          
          }
        }      
      }
    else//send template not found message
      {
      AWLog.logDebug("could not find template by name: "+name);
      ChatComponentTranslation txt = new ChatComponentTranslation("command.aw.structure.not_found", name);
      var1.addChatMessage(txt);
      }
    }
  else if(cmd.toLowerCase().equals("build"))
    {
    if(var2.length<5)
      {
      throw new WrongUsageException(getCommandUsage(var1), new Object[0]);
      }
    }
  }

private boolean deleteTemplateFile(String name)
  {
  String path = TemplateLoader.includeDirectory+name+"."+AWStructureStatics.templateExtension;
  File file = new File(path);
  if(file.exists())
    {
    file.delete();
    return true;
    }
  return false;
  }

@Override
public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
  {
  return par1ICommandSender.canCommandSenderUseCommand(this.permissionLevel, this.getCommandName());
  }

@Override
public List addTabCompletionOptions(ICommandSender var1, String[] var2)
  {
  if(var2.length==1)
    {
    return CommandBase.getListOfStringsMatchingLastWord(var2, "build", "delete");
    }
  return null;
  }

@Override
public boolean isUsernameIndex(String[] var1, int var2)
  {
  return false;
  }

}
