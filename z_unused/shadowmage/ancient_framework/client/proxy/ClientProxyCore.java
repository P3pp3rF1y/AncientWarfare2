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
package shadowmage.ancient_framework.client.proxy;

import shadowmage.ancient_framework.client.gui.options.GuiOptions;
import shadowmage.ancient_framework.client.gui.teams.GuiTeamControl;
import shadowmage.ancient_framework.client.input.KeybindManager;
import shadowmage.ancient_framework.client.input.TickHandlerClientKeyboard;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.config.Statics;
import shadowmage.ancient_framework.common.network.GUIHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxyCore extends ClientProxyBase
{

public ClientProxyCore()
  {
  // TODO Auto-generated constructor stub
  }

@Override
public void registerClientData()
  {  
  AWLog.logDebug("registering client data for core proxy");  
  KeyBindingRegistry.registerKeyBinding(new KeybindManager());
  super.registerClientData();
  }

@Override
public void registerGuis()
  {
  GUIHandler.instance().registerGui(Statics.guiOptions, GuiOptions.class);
  GUIHandler.instance().registerGui(Statics.guiTeamControl, GuiTeamControl.class);
  }

@Override
public void registerTickHandlers()
  {
  TickRegistry.registerTickHandler(new TickHandlerClientKeyboard(), Side.CLIENT);
  }

@Override
public void registerRenderers()
  {
  
  }

@Override
public void registerKeybinds()
  {
  
  }

@Override
public void registerEventHandlers()
  {
  
  }

}
