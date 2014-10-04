package net.shadowmage.ancientwarfare.automation.tile.torque;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class TileTorqueGeneratorHandCranked extends TileTorqueGeneratorBase implements IWorkSite, IOwnable
{

String ownerName = "";
TorqueCell outputCell, inputCell;

/**
 * client side this == 0.0 -> 1.0
 */
double clientEnergyState, clientInputEnergy;

/**
 * server side this == 0 -> 100 (integer percent)
 * client side this == 0.0 -> 1.0 (actual percent)
 */
double clientDestEnergyState, clientInputDestEnergy;

/**
 * used client side for rendering
 */
double rotation, prevRotation, inputRotation, prevInputRotation;

public TileTorqueGeneratorHandCranked()
  {
  outputCell = new TorqueCell(0, 32, 32, 1.f);
  inputCell = new TorqueCell(32, 0, 150, 1.f);
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(!worldObj.isRemote)
    { 
    torqueIn = getTotalTorque() - prevEnergy;
    balancePower();
    torqueOut = transferPowerTo(getPrimaryFacing());
    prevEnergy = getTotalTorque();
    serverNetworkUpdate();    
    }
  else
    {
    clientNetworkUpdate();
    updateRotation();
    }
  }

protected void balancePower()
  {
  double trans = Math.min(2.d, outputCell.getMaxEnergy()-outputCell.getEnergy());
  trans = Math.min(trans, inputCell.getEnergy());
  inputCell.setEnergy(inputCell.getEnergy()-trans);
  outputCell.setEnergy(outputCell.getEnergy()+trans);
  }

@Override
protected void serverNetworkSynch()
  {
  int percent1 = (int)(outputCell.getPercentFull()*100.d);
  int percent2 = (int)(torqueOut / outputCell.getMaxOutput());
  percent1 = Math.max(percent1, percent2);
  if(percent1 != clientDestEnergyState)
    {
    clientDestEnergyState = percent1;
    sendSideRotation(getPrimaryFacing(), percent1);    
    }
  percent1 = (int)(inputCell.getPercentFull()*100d);
  if(percent1!=clientInputDestEnergy)
    {
    clientInputDestEnergy = percent1;
    sendSideRotation(ForgeDirection.UP, percent1);
    }  
  }

@Override
protected void updateRotation()
  {
  prevRotation = rotation;
  if(clientEnergyState > 0)
    {
    double r = AWAutomationStatics.low_rpt * clientEnergyState;
    rotation += r;
    }
  prevInputRotation = inputRotation;
  if(clientInputEnergy > 0)
    {
    double r = AWAutomationStatics.low_rpt * clientInputEnergy;
    inputRotation += r;
    }  
  }

@Override
protected void clientNetworkUpdate()
  {
  if(clientEnergyState != clientDestEnergyState || clientInputEnergy != clientInputDestEnergy)
    {
    if(networkUpdateTicks>=0)
      {
      clientEnergyState += (clientDestEnergyState - clientEnergyState) / ((double)networkUpdateTicks+1.d);
      clientInputEnergy += (clientInputDestEnergy - clientInputEnergy) / ((double)networkUpdateTicks+1.d);
      networkUpdateTicks--;
      }
    else
      {
      clientEnergyState = clientDestEnergyState;
      clientInputEnergy = clientInputDestEnergy;
      }
    }
  }

@Override
protected void handleClientRotationData(ForgeDirection side, int value)
  {
  if(side==getPrimaryFacing())
    {
    clientDestEnergyState = ((double)value) * 0.01d;    
    }
  else if(side==ForgeDirection.UP)
    {
    clientInputDestEnergy = ((double)value) * 0.01d;
    }
  }

@Override
public void onBlockBroken(){}//NOOP

@Override
public int getBoundsMaxWidth(){return 0;}//NOOP

@Override
public int getBoundsMaxHeight(){return 0;}//NOOP

@Override
public boolean canOutputTorque(ForgeDirection towards)
  {
  return towards==orientation;
  }

@Override
public EnumSet<WorksiteUpgrade> getUpgrades(){return EnumSet.noneOf(WorksiteUpgrade.class);}// NOOP

@Override
public EnumSet<WorksiteUpgrade> getValidUpgrades(){return EnumSet.noneOf(WorksiteUpgrade.class);}// NOOP

@Override
public void addUpgrade(WorksiteUpgrade upgrade){}// NOOP

@Override
public void removeUpgrade(WorksiteUpgrade upgrade){}// NOOP

@Override
public void setBounds(BlockPosition p1, BlockPosition p2){}//NOOP

@Override
public void setWorkBoundsMax(BlockPosition max){}//NOOP

@Override
public void setWorkBoundsMin(BlockPosition min){}//NOOP

@Override
public void onBoundsAdjusted(){}//NOOP

@Override
public boolean userAdjustableBlocks(){return false;}// NOOP

@Override
public boolean hasWork()
  {
  return inputCell.getEnergy() < inputCell.getMaxEnergy();
  }

@Override
public void addEnergyFromWorker(IWorker worker)
  {  
  inputCell.setEnergy(inputCell.getEnergy()+AWCoreStatics.energyPerWorkUnit * worker.getWorkEffectiveness(getWorkType()) * AWAutomationStatics.hand_cranked_generator_output_factor);
  }

@Override
public void addEnergyFromPlayer(EntityPlayer player)
  {
  inputCell.setEnergy(inputCell.getEnergy()+AWCoreStatics.energyPerWorkUnit * AWAutomationStatics.hand_cranked_generator_output_factor);
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
public BlockPosition getWorkBoundsMin()//NOOP
  {
  return null;
  }

@Override
public BlockPosition getWorkBoundsMax()//NOOP
  {
  return null;
  }

@Override
public boolean hasWorkBounds()//NOOP
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
public NBTTagCompound getDescriptionTag()
  {
  NBTTagCompound tag = super.getDescriptionTag();
  tag.setInteger("clientEnergy", (int)clientDestEnergyState);
  tag.setInteger("clientInputDestEnergy", (int)clientInputDestEnergy);
  return tag;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  super.onDataPacket(net, pkt);
  NBTTagCompound tag = pkt.func_148857_g();
  clientDestEnergyState = ((double)tag.getInteger("clientEnergy")) / 100.d;
  clientInputDestEnergy = ((double)tag.getInteger("clientInputDestEnergy")) / 100.d;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  if(tag.hasKey("owner")){ownerName = tag.getString("owner");}
  outputCell.setEnergy(tag.getDouble("torqueEnergy"));
  inputCell.setEnergy(tag.getDouble("inputEnergy"));
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  if(ownerName!=null){tag.setString("owner", ownerName);}
  tag.setDouble("torqueEnergy", outputCell.getEnergy());
  tag.setDouble("inputEnergy", inputCell.getEnergy());
  }

@Override
public double getMaxTorque(ForgeDirection from)
  {
  return inputCell.getMaxEnergy() + outputCell.getMaxEnergy();
  }

@Override
public double getTorqueStored(ForgeDirection from)
  {
  return inputCell.getEnergy() + outputCell.getEnergy();
  }

@Override
public double addTorque(ForgeDirection from, double energy)
  {
  if(from==getPrimaryFacing()){return 0;}
  else if(from==ForgeDirection.UP || from==ForgeDirection.UNKNOWN)
    {
    return inputCell.addEnergy(energy);
    }
  return 0;
  }

@Override
public double drainTorque(ForgeDirection from, double energy)
  {
  if(from==getPrimaryFacing()){return outputCell.drainEnergy(energy);}
  return 0;
  }

@Override
public double getMaxTorqueOutput(ForgeDirection from)
  {
  if(from==getPrimaryFacing()){return outputCell.getMaxTickOutput();}
  return 0;
  }

@Override
public double getMaxTorqueInput(ForgeDirection from)
  {
  return 0;
  }

@Override
public boolean canInputTorque(ForgeDirection from)
  {
  return false;
  }

@Override
public float getClientOutputRotation(ForgeDirection from, float delta)
  {
  if(from==getPrimaryFacing()){return getRotation(rotation, prevRotation, delta);}
  else if(from==ForgeDirection.UP){return getRotation(inputRotation, prevInputRotation, delta);}
  return 0;
  }

@Override
public boolean useOutputRotation(ForgeDirection from)
  {
  return true;
  }

@Override
protected double getTotalTorque()
  {
  return inputCell.getEnergy()+outputCell.getEnergy();
  }

}
