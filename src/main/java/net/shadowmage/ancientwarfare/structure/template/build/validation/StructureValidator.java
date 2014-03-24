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
package net.shadowmage.ancientwarfare.structure.template.build.validation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.structure.block.BlockDataManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateParser;
import net.shadowmage.ancientwarfare.structure.world_gen.WorldStructureGenerator;

public abstract class StructureValidator
{

public static final String PROP_WORLD_GEN = "enableWorldGen";
public static final String PROP_UNIQUE = "unique";
public static final String PROP_SURVIVAL = "survival";
public static final String PROP_PRESERVE_BLOCKS = "preserveBlocks";
public static final String PROP_SELECTION_WEIGHT = "selectionWeight";
public static final String PROP_CLUSTER_VALUE = "clusterValue";
public static final String PROP_MIN_DUPLICATE_DISTANCE = "minDuplicateDistance";
public static final String PROP_BORDER_SIZE = "borderSize";
public static final String PROP_MAX_LEVELING = "maxLeveling";
public static final String PROP_MAX_FILL = "maxFill";
public static final String PROP_BIOME_WHITE_LIST = "biomeWhiteList";
public static final String PROP_DIMENSION_WHITE_LIST = "dimensionWhiteList";
public static final String PROP_BIOME_LIST = "biomeList";
public static final String PROP_DIMENSION_LIST = "dimensionList";
public static final String PROP_BLOCK_LIST = "blockList";

public final StructureValidationType validationType;

private HashMap<String, StructureValidationProperty> properties = new HashMap<String, StructureValidationProperty>();
private boolean survival;

//private int selectionWeight;
//private int clusterValue;
//private int minDuplicateDistance;
//private boolean worldGenEnabled;
//private boolean isUnique;//should this structure generate only once?
//private boolean preserveBlocks;//should this structure preserve any existing blocks when a rule '0' is encountered?
//
//private boolean biomeWhiteList;//should treat biome list as white or blacklist?
//private Set<String> biomeList;//list of biomes for white/black list.  treated as white/black list from whitelist toggle
//
//private boolean dimensionWhiteList;//should treat dimension list as white or blacklist?
//private int[] acceptedDimensions;//list of accepted dimensions treated as white/black list from whitelist toggle
//
//private int maxLeveling;
//private int maxFill;
//
//
//private int borderSize;
//
//Set<String> validTargetBlocks;//list of accepted blocks which the structure may be built upon or filled over -- 100% of blocks directly below the structure must meet this list

protected StructureValidator(StructureValidationType validationType)
  {
  this.validationType = validationType;
  for(StructureValidationProperty property : validationType.getValidationProperties())
    {    
    this.properties.put(property.regName, property.copy());
    }
  }

/**
 * should be called from validator setup GUI when swapping between validator types,
 * to transfer any comparable settings from the old one to the new one.<br>
 * This method should be called on the NEW StructureValidator.
 * @param validator
 */
public void inheritPropertiesFrom(StructureValidator validator)
  {
  StructureValidationProperty prop;
  for(String name : this.properties.keySet())
    {
    if(validator.properties.containsKey(name))
      {
      prop = validator.properties.get(name);
      this.properties.put(name, prop.copy());
      }
    }
  }

protected void readFromLines(List<String> lines)
  {  
  }

protected void write(BufferedWriter writer) throws IOException
  {
  }


/**
 * helper method to read data from tag -- to be overriden by
 * child-classes that have additional validation data set through gui
 */
public final void readFromNBT(NBTTagCompound tag)
  {
  for(StructureValidationProperty prop : this.properties.values())
    {
    prop.readFromNBT(tag);
    }
  }

public final void writeToNBT(NBTTagCompound tag)
  {
  tag.setString("validationType", this.validationType.getName());
  for(StructureValidationProperty prop : this.properties.values())
    {
    prop.writeToNBT(tag);
    }
  }

protected void setDefaultSettings(StructureTemplate template)
  {
  /**
   * TODO
   */
//  this.validTargetBlocks.addAll(WorldStructureGenerator.defaultTargetBlocks); 
//  int size = (template.ySize-template.yOffset)/3;
//  this.borderSize = size;
//  this.maxLeveling = template.ySize-template.yOffset;
//  this.maxFill = size;
  }

/**
 * should this template be included for selection for generation? should only validate block placement, most other stuff has been checked (dimension/biome/cluster value/etc)
 */
public abstract boolean shouldIncludeForSelection(World world, int x, int y, int z, int face, StructureTemplate template);

/**
 * if template should be included for selection, get the adjusted spawn Y level from the input block position.  this adjustedY will be used for validation and generation if template is selected and validated
 */
public int getAdjustedSpawnY(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb)
  {
  return y;
  }

/**
 * if selected for placement, validate that placement. return false if placement is invalid 
 */
public abstract boolean validatePlacement(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb);

/**
 * after validation, do any necessary clearing or leveling/etc
 */
public abstract void preGeneration(World world, int x, int y, int z, int face, StructureTemplate template, StructureBB bb);

/**
 * called from StructureBuilder when constructed with world-gen settings whenever a '0' rule is detected
 * in the template
 * implementations should fill the input x,y,z with whatever block is an appropriate 'fill' for that
 * validation type -- e.g. air or water
 */
public abstract void handleClearAction(World world, int x, int y, int z, StructureTemplate template, StructureBB bb);

public static final StructureValidator parseValidator(List<String> lines)
  {
  String type = null;
  List<String> tagLines = new ArrayList<String>();
  Iterator<String> it = lines.iterator();
  String line;
  boolean unique = false, worldGen = false, biome = false, dimension = false, blocks = false, survival = false;
  int selectionWeight=1, clusterValue=1, duplicate=1, maxLeveling = 0, maxFill = 0, borderSize = 0;
  int[] dimensions = null;
  Set<String> biomes = new HashSet<String>();
  Set<String> validTargetBlocks = new HashSet<String>();
  
  while(it.hasNext() && (line=it.next())!=null)
    {   
    if(line.toLowerCase().startsWith("type=")){type = StringTools.safeParseString("=", line);}
    else if(line.toLowerCase().startsWith("unique=")){unique = StringTools.safeParseBoolean("=", line);}
    else if(line.toLowerCase().startsWith("survival=")){survival = StringTools.safeParseBoolean("=", line);}
    else if(line.toLowerCase().startsWith("worldgenenabled=")){worldGen = StringTools.safeParseBoolean("=", line);}
    else if(line.toLowerCase().startsWith("biomewhitelist=")){biome = StringTools.safeParseBoolean("=", line);}
    else if(line.toLowerCase().startsWith("dimensionwhitelist=")){dimension = StringTools.safeParseBoolean("=", line);}
    else if(line.toLowerCase().startsWith("preserveblocks=")){blocks = StringTools.safeParseBoolean("=", line);}
    else if(line.toLowerCase().startsWith("dimensionlist=")){dimensions = StringTools.safeParseIntArray("=", line);}
    else if(line.toLowerCase().startsWith("biomelist=")){StringTools.safeParseStringsToSet(biomes, "=", line, true);}
    else if(line.toLowerCase().startsWith("selectionweight=")){selectionWeight = StringTools.safeParseInt("=", line);}
    else if(line.toLowerCase().startsWith("clustervalue=")){clusterValue = StringTools.safeParseInt("=", line);}
    else if(line.toLowerCase().startsWith("minduplicatedistance=")){duplicate = StringTools.safeParseInt("=", line);}
    if(line.toLowerCase().startsWith("leveling=")){maxLeveling = StringTools.safeParseInt("=", line);}
    else if(line.toLowerCase().startsWith("fill=")){maxFill = StringTools.safeParseInt("=", line);}
    else if(line.toLowerCase().startsWith("border=")){borderSize = StringTools.safeParseInt("=", line);}   
    else if(line.toLowerCase().startsWith("validtargetblocks=")){StringTools.safeParseStringsToSet(validTargetBlocks, "=", line, false);}
    else if(line.toLowerCase().startsWith("data:"))
      {
      tagLines.add(line);
      while(it.hasNext() && (line=it.next())!=null)
        {
        tagLines.add(line);
        if(line.toLowerCase().startsWith(":enddata"))
          {
          break;
          }
        }
      }
    TemplateParser.lineNumber++;
    }
  StructureValidationType validatorType = StructureValidationType.getTypeFromName(type);
  StructureValidator validator;
  if(validatorType==null)
    {
    validator = StructureValidationType.GROUND.getValidator();
    }
  else
    {
    validator = validatorType.getValidator();
    validator.readFromLines(tagLines);
    }
  
  validator.setProperty(PROP_DIMENSION_WHITE_LIST, dimension);
  validator.setProperty(PROP_DIMENSION_LIST, dimensions);
  validator.setProperty(PROP_BIOME_WHITE_LIST, biome);
  validator.setProperty(PROP_BIOME_LIST, biomes);
  validator.setProperty(PROP_WORLD_GEN, worldGen);
  validator.setProperty(PROP_SURVIVAL, survival);
  validator.setProperty(PROP_UNIQUE, unique);
  validator.setProperty(PROP_PRESERVE_BLOCKS, blocks);
  validator.setProperty(PROP_CLUSTER_VALUE, clusterValue);
  validator.setProperty(PROP_SELECTION_WEIGHT, selectionWeight);
  validator.setProperty(PROP_MIN_DUPLICATE_DISTANCE, duplicate);
  validator.setProperty(PROP_MAX_FILL, maxFill);
  validator.setProperty(PROP_MAX_LEVELING, maxLeveling);
  validator.setProperty(PROP_BORDER_SIZE, borderSize);
  validator.setProperty(PROP_BLOCK_LIST, validTargetBlocks); 
  
  return validator;
  }

public static final void writeValidator(BufferedWriter out, StructureValidator validator) throws IOException
  {
  out.write("type="+validator.validationType.getName());  
  out.newLine();
  out.write("survival="+validator.survival);  
  out.newLine();
  out.write("worldGenEnabled="+validator.isWorldGenEnabled());  
  out.newLine();
  out.write("unique="+validator.isUnique());
  out.newLine();
  out.write("preserveBlocks="+validator.isPreserveBlocks());
  out.newLine();
  out.write("selectionWeight="+validator.getSelectionWeight());
  out.newLine();
  out.write("clusterValue="+validator.getClusterValue());
  out.newLine();
  out.write("minDuplicateDistance="+validator.getMinDuplicateDistance());
  out.newLine();
  out.write("dimensionWhiteList="+validator.isDimensionWhiteList());
  out.newLine();
  out.write("dimensionList="+StringTools.getCSVStringForArray(validator.getAcceptedDimensions()));
  out.newLine();  
  out.write("biomeWhiteList="+validator.isBiomeWhiteList());
  out.newLine();
  out.write("biomeList="+StringTools.getCSVValueFor(validator.getBiomeList().toArray(new String[validator.getBiomeList().size()])));
  out.newLine();
  out.write("leveling="+validator.getMaxLeveling());
  out.newLine();
  out.write("fill="+validator.getMaxFill());
  out.newLine();
  out.write("border="+validator.getBorderSize());
  out.newLine();
  out.write("validTargetBlocks="+StringTools.getCSVfor(validator.getTargetBlocks()));
  out.newLine(); 
  out.write("data:");
  out.newLine();
  validator.write(out);
  out.write(":enddata");
  out.newLine();
  }

public final StructureValidator setDefaults(StructureTemplate template)
  {
  setDefaultSettings(template);
  return this;
  }

public final StructureValidationProperty getProperty(String name)
  {
  return properties.get(name);
  }

public final void setProperty(String name, Object value)
  {
  if(properties.containsKey(name))
    {
    properties.get(name).setValue(value);
    }
  else
    {
    throw new IllegalArgumentException("Validation properties does not contain key for: "+name);
    }
  }

public final int getSelectionWeight()
  {
  return properties.get(PROP_SELECTION_WEIGHT).getDataInt();
  }

public final int getClusterValue()
  {
  return properties.get(PROP_CLUSTER_VALUE).getDataInt();
  }

public final boolean isWorldGenEnabled()
  {
  return properties.get(PROP_WORLD_GEN).getDataBoolean();
  }

public final boolean isPreserveBlocks()
  {
  return properties.get(PROP_PRESERVE_BLOCKS).getDataBoolean();
  }

public final boolean isBiomeWhiteList()
  {
  return properties.get(PROP_BIOME_WHITE_LIST).getDataBoolean();
  }

public final boolean isUnique()
  {
  return properties.get(PROP_UNIQUE).getDataBoolean();
  }

public final boolean isDimensionWhiteList() 
  {
  return properties.get(PROP_DIMENSION_WHITE_LIST).getDataBoolean();
	}

public final int[] getAcceptedDimensions()
  {
  return properties.get(PROP_DIMENSION_LIST).getDataIntArray();
  }

public final int getMinDuplicateDistance()
  {
  return properties.get(PROP_MIN_DUPLICATE_DISTANCE).getDataInt();
  }

public final void setTargetBlocks(Collection<String> targetBlocks)
  {
  Set<String> blocks = new HashSet<String>();
  blocks.addAll(targetBlocks);
  properties.get(PROP_BLOCK_LIST).setValue(blocks);  
  }

public final void setBiomeList(Collection<String> biomes)
  {
  Set<String> blocks = new HashSet<String>();
  blocks.addAll(biomes);
  properties.get(PROP_BIOME_LIST).setValue(blocks); 
  }

public Set<String> getTargetBlocks()
  {
  return properties.get(PROP_BLOCK_LIST).getDataStringSet();
  }

public Set<String> getBiomeList()
  {
  return properties.get(PROP_BIOME_LIST).getDataStringSet();
  }

public int getMaxFill()
  {
  return properties.get(PROP_MAX_FILL).getDataInt();
  }

public int getMaxLeveling()
  {
  return properties.get(PROP_MAX_LEVELING).getDataInt();
  }

public int getBorderSize()
  {
  return properties.get(PROP_BORDER_SIZE).getDataInt();
  }

//************************************************ UTILITY METHODS *************************************************//
protected boolean validateBorderBlocks(World world, StructureTemplate template, StructureBB bb, int minY, int maxY, boolean skipWater)
  {
  Set<String> validTargetBlocks = getTargetBlocks();
  int bx, bz;
  int borderSize = getBorderSize();
  for(bx = bb.min.x-borderSize; bx<=bb.max.x+borderSize; bx++)
    {
    bz = bb.min.z-borderSize;
    if(!validateBlockHeightAndType(world, bx, bz, minY, maxY, skipWater, validTargetBlocks))
      {
      return false;
      }        
    bz = bb.max.z+borderSize;
    if(!validateBlockHeightAndType(world, bx, bz, minY, maxY, skipWater, validTargetBlocks))
      {
      return false;
      }        
    }
  for(bz = bb.min.z-borderSize+1; bz<=bb.max.z+borderSize-1; bz++)
    {
    bx = bb.min.x-borderSize;
    if(!validateBlockHeightAndType(world, bx, bz, minY, maxY, skipWater, validTargetBlocks))
      {
      return false;
      }        
    bx = bb.max.x+borderSize;
    if(!validateBlockHeightAndType(world, bx, bz, minY, maxY, skipWater, validTargetBlocks))
      {
      return false;
      }        
    }
  return true;
  }

/**
 * validates both top block height and block type for the input position and settings
 */
protected boolean validateBlockHeightAndType(World world, int x, int z, int min, int max, boolean skipWater, Set<String> validBlocks)
  {
  return validateBlockType(world, x, validateBlockHeight(world, x, z, min, max, skipWater), z, validBlocks);
  }

/**
 * validates top block height at X, Z is >=  min and <= max (inclusive)
 * returns topFoundY or -1 if not within range
 */
protected int validateBlockHeight(World world, int x, int z, int minimumAcceptableY, int maximumAcceptableY, boolean skipWater)
  {
  int topFilledY = WorldStructureGenerator.getTargetY(world, x, z, skipWater);
  if(topFilledY < minimumAcceptableY || topFilledY > maximumAcceptableY)
    {
    AWLog.logDebug("rejected for leveling or depth test. foundY: "+topFilledY + " min: "+minimumAcceptableY +" max:"+maximumAcceptableY +  " at: "+x+","+topFilledY+","+z);
    return -1;
    }
  return topFilledY;
  }

/**
 * validates the target block at x,y,z is one of the input valid blocks
 */
protected boolean validateBlockType(World world, int x, int y, int z, Set<String> validBlocks)
  {
  if(y < 0 || y>=256)
    {
    return false;
    }
  Block block = world.getBlock(x, y, z);
  if(block==null)
    {   
    AWLog.logDebug("rejected for non-matching block: air" + " at: "+x+","+y+","+z);
    return false;
    }
  if(!validBlocks.contains(BlockDataManager.instance().getNameForBlock(block)))
    {
    AWLog.logDebug("rejected for non-matching block: "+BlockDataManager.instance().getNameForBlock(block) + " at: "+x+","+y+","+z);
    return false;
    }
  return true;  
  }

/**
 * return the lowest acceptable Y level for a filled block
 * for the input template and BB
 */
protected int getMinY(StructureTemplate template, StructureBB bb)
  {
  int minY = bb.min.y - getMaxFill() - 1;
  if(getBorderSize()>0)
    {
    minY+=template.yOffset;
    }
  return minY;
  }

/**
 * return the highest acceptable Y level for a filled block
 * for the input template and BB
 */
protected int getMaxY(StructureTemplate template, StructureBB bb)
  {
  return bb.min.y + template.yOffset + getMaxLeveling();
  }

protected int getMinFillY(StructureTemplate template, StructureBB bb)
  {
  return getMinY(template, bb);
  }

protected int getMaxFillY(StructureTemplate template, StructureBB bb)
  {
  return getMinY(template, bb) + getMaxFill();
  }

protected int getMinLevelingY(StructureTemplate template, StructureBB bb)
  {
  return bb.min.y + template.yOffset;
  }

protected int getMaxLevelingY(StructureTemplate template, StructureBB bb)
  {
  return bb.min.y + template.yOffset + getMaxLeveling();
  }

protected void borderLeveling(World world, int x, int z, StructureTemplate template, StructureBB bb)
  {
  if(getMaxLeveling()<=0){return;}
  int topFilledY = WorldStructureGenerator.getTargetY(world, x, z, true);
  int step = WorldStructureGenerator.getStepNumber(x, z, bb.min.x, bb.max.x, bb.min.z, bb.max.z);  
  for(int y = bb.min.y + template.yOffset + step; y <= topFilledY ; y++)
    {
    handleClearAction(world, x, y, z, template, bb);
    }
  BiomeGenBase biome = world.getBiomeGenForCoords(x, z);  
  Block fillBlock = Blocks.grass;
  if(biome!=null && biome.topBlock!=null)
    {
    fillBlock = biome.topBlock;
    }
  int y = bb.min.y + template.yOffset + step - 1;
  Block block = world.getBlock(x, y, z);
  if(block!=null && block!= Blocks.flowing_water && block!=Blocks.water && !WorldStructureGenerator.skippableWorldGenBlocks.contains(BlockDataManager.instance().getNameForBlock(block)))
    {
    world.setBlock(x, y, z, fillBlock);
    }  
  }

protected void borderFill(World world, int x, int z, StructureTemplate template, StructureBB bb)
  {
  if(getMaxFill()<=0){return;}
  int maxFillY = getMaxFillY(template, bb);  
  int step = WorldStructureGenerator.getStepNumber(x, z, bb.min.x, bb.max.x, bb.min.z, bb.max.z);  
  maxFillY -= step;
  Block block;
  BiomeGenBase biome = world.getBiomeGenForCoords(x, z);  
  Block fillBlockID = Blocks.grass;
  if(biome!=null && biome.topBlock!=null)
    {
    fillBlockID = biome.topBlock;
    }
  for(int y = maxFillY; y>1; y--)
    {
    block = world.getBlock(x, y, z);
    if(block==null || WorldStructureGenerator.skippableWorldGenBlocks.contains(BlockDataManager.instance().getNameForBlock(block)) || (block==Blocks.water || block==Blocks.flowing_water))
      {
      world.setBlock(x, y, z, fillBlockID);
      }
    }
  }

protected void underFill(World world, int x, int z, StructureTemplate template, StructureBB bb)
  {
  int topFilledY = WorldStructureGenerator.getTargetY(world, x, z, true);
  BiomeGenBase biome = world.getBiomeGenForCoords(x, z);  
  Block fillBlockID = Blocks.grass;
  if(biome!=null && biome.topBlock!=null)
    {
    fillBlockID = biome.topBlock;
    }
  for(int y = topFilledY; y <= bb.min.y-1; y++)
    {
    world.setBlock(x, y, z, fillBlockID);
    }  
  }

protected void prePlacementUnderfill(World world, StructureTemplate template, StructureBB bb)
  {
  if(getMaxFill()<=0){return;}
  int bx, bz;
  for(bx = bb.min.x; bx<=bb.max.x; bx++)
    {
    for(bz = bb.min.z; bz<=bb.max.z; bz++)
      {
      underFill(world, bx, bz, template, bb);
      }
    }
  }

protected void prePlacementBorder(World world, StructureTemplate template, StructureBB bb)
  {
  int borderSize = getBorderSize();
  if(borderSize<=0){return;}
  int bx, bz;
  for(bx = bb.min.x-borderSize; bx <= bb.max.x + borderSize; bx++)
    {
    for(bz = bb.max.z+borderSize; bz>bb.max.z; bz--)
      {
      borderLeveling(world, bx, bz, template, bb);
      borderFill(world, bx, bz, template, bb);
      }
    for(bz = bb.min.z-borderSize; bz<bb.min.z; bz++)
      {
      borderLeveling(world, bx, bz, template, bb);
      borderFill(world, bx, bz, template, bb);
      }         
    }
  for(bz = bb.min.z; bz <= bb.max.z; bz++)
    {    
    for(bx = bb.min.x-borderSize; bx<bb.min.x; bx++)
      {
      borderLeveling(world, bx, bz, template, bb);
      borderFill(world, bx, bz, template, bb);
      }
    for(bx = bb.max.x+borderSize; bx>bb.max.x; bx--)
      {
      borderLeveling(world, bx, bz, template, bb);
      borderFill(world, bx, bz, template, bb);
      }
    }
  }

public Collection<StructureValidationProperty> getProperties()
  {
  return properties.values();
  }

public boolean isSurvival()
  {
  return survival;
  }

}
