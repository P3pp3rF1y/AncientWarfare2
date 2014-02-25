/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
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
package shadowmage.ancient_structures.common.container;

import java.io.File;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import shadowmage.ancient_framework.common.container.ContainerBase;
import shadowmage.ancient_framework.common.utils.BlockPosition;
import shadowmage.ancient_framework.common.utils.BlockTools;
import shadowmage.ancient_structures.common.item.AWStructuresItemLoader;
import shadowmage.ancient_structures.common.item.ItemStructureSettings;
import shadowmage.ancient_structures.common.manager.StructureTemplateManager;
import shadowmage.ancient_structures.common.template.StructureTemplate;
import shadowmage.ancient_structures.common.template.build.validation.StructureValidationType;
import shadowmage.ancient_structures.common.template.build.validation.StructureValidator;
import shadowmage.ancient_structures.common.template.load.TemplateLoader;
import shadowmage.ancient_structures.common.template.save.TemplateExporter;
import shadowmage.ancient_structures.common.template.scan.TemplateScanner;

public class ContainerStructureScanner extends ContainerBase
{

ItemStructureSettings settings = new ItemStructureSettings();

public ContainerStructureScanner(EntityPlayer openingPlayer, int x, int y, int z)
  {
  super(openingPlayer, x, y, z );
  if(player.worldObj.isRemote)
    {
    return;
    }
  ItemStack builderItem = player.inventory.getCurrentItem();
  if(builderItem==null || builderItem.getItem()==null || builderItem.getItem()!=AWStructuresItemLoader.structureScanner)
    {
    return;
    } 
  ItemStructureSettings.getSettingsFor(builderItem, settings);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("export"))
    {
    boolean include = tag.getBoolean("export");
    String name = tag.getString("name");
    scanStructure(player.worldObj, settings.pos1(), settings.pos2(), settings.buildKey(), settings.face(), name, include, tag);
    settings.clearSettings();    
    }
  if(tag.hasKey("reset"))
    {
    settings.clearSettings();
    }
  }

public boolean scanStructure(World world, BlockPosition pos1, BlockPosition pos2, BlockPosition key, int face, String name, boolean include, NBTTagCompound tag)
  {
  BlockPosition min = BlockTools.getMin(pos1, pos2);
  BlockPosition max = BlockTools.getMax(pos1, pos2);
  TemplateScanner scanner = new TemplateScanner();
  int turns = face==0 ? 2 : face==1 ? 1 : face==2 ? 0 : face==3 ? 3 : 0; //because for some reason my mod math was off?  
  StructureTemplate template = scanner.scan(world, min, max, key, turns, name);

  String validationType = tag.getString("validationType");
  StructureValidationType type = StructureValidationType.getTypeFromName(validationType);
  StructureValidator validator = type.getValidator();
  validator.readFromTag(tag);  
  template.setValidationSettings(validator);
  if(include)
    {
    StructureTemplateManager.instance().addTemplate(template);    
    }
  TemplateExporter.exportTo(template, new File(include ? TemplateLoader.includeDirectory : TemplateLoader.outputDirectory));
  return true;
  }

@Override
public void onContainerClosed(EntityPlayer par1EntityPlayer)
  {
  super.onContainerClosed(par1EntityPlayer);
  if(par1EntityPlayer.worldObj.isRemote)
    {
    return;
    }
  ItemStack builderItem = player.inventory.getCurrentItem();  
  if(builderItem==null || builderItem.getItem()==null || builderItem.getItem()!=AWStructuresItemLoader.structureScanner)
    {
    return;
    }
  ItemStructureSettings.setSettingsFor(builderItem, settings); 
  }

@Override
public void handleInitData(NBTTagCompound tag)
  {
 
  }

@Override
public List<NBTTagCompound> getInitData()
  {  
  return Collections.emptyList();
  }

}
