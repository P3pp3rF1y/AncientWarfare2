package net.shadowmage.ancientwarfare.core.config;

import java.util.Collection;
import java.util.HashMap;

import net.minecraftforge.common.config.Configuration;

public class ClientOptions
{

public static final ClientOptions INSTANCE = new ClientOptions();
private ClientOptions(){}

private HashMap<String, ClientOption> clientOptions = new HashMap<String, ClientOption>();

private static final String clientOptionsCategory = AWCoreStatics.clientOptions;

/**
 * automation module client-side options
 */
public static final String OPTION_RENDER_WORK_BOUNDS = "render_work_bounds";
public static final String OPTION_RENDER_WORK_POINTS = "render_work_points";

/**
 * npc module client-side options
 */
public static final String OPTION_RENDER_NPC_AI = "render_npc_ai";
public static final String OPTION_RENDER_NPC_ADDITIONAL_INFO = "render_nameplates";
public static final String OPTION_RENDER_NPC_HOSTILE_NAMES = "render_hostile_names";
public static final String OPTION_RENDER_NPC_FRIENDLY_NAMES = "render_friendly_names";

public void registerClientOption(String name, String comment, boolean val, Configuration config)
  {
  ClientOption option = new ClientOption(name, val, comment, config);
  clientOptions.put(name, option);
  }

public void registerClientOption(String name, String comment, int val, Configuration config)
  {
  ClientOption option = new ClientOption(name, val, comment, config);
  clientOptions.put(name, option);
  }

/**
 * should be called during core-module init.<br>
 * any modules adding client-options should register them<br>
 * during PRE-init phase 
 */
public void loadClientOptions()
  {
  ClientOption option;
  for(String name : this.clientOptions.keySet())
    {
    option = clientOptions.get(name);
    if(option.isBooleanValue())
      {
      option.setValue(option.config.get(clientOptionsCategory, name, option.getBooleanValue(), option.getComment()).getBoolean(option.getBooleanValue()));
      }
    else if(option.isIntValue())
      {
      option.setValue(option.config.get(clientOptionsCategory, name, option.getIntValue(), option.getComment()).getInt(option.getIntValue()));
      }
    }
  }

public int getIntValue(String optionName)
  {
  if(clientOptions.containsKey(optionName))
    {
    return clientOptions.get(optionName).getIntValue();
    }
  return 0;
  }

public boolean getBooleanValue(String optionName)
  {
  if(clientOptions.containsKey(optionName))
    {
    return clientOptions.get(optionName).getBooleanValue();
    }
  return false;
  }

public Collection<String> getOptionKeys()
  {
  return clientOptions.keySet();
  }

public ClientOption getClientOption(String name)
  {
  return clientOptions.get(name);
  }

public static final class ClientOption
{
Configuration config;
private String name;
private String comment;
private boolean isBoolean = false;

private boolean booleanVal;
private int intVal;

private ClientOption(String name, int value, String comment, Configuration config)
  {
  this.intVal = value;
  isBoolean = false;
  this.name = name;
  this.comment = comment;
  this.config = config;
  }

private ClientOption(String name, boolean value, String comment, Configuration config)
  {
  this.booleanVal = value;
  isBoolean = true;
  this.name = name;
  this.comment = comment;
  this.config = config;
  }

public boolean getBooleanValue()
  {
  return isBoolean ? booleanVal : false;
  }

public int getIntValue()
  {
  return !isBoolean ? intVal : 0;
  }

public String getName()
  {
  return name;
  }

public void setValue(int value)
  {
  if(!isBoolean)
    {
    intVal = value;
    }
  }

public void setValue(boolean val)
  {
  if(isBoolean)
    {
    this.booleanVal = val;
    }
  }

public boolean isBooleanValue()
  {
  return isBoolean;
  }

public boolean isIntValue()
  {
  return !isBoolean;
  }

public String getComment()
  {
  return comment;
  }

}

}
