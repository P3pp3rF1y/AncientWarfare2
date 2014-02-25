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
package shadowmage.ancient_structures.client.proxy;

import net.minecraftforge.common.MinecraftForge;
import shadowmage.ancient_framework.client.proxy.ClientProxyBase;
import shadowmage.ancient_framework.common.config.Statics;
import shadowmage.ancient_framework.common.network.GUIHandler;
import shadowmage.ancient_structures.client.gui.structure.GuiSpawnerPlacer;
import shadowmage.ancient_structures.client.gui.structure.GuiStructureScanner;
import shadowmage.ancient_structures.client.gui.structure.GuiStructureSelection;
import shadowmage.ancient_structures.client.render.StructureBoundingBoxRenderer;

public class ClientProxyStructure extends ClientProxyBase
{

public ClientProxyStructure()
  {
  
  }

@Override
public void registerGuis()
  {
  GUIHandler.instance().registerGui(Statics.guiStructureBuilderCreative, GuiStructureSelection.class);
  GUIHandler.instance().registerGui(Statics.guiStructureScannerCreative, GuiStructureScanner.class);
  GUIHandler.instance().registerGui(Statics.guiSpawnerPlacer, GuiSpawnerPlacer.class);
  }

@Override
public void registerKeybinds()
  {
  // TODO Auto-generated method stub
  
  }

@Override
public void registerTickHandlers()
  {
  // TODO Auto-generated method stub
  
  }

@Override
public void registerRenderers()
  {
  // TODO Auto-generated method stub
  
  }

@Override
public void registerEventHandlers()
  {
  MinecraftForge.EVENT_BUS.register(StructureBoundingBoxRenderer.instance());
  }

}
