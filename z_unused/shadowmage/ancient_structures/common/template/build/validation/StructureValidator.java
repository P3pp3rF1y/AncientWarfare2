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
package shadowmage.ancient_structures.common.template.build.validation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.utils.StringTools;
import shadowmage.ancient_structures.common.manager.BlockDataManager;
import shadowmage.ancient_structures.common.template.StructureTemplate;
import shadowmage.ancient_structures.common.template.build.StructureBB;
import shadowmage.ancient_structures.common.template.load.TemplateParser;
import shadowmage.ancient_structures.common.world_gen.WorldStructureGenerator;

public abstract class StructureValidator
{

public final StructureValidationType validationType;
private int selectionWeight;
private int clusterValue;
private int minDuplicateDistance;
private boolean worldGenEnabled;
private boolean isUnique;//should this structure generate only once?
private boolean preserveBlocks;//should this structure preserve any existing blocks when a rule '0' is encountered?

private boolean biomeWhiteList;//should treat biome list as white or blacklist?
private Set<String> biomeList;//list of biomes for white/black list.  treated as white/black list from whitelist toggle

private boolean dimensionWhiteList;//should treat dimension list as white or blacklist?
private int[] acceptedDimensions;//list of accepted dimensions treated as white/black list from whitelist toggle

int maxLeveling;
int maxFill;
int borderSize;

Set<String> validTargetBlocks;//list of accepted blocks which the structure may be built upon or filled over -- 100% of blocks directly below the structure must meet this list

protected StructureValidator(StructureValidationType validationType)
  {
  this.validationType = validationType;
  selectionWeight = 1;
  clusterValue = 1;  
  minDuplicateDistance = 8;
  biomeList = new HashSet<String>();
  validTargetBlocks = new HashSet<String>();
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
public void readFromTag(NBTTagCompound tag)
  {
  worldGenEnabled = tag.getBoolean("enableWorldGen");
  isUnique = tag.getBoolean("unique");
  preserveBlocks = tag.getBoolean("preserveBlocks");
  selectionWeight = tag.getInteger("selectionWeight");
  clusterValue = tag.getInteger("clusterValue");
  minDuplicateDistance = tag.getInteger("minDuplicateDistance");
  borderSize = tag.getInteger("borderSize");
  maxLeveling = tag.getInteger("maxLeveling");
  maxFill = tag.getInteger("maxFill");  
  biomeWhiteList = tag.getBoolean("biomeWhiteList");
  dimensionWhiteList = tag.getBoolean("dimensionWhiteList");
  
  if(tag.hasKey("biomeList"))
    {
    ArrayList<String> biomes = new ArrayList<String>();
    NBTTagList biomeList = tag.getTagList("biomeList");
    NBTTagString biomeTag;
    for(int i = 0; i <biomeList.tagCount(); i++)
      {
      biomeTag = (NBTTagString) biomeList.tagAt(i);      
      biomes.add(biomeTag.data);
      }
    this.setBiomeList(biomes);
    }
  if(tag.hasKey("blockList"))
    {
    ArrayList<String> blocks = new ArrayList<String>();
    NBTTagList blockList = tag.getTagList("blockList");
    NBTTagString blockTag;
    for(int i = 0; i <blockList.tagCount(); i++)
      {
      blockTag = (NBTTagString) blockList.tagAt(i);      
      blocks.add(blockTag.data);
      }
    this.setTargetBlocks(blocks);
    }
  
  if(tag.hasKey("dimensions"))
    {
    this.acceptedDimensions = tag.getIntArray("dimensions");
    }
  }

protected void setDefaultSettings(StructureTemplate template)
  {
  this.validTargetBlocks.addAll(WorldStructureGenerator.defaultTargetBlocks); 
  int size = (template.ySize-template.yOffset)/3;
  this.borderSize = size;
  this.maxLeveling = template.ySize-template.yOffset;
  this.maxFill = size;
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
  boolean unique = false, worldGen = false, biome = false, dimension = false, blocks = false;
  int selectionWeight=1, clusterValue=1, duplicate=1, maxLeveling = 0, maxFill = 0, borderSize = 0;
  int[] dimensions = null;
  Set<String> biomes = new HashSet<String>();
  Set<String> validTargetBlocks = new HashSet<String>();
  
  while(it.hasNext() && (line=it.next())!=null)
    {   
    if(line.toLowerCase().startsWith("type=")){type = StringTools.safeParseString("=", line);}
    else if(line.toLowerCase().startsWith("unique=")){unique = StringTools.safeParseBoolean("=", line);}
    else if(line.toLowerCase().startsWith("worldgenenabled=")){worldGen = StringTools.safeParseBoolean("=", line);}
    else if(line.toLowerCase().startsWith("biomewhitelist=")){biome = StringTools.safeParseBoolean("=", line);}
    else if(line.toLowerCase().startsWith("dimensionwhitelise=")){dimension = StringTools.safeParseBoolean("=", line);}
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
  validator.dimensionWhiteList = dimension;
  validator.acceptedDimensions = dimensions;
  validator.biomeWhiteList = biome;
  validator.biomeList = biomes;
  validator.worldGenEnabled = worldGen; 
  validator.isUnique = unique;
  validator.preserveBlocks = blocks;
  validator.clusterValue = clusterValue;
  validator.selectionWeight = selectionWeight;
  validator.minDuplicateDistance = duplicate;  
  
  validator.maxFill = maxFill;
  validator.maxLeveling = maxLeveling;      
  validator.borderSize = borderSize;
  validator.validTargetBlocks = validTargetBlocks;
  return validator;
  }

public static final void writeValidator(BufferedWriter out, StructureValidator validator) throws IOException
  {
  out.write("type="+validator.validationType.getName());  
  out.newLine();
  out.write("worldGenEnabled="+validator.worldGenEnabled);  
  out.newLine();
  out.write("unique="+validator.isUnique);
  out.newLine();
  out.write("preserveBlocks="+validator.preserveBlocks);
  out.newLine();
  out.write("selectionWeight="+validator.selectionWeight);
  out.newLine();
  out.write("clusterValue="+validator.clusterValue);
  out.newLine();
  out.write("minDuplicateDistance="+validator.minDuplicateDistance);
  out.newLine();
  out.write("dimensionWhiteList="+validator.dimensionWhiteList);
  out.newLine();
  out.write("dimensionList="+StringTools.getCSVStringForArray(validator.acceptedDimensions));
  out.newLine();  
  out.write("biomeWhiteList="+validator.biomeWhiteList);
  out.newLine();
  out.write("biomeList="+StringTools.getCSVValueFor(validator.biomeList.toArray(new String[validator.biomeList.size()])));
  out.newLine();
  out.write("leveling="+validator.maxLeveling);
  out.newLine();
  out.write("fill="+validator.maxFill);
  out.newLine();
  out.write("border="+validator.borderSize);
  out.newLine();
  out.write("validTargetBlocks="+StringTools.getCSVfor(validator.validTargetBlocks));
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

public final int getSelectionWeight()
  {
  return selectionWeight;
  }

public final int getClusterValue()
  {
  return clusterValue;
  }

public final boolean isWorldGenEnabled()
  {
  return worldGenEnabled;
  }

public final Set<String> getBiomeList()
  {
  return biomeList;
  }

public final boolean isPreserveBlocks()
  {
  return preserveBlocks;
  }

public final boolean isBiomeWhiteList()
  {
  return biomeWhiteList;
  }

public final boolean isUnique()
  {
  return isUnique;
  }

public final boolean isDimensionWhiteList() 
  {
	return dimensionWhiteList;
	}

public final int[] getAcceptedDimensions()
  {
  return acceptedDimensions;
  }

public final int getMinDuplicateDistance()
  {
  return minDuplicateDistance;
  }

public final void setTargetBlocks(Collection<String> targetBlocks)
  {
  this.validTargetBlocks.clear();
  this.validTargetBlocks.addAll(targetBlocks);
  }

public final void setBiomeList(Collection<String> biomes)
  {
  this.biomeList.clear();
  this.biomeList.addAll(biomes);
  }

//************************************************ UTILITY METHODS *************************************************//
protected boolean validateBorderBlocks(World world, StructureTemplate template, StructureBB bb, int minY, int maxY, boolean skipWater)
  {
  int bx, bz;
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
  Block block = Block.blocksList[world.getBlockId(x, y, z)];
  if(block==null)
    {   
    AWLog.logDebug("rejected for non-matching block: air" + " at: "+x+","+y+","+z);
    return false;
    }
  if(!validBlocks.contains(BlockDataManager.getBlockName(block)))
    {
    AWLog.logDebug("rejected for non-matching block: "+BlockDataManager.getBlockName(block) + " at: "+x+","+y+","+z);
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
  int minY = bb.min.y - maxFill - 1;
  if(borderSize>0)
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
  return bb.min.y + template.yOffset + maxLeveling;
  }

protected int getMinFillY(StructureTemplate template, StructureBB bb)
  {
  return getMinY(template, bb);
  }

protected int getMaxFillY(StructureTemplate template, StructureBB bb)
  {
  return getMinY(template, bb) + maxFill;
  }

protected int getMinLevelingY(StructureTemplate template, StructureBB bb)
  {
  return bb.min.y + template.yOffset;
  }

protected int getMaxLevelingY(StructureTemplate template, StructureBB bb)
  {
  return bb.min.y + template.yOffset + maxLeveling;
  }

protected void borderLeveling(World world, int x, int z, StructureTemplate template, StructureBB bb)
  {
  if(maxLeveling<=0){return;}
  int topFilledY = WorldStructureGenerator.getTargetY(world, x, z, true);
  int step = WorldStructureGenerator.getStepNumber(x, z, bb.min.x, bb.max.x, bb.min.z, bb.max.z);  
  for(int y = bb.min.y + template.yOffset + step; y <= topFilledY ; y++)
    {
    handleClearAction(world, x, y, z, template, bb);
    }
  BiomeGenBase biome = world.getBiomeGenForCoords(x, z);  
  int fillBlockID = Block.grass.blockID;
  if(biome!=null && biome.topBlock>=1)
    {
    fillBlockID = biome.topBlock;
    }
  int y = bb.min.y + template.yOffset + step - 1;
  Block block = Block.blocksList[world.getBlockId(x, y, z)];
  if(block!=null && block!= Block.waterMoving && block!=Block.waterStill && !WorldStructureGenerator.skippableWorldGenBlocks.contains(BlockDataManager.getBlockName(block)))
    {
    world.setBlock(x, y, z, fillBlockID);
    }  
  }

protected void borderFill(World world, int x, int z, StructureTemplate template, StructureBB bb)
  {
  if(maxFill<=0){return;}
  int maxFillY = getMaxFillY(template, bb);  
  int step = WorldStructureGenerator.getStepNumber(x, z, bb.min.x, bb.max.x, bb.min.z, bb.max.z);  
  maxFillY -= step;
  Block block;
  BiomeGenBase biome = world.getBiomeGenForCoords(x, z);  
  int fillBlockID = Block.grass.blockID;
  if(biome!=null && biome.topBlock>=1)
    {
    fillBlockID = biome.topBlock;
    }
  for(int y = maxFillY; y>1; y--)
    {
    block = Block.blocksList[world.getBlockId(x, y, z)];
    if(block==null || WorldStructureGenerator.skippableWorldGenBlocks.contains(BlockDataManager.getBlockName(block)) || (block==Block.waterStill || block==Block.waterMoving))
      {
      world.setBlock(x, y, z, fillBlockID);
      }
    }
  }

protected void underFill(World world, int x, int z, StructureTemplate template, StructureBB bb)
  {
  int topFilledY = WorldStructureGenerator.getTargetY(world, x, z, true);
  BiomeGenBase biome = world.getBiomeGenForCoords(x, z);  
  int fillBlockID = Block.grass.blockID;
  if(biome!=null && biome.topBlock>=1)
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
  if(maxFill<=0){return;}
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
}
