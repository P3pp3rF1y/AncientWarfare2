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
package shadowmage.ancient_structures.common.template.save;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_structures.common.config.AWStructureStatics;
import shadowmage.ancient_structures.common.template.StructureTemplate;
import shadowmage.ancient_structures.common.template.build.validation.StructureValidator;
import shadowmage.ancient_structures.common.template.rule.TemplateRule;

public class TemplateExporter
{


public static void exportTo(StructureTemplate template, File directory)
  {
  File exportFile = new File(directory, template.name+"."+AWStructureStatics.templateExtension);
  if(!exportFile.exists())
    {
    try
      {
      exportFile.createNewFile();
      } 
    catch (IOException e)
      {
      AWLog.logError("Could not export template..could not create file : "+exportFile.getAbsolutePath());
      e.printStackTrace();
      return;
      }
    }  
  BufferedWriter writer = null;
  try
    {
    writer = new BufferedWriter(new FileWriter(exportFile));
    
    writeHeader(template, writer);    
    writeValidationSettings(template.getValidationSettings(), writer);    
    writeLayers(template, writer);
    
    writer.write("#### RULES ####");
    writer.newLine();
    TemplateRule[] templateRules = template.getTemplateRules();
    for(TemplateRule rule : templateRules)
      {
      TemplateRule.writeRuleLines(rule, writer, "rule");
      }    
    writer.write("#### ENTITIES ####");
    writer.newLine();
    templateRules = template.getEntityRules();
    for(TemplateRule rule : templateRules)
      {
      TemplateRule.writeRuleLines(rule, writer, "entity");
      }    
    } 
  catch (IOException e)
    {
    AWLog.logError("Could not export template..could not create file : "+exportFile.getAbsolutePath());
    e.printStackTrace();
    }
  finally
    {
    if(writer!=null)
      {
      try
        {
        writer.close();
        } 
      catch (IOException e)
        {
        AWLog.logError("Could not export template..could not close file : "+exportFile.getAbsolutePath());
        e.printStackTrace();
        }
      }
    }
  }

private static void writeValidationSettings(StructureValidator settings, BufferedWriter writer) throws IOException
  {
  writer.write("#### VALIDATION ####");    
  writer.newLine(); 
  writer.write("validation:");
  writer.newLine();
  StructureValidator.writeValidator(writer, settings);
  writer.write(":endvalidation");
  writer.newLine();
  writer.newLine();
  }

private static void writeHeader(StructureTemplate template, BufferedWriter writer) throws IOException
  {
  Calendar cal = Calendar.getInstance();
  writer.write("# Ancient Warfare Structure Template File");
  writer.newLine();
  writer.write("# auto-generated structure file. created on: "+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.YEAR)+ " at: "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND));
  writer.newLine();
  writer.write("# Lines beginning with # denote comments");
  writer.newLine();
  writer.newLine();
  writer.write("header:");
  writer.newLine();
  writer.write("version=2.0");
  writer.newLine();    
  writer.write("name="+template.name);
  writer.newLine();
  writer.write("size="+template.xSize+","+template.ySize+","+template.zSize);
  writer.newLine();
  writer.write("offset="+template.xOffset+","+template.yOffset+","+template.zOffset);
  writer.newLine();
  writer.write(":endheader");
  writer.newLine();
  writer.newLine();
  }

private static void writeLayers(StructureTemplate template, BufferedWriter writer) throws IOException
  { 
  writer.write("#### LAYERS ####");
  writer.newLine();
  for(int y = 0; y< template.ySize; y++)
    {
    writer.write("layer: "+y);
    writer.newLine();
    for(int z = 0 ; z<template.zSize; z++)
      {
      for(int x = 0; x<template.xSize; x++)
        {
        short data = template.getTemplateData()[StructureTemplate.getIndex(x, y, z, template.xSize, template.ySize, template.zSize)];          
        writer.write(String.valueOf(data));
        if(x<template.xSize-1)
          {
          writer.write(",");
          }
        }
      writer.newLine();
      }
    writer.write(":endlayer");
    writer.newLine();
    }
  writer.newLine();
  }






}
