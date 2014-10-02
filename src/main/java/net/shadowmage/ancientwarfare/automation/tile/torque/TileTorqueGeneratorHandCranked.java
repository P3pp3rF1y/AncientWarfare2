package net.shadowmage.ancientwarfare.automation.tile.torque;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class TileTorqueGeneratorHandCranked extends TileTorqueGeneratorBase implements IWorkSite, IOwnable
{

String ownerName = "";
TorqueCell torqueCell;

/**
 * client side this == 0.0 -> 1.0
 */
double clientEnergyState;

/**
 * server side this == 0 -> 100 (integer percent)
 * client side this == 0.0 -> 1.0 (actual percent)
 */
double clientDestEnergyState;

/**
 * used client side for rendering
 */
double rotation, prevRotation;

public TileTorqueGeneratorHandCranked()
  {
  torqueCell = new TorqueCell(0, 4, 1600, 1.f);
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(!worldObj.isRemote)
    { 
    serverNetworkUpdate();    
    torqueIn = torqueCell.getEnergy() - prevEnergy;
    double d = torqueCell.getEnergy();
    transferPowerTo(getPrimaryFacing());
    torqueOut = d-torqueCell.getEnergy();
    prevEnergy = torqueCell.getEnergy();
    }
  else
    {
    clientNetworkUpdate();
    updateRotation();
    }
  }

@Override
protected void serverNetworkSynch()
  {
  int percent = (int)(torqueCell.getPercentFull()*100.d);
  percent += (int)(torqueOut / torqueCell.getMaxOutput());
  if(percent>100){percent=100;}
  if(percent != clientDestEnergyState)
    {
    clientDestEnergyState = percent;
    sendSideRotation(getPrimaryFacing(), percent);    
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
  }

@Override
protected void clientNetworkUpdate()
  {
  if(clientEnergyState != clientDestEnergyState)
    {
    if(networkUpdateTicks>=0)
      {
      clientEnergyState += (clientDestEnergyState - clientEnergyState) / ((double)networkUpdateTicks+1.d);
      networkUpdateTicks--;
      }
    else
      {
      clientEnergyState = clientDestEnergyState;
      }
    }
  }

@Override
protected void handleClientRotationData(ForgeDirection side, int value)
  {
  clientDestEnergyState = ((double)value) * 0.01d;
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
  return torqueCell.getEnergy() < torqueCell.getMaxEnergy();
  }

@Override
public void addEnergyFromWorker(IWorker worker)
  {  
  torqueCell.setEnergy(torqueCell.getEnergy()+AWCoreStatics.energyPerWorkUnit * worker.getWorkEffectiveness(getWorkType()) * AWAutomationStatics.hand_cranked_generator_output_factor);
  }

@Override
public void addEnergyFromPlayer(EntityPlayer player)
  {
  torqueCell.setEnergy(torqueCell.getEnergy()+AWCoreStatics.energyPerWorkUnit * AWAutomationStatics.hand_cranked_generator_output_factor);
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
  return tag;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  super.onDataPacket(net, pkt);
  NBTTagCompound tag = pkt.func_148857_g();
  clientDestEnergyState = ((double)tag.getInteger("clientEnergy")) / 100.d;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  if(tag.hasKey("owner")){ownerName = tag.getString("owner");}
  torqueCell.setEnergy(tag.getDouble("torqueEnergy"));  
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  if(ownerName!=null){tag.setString("owner", ownerName);}
  tag.setDouble("torqueEnergy", torqueCell.getEnergy());
  }

@Override
public double getMaxTorque(ForgeDirection from)
  {
  return torqueCell.getMaxEnergy();
  }

@Override
public double getTorqueStored(ForgeDirection from)
  {
  return torqueCell.getEnergy();
  }

@Override
public double addTorque(ForgeDirection from, double energy)
  {
  return torqueCell.addEnergy(energy);
  }

@Override
public double drainTorque(ForgeDirection from, double energy)
  {
  return torqueCell.drainEnergy(energy);
  }

@Override
public double getMaxTorqueOutput(ForgeDirection from)
  {
  return torqueCell.getMaxTickOutput();
  }

@Override
public double getMaxTorqueInput(ForgeDirection from)
  {
  return torqueCell.getMaxTickInput();
  }

@Override
public boolean canInputTorque(ForgeDirection from)
  {
  return false;
  }

@Override
public float getClientOutputRotation(ForgeDirection from, float delta)
  {
  return getRotation(rotation, prevRotation, delta);
  }

@Override
public boolean useOutputRotation(ForgeDirection from)
  {
  return true;
  }

@Override
protected double getTotalTorque()
  {
  return torqueCell.getEnergy();
  }

}
