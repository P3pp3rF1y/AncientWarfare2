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
package shadowmage.ancient_structures.common.template.load;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_structures.common.config.AWStructureStatics;
import shadowmage.ancient_structures.common.manager.StructureTemplateManager;
import shadowmage.ancient_structures.common.template.StructureTemplate;

import com.google.common.io.ByteStreams;

public class TemplateLoader
{

public static final String defaultTemplateDirectory = "/assets/ancientwarfare/templates/";
public static String outputDirectory = null;
public static String includeDirectory = null;
public static String convertDirectory = null;
public static String configBaseDirectory = null;

private List<String> defaultExportStructures = new ArrayList<String>();
private List<File> probableStructureFiles = new ArrayList<File>();

private TemplateLoader(){}
private static TemplateLoader instance = new TemplateLoader(){};
public static TemplateLoader instance(){return instance;}

private void setDefaultStructureNames()
  {
  this.defaultExportStructures.add("villageGardenLarge.aws");
  this.defaultExportStructures.add("villageGardenSmall.aws");
  this.defaultExportStructures.add("villageHouse1.aws");
  this.defaultExportStructures.add("villageHouse2.aws");
  this.defaultExportStructures.add("villageHouseGarden.aws");
  this.defaultExportStructures.add("villageHouseSmall.aws");
  this.defaultExportStructures.add("villageHouseSmall2.aws");
  this.defaultExportStructures.add("villageLibrary.aws");
  this.defaultExportStructures.add("villageSmith.aws");
  this.defaultExportStructures.add("villageTorch.aws");
  this.defaultExportStructures.add("villageWell.aws");
  this.defaultExportStructures.add("advancedVillageLibrary.aws");
  this.defaultExportStructures.add("obsidianVault.aws");
  this.defaultExportStructures.add("banditCamp.aws");
  this.defaultExportStructures.add("lavaFarm.aws");
  this.defaultExportStructures.add("fountain1.aws");
  this.defaultExportStructures.add("logCabin.aws");
  this.defaultExportStructures.add("fortress1.aws");
  this.defaultExportStructures.add("fortress2.aws");
  this.defaultExportStructures.add("tower1.aws");
  }

public void initializeAndExportDefaults(String path)
  {   
  outputDirectory = path+"/AWConfig/structures/export/";
  includeDirectory = path+"/AWConfig/structures/included/";
  convertDirectory = path+"/AWConfig/structures/convert/";
  configBaseDirectory = path+"/AWConfig/"; 

  /**
   * create default dirs if they don't exist...
   */
  File existTest = new File(outputDirectory);
  if(!existTest.exists())
    {
    AWLog.log("Creating default Export Directory");
    existTest.mkdirs();
    }

  existTest = new File(includeDirectory);
  if(!existTest.exists())
    {
    AWLog.log("Creating default Include Directory");
    existTest.mkdirs();
    }
  
  existTest = new File(convertDirectory);
  if(!existTest.exists())
    {
    AWLog.log("Creating default Convert Directory");
    existTest.mkdirs();
    }
  
  existTest = new File(configBaseDirectory);
  if(!existTest.exists())
    {
    AWLog.log("Creating AWConfig directory in config/");
    existTest.mkdirs();
    }
     
  if(AWStructureStatics.shouldExport)
    {
    this.setDefaultStructureNames();
    this.copyDefaultStructures(includeDirectory);
    AWStructureStatics.shouldExport = false;
    }
  this.defaultExportStructures.clear();
  }

public void loadTemplates()
  {
  this.locateStructureFiles();
  StructureTemplate template;
  int loadedCount = 0;
  for(File f : this.probableStructureFiles)
    {
    template = TemplateParser.instance().parseTemplate(f);
    if(template!=null)
      { 
      StructureTemplateManager.instance().addTemplate(template);
      loadedCount++;
      }
    else
      {
      AWLog.logError("Could not load template for: "+f.getAbsolutePath() + " error parsing.");
      }
    }
  AWLog.log("Loaded "+loadedCount+" structure(s).");
  this.probableStructureFiles.clear();
  }

private void copyDefaultStructures(String pathName)
  { 
  InputStream is = null;
  FileOutputStream os = null;
  File file = null;
  AWLog.log("Exporting default structures....");
  int exportCount = 0;
  byte[] byteBuffer;
  for(String fileName : this.defaultExportStructures)
    {
    try
      {
      is = this.getClass().getResourceAsStream(defaultTemplateDirectory+fileName);
      if(is==null)
        {
        continue;
        }
      
      String trimmedName = fileName.substring(0, fileName.length()-4);
      fileName = trimmedName +"."+AWStructureStatics.templateExtension;
      file = new File(includeDirectory,fileName);
  
      if(!file.exists())
        {
        AWLog.log("Exporting: "+fileName);
        file.createNewFile();
        }
      else
        {
        AWLog.log("Overwriting: "+fileName);
        }
  
      byteBuffer = ByteStreams.toByteArray(is);
      is.close();
      if(byteBuffer.length>0)
        {
        os = new FileOutputStream(file);        
        os.write(byteBuffer);
        os.close();
        exportCount++;
        }
      }
    catch(Exception e)
      {
      AWLog.logError("Error during export of: "+fileName);
      e.printStackTrace();
      }    
    }
  AWLog.log("Exported "+exportCount+" structures");  
  }

private void createDirectory(File file)
  {
  if(!file.exists())
    {
    file.mkdirs();
    }
  }

private void locateStructureFiles()
  {
  this.recursiveScan(new File(includeDirectory), probableStructureFiles, AWStructureStatics.templateExtension);
  }

private void recursiveScan(File directory, List<File> fileList, String extension)
  {
  if(directory==null)
    {
    AWLog.logError("Could not locate "+directory+" directory to load structures!");
    return;
    }
  File[] allFiles = directory.listFiles();
  if(allFiles==null)
    {
    AWLog.logError("Could not locate "+directory+" directory to load structures!--no files in directory file list!");
    return;
    }
  File currentFile;
  for(int i = 0; i < allFiles.length; i++)
    {
    currentFile = allFiles[i];
    if(currentFile.isDirectory())
      {
      recursiveScan(currentFile, fileList, extension);
      }
    else if(isProbableFile(currentFile, extension))
      {
      fileList.add(currentFile);
      }
    }
  }

private boolean isProbableFile(File file, String extension)
  {
  return file.getName().toLowerCase().endsWith(extension);
  }


}
