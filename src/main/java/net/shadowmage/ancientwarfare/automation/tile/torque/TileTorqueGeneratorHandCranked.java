package net.shadowmage.ancientwarfare.automation.tile.torque;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.item.ItemWorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class TileTorqueGeneratorHandCranked extends TileTorqueGeneratorBase implements IWorkSite, IOwnable
{

String ownerName = "";

@Override
public void onBlockBroken()
  {
  // TODO
  }

@Override
public int getBoundsMaxWidth(){return 0;}

@Override
public int getBoundsMaxHeight(){return 0;}

@Override
public boolean canOutput(ForgeDirection towards)
  {
  return towards==orientation;
  }

@Override
public EnumSet<WorksiteUpgrade> getUpgrades(){return EnumSet.noneOf(WorksiteUpgrade.class);}// TODO Auto-generated method stub

@Override
public EnumSet<WorksiteUpgrade> getValidUpgrades(){return EnumSet.noneOf(WorksiteUpgrade.class);}// TODO Auto-generated method stub

@Override
public void addUpgrade(WorksiteUpgrade upgrade)
  {
  // TODO Auto-generated method stub
  }

@Override
public void removeUpgrade(WorksiteUpgrade upgrade)
  {
  // TODO Auto-generated method stub  
  }

@Override
public boolean hasWork()
  {
  return getEnergyStored()<getMaxEnergy();
  }

@Override
public void addEnergyFromWorker(IWorker worker)
  {
  storedEnergy += AWCoreStatics.energyPerWorkUnit * worker.getWorkEffectiveness(getWorkType());
  if(storedEnergy>getMaxEnergy()){storedEnergy = getMaxEnergy();}
  }

@Override
public void addEnergyFromPlayer(EntityPlayer player)
  {
  storedEnergy+=AWCoreStatics.energyPerWorkUnit;
  if(storedEnergy>getMaxEnergy()){storedEnergy=getMaxEnergy();}
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.CRAFTING;
  }

@Override
public Team getTeam()
  {
  return worldObj.getScoreboard().getPlayersTeam(ownerName);
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
public void setOwnerName(String name)
  {
  if(name==null){name = "";}
  ownerName = name;
  }

@Override
public String getOwnerName()
  {
  return ownerName;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  if(tag.hasKey("owner")){ownerName = tag.getString("owner");}
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  if(ownerName!=null){tag.setString("owner", ownerName);}
  }

}
