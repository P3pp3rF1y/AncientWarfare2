package shadowmage.meim.client.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import shadowmage.ancient_framework.client.model.ModelBaseAW;
import shadowmage.ancient_framework.client.model.ModelPiece;
import shadowmage.ancient_framework.client.model.PrimitiveBox;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.utils.StringTools;
import shadowmage.ancient_framework.common.utils.Trig;

public class ModelLoader
{

public ModelBaseAW loadModel(File file)
  {  
  FileInputStream fis = null;
  try
    {
    fis = new FileInputStream(file);
    } 
  catch (FileNotFoundException e)
    {      
    e.printStackTrace();
    return null;
    }
  Scanner scan = new Scanner(fis);
  ArrayList<String> lines = new ArrayList<String>();
  while(scan.hasNext())
    {
    lines.add(scan.next());
    }
  scan.close();
  try
    {
    fis.close();
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  if(file.getName().endsWith(".mmf"))
    {
    return this.parseOldModelLines(lines);    
    }
  else
    {
    ModelBaseAW model = new ModelBaseAW();
    model.parseFromLines(lines);
    return model;    
    }
  }

private ModelBaseAW parseOldModelLines(List<String> lines)
  {
  float importScale = 0.0625f;
  ModelBaseAW model = new ModelBaseAW();
  HashMap<String, Integer> txMap = new HashMap<String, Integer>();
  HashMap<String, Integer> tyMap = new HashMap<String, Integer>();
  Iterator<String> it = lines.iterator();
  String line;
  String split[];
  while(it.hasNext())
    {
    line = it.next();
    if(line.toLowerCase().startsWith("part="))
      {
      split = StringTools.safeParseStringArray("=", line);
      String name = split[0];
      String parent = split[1];
      float x = StringTools.safeParseFloat(split[2]) * importScale;
      float y = StringTools.safeParseFloat(split[3]) * importScale;
      float z = StringTools.safeParseFloat(split[4]) * importScale;
      float rx = StringTools.safeParseFloat(split[5]) * Trig.TODEGREES;
      float ry = StringTools.safeParseFloat(split[6]) * Trig.TODEGREES;
      float rz = StringTools.safeParseFloat(split[7]) * Trig.TODEGREES;
      int tx = StringTools.safeParseInt(split[8]);
      int ty = StringTools.safeParseInt(split[9]);
      int tw = (int)StringTools.safeParseFloat(split[10]);
      int th = (int)StringTools.safeParseFloat(split[11]);
      model.setTextureSize(tw, th);      
      ModelPiece piece = new ModelPiece(model, name, -x, -y, -z, rx, ry, rz, model.getPiece(parent));
      model.addPiece(piece);
      if(piece.getParent()==null)
        {
        piece.setPosition(piece.x(), piece.y()+1, piece.z());//TODO find the proper Y offset needed...NFC
        }
      txMap.put(name, tx);
      tyMap.put(name, ty);
      }    
    else if(line.toLowerCase().startsWith("box="))
      {
      split = StringTools.safeParseStringArray("=", line);
      String name = split[0];      
      float x = StringTools.safeParseFloat(split[1]) * importScale;
      float y = StringTools.safeParseFloat(split[2]) * importScale;
      float z = StringTools.safeParseFloat(split[3]) * importScale;  
      float bw = StringTools.safeParseInt(split[4]) * importScale;
      float bh = StringTools.safeParseInt(split[5]) * importScale;
      float bl = StringTools.safeParseInt(split[6]) * importScale; 
      ModelPiece piece = model.getPiece(name);
      int tx = txMap.get(name);
      int ty = tyMap.get(name);
      if(piece==null){continue;}
      PrimitiveBox box = new PrimitiveBox(piece);
      
      box.setBounds(-x - bw, -y - bh , -z - bl, bw, bh, bl);
      box.setOrigin(0, 0, 0);
      box.setRotation(0, 0, 0);
      
      box.setTx(tx);
      box.setTy(ty);      
      piece.addPrimitive(box);
      }
    }
  return model;
  }

public void saveModel(ModelBaseAW model, File file)
  {
  try
    {    
    List<String> lines = model.getModelLines();

    if(!file.exists())
      {
      File newoutputfile = new File(file.getParent());
      newoutputfile.mkdirs();
      file.createNewFile();
      }
    FileWriter writer = new FileWriter(file);
    for(String line : lines)
      {
      writer.write(line+"\n");
      }
    writer.close();
    }
  catch (IOException e)
    {
    AWLog.logDebug("error exporting model for name: "+file.getName());
    e.printStackTrace();
    }

  }

}
