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
package net.shadowmage.ancientwarfare.structure.template;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.structure.template.plugin.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.StructurePluginVanillaHandler;
import net.shadowmage.ancientwarfare.structure.template.rule.TemplateRule;
import net.shadowmage.ancientwarfare.structure.template.rule.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.template.rule.TemplateRuleEntity;

public class StructurePluginManager
{


private List<StructureContentPlugin> loadedContentPlugins = new ArrayList<StructureContentPlugin>();

private HashMap<Class<?extends Entity>, Class<? extends TemplateRule>> entityRules = new HashMap<Class<?extends Entity>, Class<? extends TemplateRule>>();
private HashMap<Block, Class<?extends TemplateRule>> blockRules = new HashMap<Block, Class<?extends TemplateRule>>();
private HashMap<Class<?extends TemplateRule>, String> idByRuleClass = new HashMap<Class<? extends TemplateRule>, String>();
private HashMap<String, Class<?extends TemplateRule>> ruleByID = new HashMap<String, Class<?extends TemplateRule>>();
private HashMap<Block, String> pluginByBlock = new HashMap<Block, String>();
private HashMap<Class<? extends Entity>, String> pluginByEntity = new HashMap<Class<? extends Entity>, String>();

private StructurePluginVanillaHandler vanillaPlugin;

private static StructurePluginManager instance = new StructurePluginManager();
private StructurePluginManager(){}
public static StructurePluginManager instance(){return instance;}

public void loadPlugins()
  {
  vanillaPlugin = new StructurePluginVanillaHandler();
  this.addPlugin(vanillaPlugin);
  
//  if(AWFramework.loadedAutomation){StructurePluginAutomation.load();}
//  if(AWFramework.loadedNpcs){StructurePluginNpcs.load();}
//  if(AWFramework.loadedVehicles){StructurePluginVehicles.load();}
  
  for(StructureContentPlugin plugin : this.loadedContentPlugins)
    {
    plugin.addHandledBlocks(this);
    plugin.addHandledEntities(this);
    }
  }

public void addPlugin(StructureContentPlugin plugin)
  {
  loadedContentPlugins.add(plugin);
  }

public String getPluginNameForEntity(Class<? extends Entity> entityClass)
  {
  return this.pluginByEntity.get(entityClass);
  }

public String getPluginNameFor(Block block)
  {  
  return pluginByBlock.get(block);
  }

public String getPluginNameFor(Class<?extends TemplateRule> ruleClass)
  {
  return this.idByRuleClass.get(ruleClass);
  }

public Class<?extends TemplateRule> getRuleByName(String name)
  {
  return this.ruleByID.get(name);
  }

public TemplateRuleBlock getRuleForBlock(World world, Block block, int turns, int x, int y, int z)
  {
  TemplateRule rule;    
  Class<?extends TemplateRule> clz = blockRules.get(block);
  if(clz!=null)
    {
    int meta = world.getBlockMetadata(x, y, z);  
    try
      {
      rule = clz.getConstructor(World.class, int.class, int.class, int.class, Block.class, int.class, int.class).newInstance(world, x, y, z, block, meta, turns);
      return (TemplateRuleBlock) rule;
      } 
    catch (InstantiationException e)
      {
      e.printStackTrace();
      } 
    catch (IllegalAccessException e)
      {
      e.printStackTrace();
      } 
    catch (IllegalArgumentException e)
      {
      e.printStackTrace();
      } 
    catch (InvocationTargetException e)
      {
      e.printStackTrace();
      } 
    catch (NoSuchMethodException e)
      {
      e.printStackTrace();
      } 
    catch (SecurityException e)
      {
      e.printStackTrace();
      }      
    }  
  return null;
  }

public TemplateRuleEntity getRuleForEntity(World world, Entity entity, int turns, int x, int y, int z)
  {
  Class<? extends Entity> entityClass = entity.getClass();
  if(this.entityRules.containsKey(entityClass))
    {
    Class<? extends TemplateRule> entityRuleClass = this.entityRules.get(entityClass);
    if(entityRuleClass!=null)
      {
      try
        {
        return (TemplateRuleEntity) entityRuleClass.getConstructor(World.class, Entity.class, int.class, int.class, int.class, int.class).newInstance(world, entity, turns, x, y, z);
        } 
      catch (InstantiationException e)
        {
        e.printStackTrace();
        } 
      catch (IllegalAccessException e)
        {
        e.printStackTrace();
        } 
      catch (IllegalArgumentException e)
        {
        e.printStackTrace();
        } 
      catch (InvocationTargetException e)
        {
        e.printStackTrace();
        } 
      catch (NoSuchMethodException e)
        {
        e.printStackTrace();
        } 
      catch (SecurityException e)
        {
        e.printStackTrace();
        }
      }
    }
  return null;//TODO
  }

public void registerEntityHandler(String pluginName, Class<?extends Entity> entityClass, Class<? extends TemplateRule> ruleClass)
  {
  if(ruleByID.containsKey(pluginName))
    {
    if(!ruleByID.get(pluginName).equals(ruleClass))
      {
      Class<? extends TemplateRule> clz = ruleByID.get(pluginName);
      throw new IllegalArgumentException("Attempt to overwrite "+clz+" with "+ruleClass+" by "+pluginName + " for entityClass: "+entityClass);
      }
    }
  entityRules.put(entityClass, ruleClass);
  ruleByID.put(pluginName, ruleClass);
  idByRuleClass.put(ruleClass, pluginName);
  pluginByEntity.put(entityClass, pluginName);
  }

public void registerBlockHandler(String pluginName, Block block, Class<? extends TemplateRule> ruleClass)
  {  
  if(ruleByID.containsKey(pluginName))
    {
    if(!ruleByID.get(pluginName).equals(ruleClass))
      {
      Class<? extends TemplateRule> clz = ruleByID.get(pluginName);
      throw new IllegalArgumentException("Attempt to overwrite "+clz+" with "+ruleClass+" by "+pluginName + " for block: "+block);
      }
    }  
  blockRules.put(block, ruleClass);
  ruleByID.put(pluginName, ruleClass);
  idByRuleClass.put(ruleClass, pluginName);
  pluginByBlock.put(block, pluginName);  
  }

}
