/**
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package shadowmage.ancient_framework.common.config;

import java.util.Collection;
import java.util.HashMap;

public class AWClientConfig
{

private static HashMap<String, ClientConfigOption> clientOptions = new HashMap<String, ClientConfigOption>();


static
{
setBooleanValue("renderOverlay", true);
setBooleanValue("renderAdvancedOverlay", true);
setBooleanValue("renderVehicleFirstPerson", true);
setBooleanValue("renderVehicleNameplates", true);
setBooleanValue("renderNpcNameplates", true);
}

public static void initIntValue(String displayName, String name, int value)
  {
  clientOptions.put(name, new ClientConfigOption(displayName, name, int.class, value));
  }

public static void initBooleanValue(String displayName, String name, boolean value)
  {
  clientOptions.put(name, new ClientConfigOption(displayName, name, boolean.class, value));
  }

public static void setIntValue(String name, int value)
  {
  if(clientOptions.containsKey(name))
    {
    ClientConfigOption option = clientOptions.get(name);
    option.dataClass = int.class;
    option.dataValue = value;
    }
  else
    {
    ClientConfigOption option = new ClientConfigOption(name, name, int.class, value);
    clientOptions.put(name, option);
    } 
  }

public static void setBooleanValue(String name, boolean value)
  {
  if(clientOptions.containsKey(name))
    {
    ClientConfigOption option = clientOptions.get(name);
    option.dataClass = boolean.class;
    option.dataValue = value;
    }
  else
    {
    ClientConfigOption option = new ClientConfigOption(name, name, boolean.class, value);
    clientOptions.put(name, option);
    }  
  }

public static boolean getBooleanValue(String name)
  {
  ClientConfigOption option = clientOptions.get(name);
  if(option!=null && option.dataClass==boolean.class)
    {
    return (Boolean)option.dataValue;
    }  
  return false;
  }

public static int getIntValue(String name)
  {
  ClientConfigOption option = clientOptions.get(name);
  if(option!=null && option.dataClass==int.class)
    {
    return (Integer)option.dataValue;
    }  
  return 0;
  }

public static class ClientConfigOption
  {  

  public String displayName;
  public String optionName;  
  public Class dataClass;
  public Object dataValue;
  public ClientConfigOption(String display, String label, Class clz, Object data)
    {
    displayName = display;
    optionName = label;
    dataClass = clz;
    dataValue = data;
    }
  }

public static Collection<ClientConfigOption> getClientOptions()
  {
  return clientOptions.values();
  }

}
