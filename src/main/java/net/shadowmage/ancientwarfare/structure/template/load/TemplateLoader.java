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
package net.shadowmage.ancientwarfare.structure.template.load;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;

public class TemplateLoader
{

public static final String defaultTemplateDirectory = "/assets/ancientwarfare/templates/";
public static String outputDirectory = null;
public static String includeDirectory = null;
public static String configBaseDirectory = null;

private List<File> probableStructureFiles = new ArrayList<File>();
private List<File> probableZipFiles = new ArrayList<File>();

private TemplateLoader(){}
private static TemplateLoader instance = new TemplateLoader(){};
public static TemplateLoader instance(){return instance;}

public void initializeAndExportDefaults(String path)
  {   
  outputDirectory = path+"/AWConfig/structures/export/";
  includeDirectory = path+"/AWConfig/structures/included/";
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
  }

public void loadTemplates()
  {
  this.locateStructureFiles();
  StructureTemplate template;
  int loadedCount = 0;
  for(File f : this.probableStructureFiles)
    {
    AWLog.logDebug("loading template: "+f.getName());
    template = loadTemplateFromFile(f);
    if(template!=null)
      { 
      StructureTemplateManager.instance().addTemplate(template);
      loadedCount++;
      }
    }
  loadedCount+=this.loadTemplatesFromZip();
  AWLog.log("Loaded "+loadedCount+" structure(s).");
  this.probableStructureFiles.clear();
  this.probableZipFiles.clear();
  }

private StructureTemplate loadTemplateFromFile(File file)
  {
  FileReader reader = null;
  Scanner scan = null;
  List<String> templateLines = new ArrayList<String>();
  try
    {
    reader = new FileReader(file);
    scan = new Scanner(reader);
    while(scan.hasNext())
      {
      templateLines.add(scan.nextLine());
      }
    return TemplateParser.instance().parseTemplate(file.getName(), templateLines);
    } 
  catch (FileNotFoundException e)
    {
    e.printStackTrace();
    return null;
    }
  finally
    {
    if(scan!=null){scan.close();}
    }
  }

private int loadTemplatesFromZip()
  {
  ZipFile z;
  ZipEntry entry;
  Enumeration<? extends ZipEntry> zipEntries;
  StructureTemplate template;
  int parsed = 0;
  int totalParsed = 0;
  for(File f : this.probableZipFiles)
    {
    parsed = 0;
    AWLog.logDebug("parsing templates from zip file: "+f.getName());
    try
      {
      z = new ZipFile(f);
      zipEntries = z.entries();
      while(zipEntries.hasMoreElements())
        {
        entry = zipEntries.nextElement();
        if(entry.isDirectory()){continue;}//TODO how to handle subfolders in a zip-file?
        AWLog.logDebug("loading template: "+entry.getName());
        template = loadTemplateFromZip(entry, z.getInputStream(entry));
        if(template!=null)
          {
          StructureTemplateManager.instance().addTemplate(template);
          parsed++;
          }
        }
      } 
    catch (ZipException e)
      {
      e.printStackTrace();
      } 
    catch (IOException e)
      {
      e.printStackTrace();
      }
    AWLog.logDebug("parsed : "+parsed+" templates from zip file.");
    totalParsed+=parsed;
    }
  return totalParsed;
  }

private StructureTemplate loadTemplateFromZip(ZipEntry entry, InputStream is)
  {
  InputStreamReader isr = new InputStreamReader(is);
  BufferedReader reader = new BufferedReader(isr);
  List<String> lines = new ArrayList<String>();
  String line;
  StructureTemplate template = null;
  try
    {
    while((line = reader.readLine())!=null)
      {
      lines.add(line);
      }
    template = TemplateParser.instance().parseTemplate(entry.getName(), lines);
    } 
  catch (IOException e1)
    {
    e1.printStackTrace();
    template = null;
    }  
  try
    {
    reader.close();
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  try
    {
    isr.close();
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  try
    {
    is.close();
    }
  catch (IOException e)
    {
    e.printStackTrace();
    }
  return template;
  }

private void locateStructureFiles()
  {
  this.recursiveScan(new File(includeDirectory), probableStructureFiles, probableZipFiles, AWStructureStatics.templateExtension);
  }

private void recursiveScan(File directory, List<File> fileList, List<File> zipFileList, String extension)
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
      recursiveScan(currentFile, fileList, zipFileList, extension);
      }
    else if(isProbableFile(currentFile, extension))
      {
      fileList.add(currentFile);
      }
    else if(isProbableZip(currentFile))
      {
      zipFileList.add(currentFile);
      }
    }
  }

private boolean isProbableFile(File file, String extension)
  {
  return file.getName().toLowerCase().endsWith(extension);
  }

private boolean isProbableZip(File file)
  {
  return file.getName().toLowerCase().endsWith(".zip");
  }




}
