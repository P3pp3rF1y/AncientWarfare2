package net.shadowmage.ancientwarfare.core.config;

import java.util.Collection;
import java.util.HashMap;

import net.minecraftforge.common.config.Configuration;

public class ClientOptions
{

public static final ClientOptions INSTANCE = new ClientOptions();
private ClientOptions(){}

private Configuration config;
private HashMap<String, ClientOption> clientOptions = new HashMap<String, ClientOption>();

private static final String clientOptionsCategory = "c_client_options";

public void setConfig(Configuration config)
  {
  this.config = config;
  }

/**
 * registers the client-option, with default value.  actual value will be loaded
 * from config file.
 * @param name
 * @param comment
 * @param value
 */
public void registerClientOption(String name, String comment, Object value)
  {
  ClientOption option = new ClientOption(name, value);
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
      option.setValue(config.get(clientOptionsCategory, name, option.getBooleanValue()).getBoolean(option.getBooleanValue()));
      }
    else if(option.isIntValue())
      {
      option.setValue(config.get(clientOptionsCategory, name, option.getIntValue()).getInt(option.getIntValue()));
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

private static final class ClientOption
{
private String name;
private Object value;

private ClientOption(String name, Object value)
  {
  this.value = value;
  this.name = name;
  }

public boolean getBooleanValue()
  {
  return value instanceof Boolean ? ((Boolean)value) : false;
  }

public int getIntValue()
  {
  return value instanceof Integer ? ((Integer)value) : 0;
  }

public String getName()
  {
  return name;
  }

public void setValue(Object value)
  {
  this.value = value;
  }

public boolean isBooleanValue()
  {
  return value instanceof Boolean;
  }

public boolean isIntValue()
  {
  return value instanceof Integer;
  }

}

}
