package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.tile.TileTorqueConduit;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilderTicked;

public class TileStructureBuilder extends TileEntity implements IWorkSite
{

protected String owningPlayer;
int maxWorkers = 2;

StructureBuilderTicked builder;
private boolean shouldRemove = false;
public boolean isStarted = false;
int workDelay = 20;

public TileStructureBuilder()
  {
  
  }

double maxEnergyStored = 1600;
double maxInput = 100;
private double storedEnergy;

@Override
public void setEnergy(double energy)
  {
  this.storedEnergy = energy;
  }

@Override
public double addEnergy(ForgeDirection from, double energy)
  {
  if(canInput(from))
    {
    if(energy+getEnergyStored()>getMaxEnergy())
      {
      energy = getMaxEnergy()-getEnergyStored();
      }
    if(energy>getMaxInput())
      {
      energy = getMaxInput();
      }
    storedEnergy+=energy;
    return energy;    
    }
  return 0;
  }

@Override
public double getMaxEnergy()
  {
  return TileTorqueConduit.maxEnergy;
  }

@Override
public double getEnergyStored()
  {
  return storedEnergy;
  }

@Override
public double getMaxInput()
  {
  return maxInput;
  }

@Override
public boolean canInput(ForgeDirection from)
  {
  return true;
  }

@Override
public boolean canUpdate()
  {
  return true;
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(worldObj.isRemote){return;}
  if(builder==null || builder.invalid || builder.isFinished())
    {
    shouldRemove = true;
    }
  if(builder.getWorld()==null){builder.setWorld(worldObj);}
  if(shouldRemove)
    {
    worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.air);
    return;
    }
  if(ModuleStatus.automationLoaded || ModuleStatus.npcsLoaded)
    {
    if(storedEnergy>=maxEnergyStored)
      {
      storedEnergy-=maxEnergyStored;
      processWork();
      }
    return;
    }
  if(workDelay>0)
    {
    workDelay--;
    }
  if(workDelay<=0)
    {
    processWork();
    workDelay=20;
    }
  }

public void processWork()
  {
  isStarted = true;
  builder.tick();
  }

/**
 * should be called immediately after the tile-entity is set into the world
 * from the ItemBlockStructureBuilder item onBlockPlaced code
 * @param builder
 */
public void setOwnerName(String name)
  {
  this.owningPlayer = name;
  }

/**
 * should be called immediately after the tile-entity is set into the world
 * from the ItemBlockStructureBuilder item onBlockPlaced code<br>
 * the passed in builder must be valid (have a valid structure), and must not
 * be null
 * @param builder
 */
public void setBuilder(StructureBuilderTicked builder)
  {
  this.builder = builder;
  }

public void onBlockBroken()
  {
  if(!worldObj.isRemote && !isStarted && builder!=null && builder.getTemplate()!=null)
    {
    isStarted = true;//to prevent further drops
    ItemStack item = new ItemStack(AWBlocks.builderBlock);
    item.setTagInfo("structureName", new NBTTagString(builder.getTemplate().name));
    }
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);  
  if(tag.hasKey("builder"))
    {
    builder = new StructureBuilderTicked();    
    builder.readFromNBT(tag.getCompoundTag("builder"));    
    }
  else
    {
    this.shouldRemove = true;
    }
  this.isStarted = tag.getBoolean("started");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  if(builder!=null)
    {
    NBTTagCompound builderTag = new NBTTagCompound();
    builder.writeToNBT(builderTag);  
    tag.setTag("builder", builderTag);    
    }
  tag.setBoolean("started", isStarted);
  }

//*******************************************WORKSITE************************************************//
@Override
public boolean hasWork()
  {
  return true;
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.CONSTRUCTION;
  }

@Override
public final Team getTeam()
  {  
  if(owningPlayer!=null)
    {
    worldObj.getScoreboard().getPlayersTeam(owningPlayer);
    }
  return null;
  }

@Override
public BlockPosition getWorkBoundsMin()
  {
  return null;
  }

@Override
public BlockPosition getWorkBoundsMax()
  {
  return null;
  }

@Override
public boolean hasWorkBounds()
  {
  return false;
  }

@Override
public void addEnergyFromWorker(IWorker worker)
  {
  storedEnergy += AWAutomationStatics.energyPerWorkUnit * worker.getWorkEffectiveness();
  }

}
