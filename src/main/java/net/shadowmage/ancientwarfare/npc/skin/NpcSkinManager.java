package net.shadowmage.ancientwarfare.npc.skin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;

public class NpcSkinManager
{

public static final NpcSkinManager INSTANCE = new NpcSkinManager();

/**
 * server-side skins
 */
private static final String skinMainPath = "config/AWConfig/npc/skins/";

/**
 * client-side skins
 */
private static final String skinCachePath = "config/AWConfig/npc/client_skin_cache/";

private List<File> probableZipFiles = new ArrayList<File>();



public ResourceLocation getTextureFor(NpcBase npc)
  {
  return null;
  }

public void loadSkinPacks(String basePath)
  {
  String path = basePath+skinMainPath;
  // scan through skins directory, loading any .zip files that are skin-packs 
  // (they will contain a meta-data file along with the skin images)
  
  //locate zip files
  recursiveScan(new File(path), probableZipFiles);
  
  //loop through files locating any skin-packs
  loadSkinPackZips();
  probableZipFiles.clear();
  }

private void recursiveScan(File directory, List<File> zipFileList)
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
      recursiveScan(currentFile, zipFileList);
      }
    else if(isProbableZip(currentFile))
      {
      zipFileList.add(currentFile);
      }
    }
  loadSkinPackZips();
  }

private int loadSkinPackZips()
  {
  ZipFile z;
  ZipEntry entry;
  Enumeration<? extends ZipEntry> zipEntries;
  int parsed = 0;
  int totalParsed = 0;
  for(File f : this.probableZipFiles)
    {
    parsed = 0;
    AWLog.logDebug("Parsing templates from zip file: "+f.getName());
    try
      {
      z = new ZipFile(f);
      zipEntries = z.entries();
      while(zipEntries.hasMoreElements())
        {
        entry = zipEntries.nextElement();
        if(entry.isDirectory()){continue;}//TODO how to handle subfolders in a zip-file?
        if(entry.getName().toLowerCase().endsWith(".png"))
          {
          //add to image file cache to load after finding the meta-data file
//          loadStructureImage(entry.getName(), z.getInputStream(entry));
          continue;
          }
        else if(!entry.getName().toLowerCase().endsWith("."+AWStructureStatics.templateExtension))
          {
          //TODO change this to check for the metadata file
          continue;
          }
//        AWLog.logDebug("Loading template ("+f.getName()+"): "+entry.getName());
//        template = loadTemplateFromZip(entry, z.getInputStream(entry));
//        if(template!=null)
//          {
//          StructureTemplateManager.instance().addTemplate(template);
//          loadedStructureNames.add(template.name);
//          parsed++;
//          }
        }
      z.close();
      } 
    catch (ZipException e)
      {
      e.printStackTrace();
      } 
    catch (IOException e)
      {
      e.printStackTrace();
      }
    AWLog.logDebug("Parsed total of "+parsed+" template(s) from zip file: "+f.getName());
    totalParsed+=parsed;
    }
  return totalParsed;
  }

//private StructureTemplate loadTemplateFromZip(ZipEntry entry, InputStream is)
//  {
//  InputStreamReader isr = new InputStreamReader(is);
//  BufferedReader reader = new BufferedReader(isr);
//  List<String> lines = new ArrayList<String>();
//  String line;
//  StructureTemplate template = null;
//  try
//    {
//    while((line = reader.readLine())!=null)
//      {
//      lines.add(line);
//      }
//    template = TemplateParser.instance().parseTemplate(entry.getName(), lines);
//    } 
//  catch (IOException e1)
//    {
//    e1.printStackTrace();
//    template = null;
//    }  
//  try
//    {
//    reader.close();
//    } 
//  catch (IOException e)
//    {
//    e.printStackTrace();
//    }
//  try
//    {
//    isr.close();
//    } 
//  catch (IOException e)
//    {
//    e.printStackTrace();
//    }
//  try
//    {
//    is.close();
//    }
//  catch (IOException e)
//    {
//    e.printStackTrace();
//    }
//  return template;
//  }

private boolean isProbableZip(File file)
  {
  return file.getName().toLowerCase().endsWith(".zip");
  }

private boolean isProbableImage(File file)
  {
  return file.getName().toLowerCase().endsWith(".png");
  }

}
