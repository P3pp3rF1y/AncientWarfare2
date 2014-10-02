package net.shadowmage.ancientwarfare.automation.tile.worksite;

import java.util.ArrayList;
import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.item.ItemWorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public abstract class TileWorksiteBase extends TileEntity implements IWorkSite, IInteractableTile, IOwnable, ITorqueTile, IRotatableTile
{
protected String owningPlayer = "";

protected ArrayList<ItemStack> inventoryOverflow = new ArrayList<ItemStack>();

private double efficiencyBonusFactor = 0.f;

private EnumSet<WorksiteUpgrade> upgrades = EnumSet.noneOf(WorksiteUpgrade.class);

private ForgeDirection orientation = ForgeDirection.NORTH;

private TorqueCell torqueCell;

public TileWorksiteBase()
  {
  torqueCell = new TorqueCell(32, 0, AWCoreStatics.energyPerWorkUnit*3, 1);
  }

@Override
public final EnumSet<WorksiteUpgrade> getUpgrades(){return upgrades;}

@Override
public EnumSet<WorksiteUpgrade> getValidUpgrades()
  {
  return EnumSet.of(
      WorksiteUpgrade.ENCHANTED_TOOLS_1,
      WorksiteUpgrade.ENCHANTED_TOOLS_2,
      WorksiteUpgrade.TOOL_QUALITY_1,
      WorksiteUpgrade.TOOL_QUALITY_2,
      WorksiteUpgrade.TOOL_QUALITY_3
      );
  }

@Override
public void onBlockBroken()
  {
  for(WorksiteUpgrade ug : this.upgrades)
    {
    InventoryTools.dropItemInWorld(worldObj, ItemWorksiteUpgrade.getStack(ug), xCoord, yCoord, zCoord);
    }
  efficiencyBonusFactor = 0;
  upgrades.clear();
  }

@Override
public final void addUpgrade(WorksiteUpgrade upgrade)
  {
  upgrades.add(upgrade);
  updateEfficiency();
  worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  }

@Override
public final void removeUpgrade(WorksiteUpgrade upgrade)
  {
  upgrades.remove(upgrade);
  updateEfficiency();
  worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  }

protected abstract boolean processWork();

protected abstract boolean hasWorksiteWork();

protected abstract void updateOverflowInventory();

protected abstract void updateWorksite();

@Override
public boolean shouldRenderInPass(int pass)
  {
  return pass==1;
  }

@Override
public abstract boolean onBlockClicked(EntityPlayer player);

@Override
public final float getClientOutputRotation(ForgeDirection from, float delta)
  {
  return 0;
  }

@Override
public final boolean useOutputRotation(ForgeDirection from)
  {
  return false;
  }

@Override
public final double getMaxTorqueOutput(ForgeDirection from)
  {
  return 0;
  }

@Override
public final boolean canOutputTorque(ForgeDirection towards)
  {
  return false;
  }

@Override
public final double drainTorque(ForgeDirection from, double energy)
  {
  return 0;
  }

@Override
public final void addEnergyFromWorker(IWorker worker)
  {  
  addTorque(ForgeDirection.UNKNOWN, AWCoreStatics.energyPerWorkUnit * worker.getWorkEffectiveness(getWorkType()) * AWAutomationStatics.hand_cranked_generator_output_factor);
  }

@Override
public final void addEnergyFromPlayer(EntityPlayer player)
  {
  addTorque(ForgeDirection.UNKNOWN, AWCoreStatics.energyPerWorkUnit * AWAutomationStatics.hand_cranked_generator_output_factor);
  }

@Override
public final double addTorque(ForgeDirection from, double energy)
  {
  return torqueCell.addEnergy(energy);
  }

@Override
public String toString()
  {
  return "Worksite Base["+torqueCell.getEnergy()+"]";
  }

@Override
public final double getMaxTorque(ForgeDirection from)
  {
  return torqueCell.getMaxEnergy();
  }

@Override
public final double getTorqueStored(ForgeDirection from)
  {
  return torqueCell.getEnergy();
  }

@Override
public final double getMaxTorqueInput(ForgeDirection from)
  {
  return torqueCell.getMaxTickInput();
  }

@Override
public final boolean canInputTorque(ForgeDirection from)
  {
  return true;
  }

@Override
public boolean hasWork()
  {
  return torqueCell.getEnergy() < torqueCell.getMaxEnergy() && !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) && inventoryOverflow.isEmpty();  
  }

@Override
public final String getOwnerName()
  {  
  return owningPlayer;
  }

@Override
public final boolean canUpdate()
  {
  return true;
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(worldObj.isRemote){return;}  
  worldObj.theProfiler.startSection("AWWorksite");
  worldObj.theProfiler.startSection("InventoryOverflow");
  if(!inventoryOverflow.isEmpty())
    {
    updateOverflowInventory();
    } 
  worldObj.theProfiler.endStartSection("Check For Work");
  double ePerUse = IWorkSite.WorksiteImplementation.getEnergyPerActivation(efficiencyBonusFactor);
  boolean hasWork = getTorqueStored(null) >= ePerUse && hasWorksiteWork();
  worldObj.theProfiler.endStartSection("Process Work");
  if(hasWork)
    {
    if(processWork())
      {
      torqueCell.setEnergy(torqueCell.getEnergy() - ePerUse);
      }    
    }
  worldObj.theProfiler.endStartSection("WorksiteBaseUpdate");
  updateWorksite();
  worldObj.theProfiler.endSection();
  worldObj.theProfiler.endSection();
  }

protected final void updateEfficiency()
  {
  efficiencyBonusFactor = IWorkSite.WorksiteImplementation.getEfficiencyFactor(upgrades);
  }

@Override
public final void setOwnerName(String name)
  {
  if(name==null){name="";}
  this.owningPlayer = name;  
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setDouble("storedEnergy", torqueCell.getEnergy());
  if(owningPlayer!=null)
    {
    tag.setString("owner", owningPlayer);
    }
  if(!inventoryOverflow.isEmpty())
    {
    NBTTagList list = new NBTTagList();
    for(ItemStack item : inventoryOverflow)
      {
      list.appendTag(InventoryTools.writeItemStack(item, new NBTTagCompound()));
      }
    tag.setTag("inventoryOverflow", list);
    }
  int[] ug = new int[getUpgrades().size()];
  int i = 0;
  for(WorksiteUpgrade u : getUpgrades())
    {
    ug[i] = u.ordinal();
    i++;
    }
  tag.setIntArray("upgrades", ug);
  tag.setInteger("orientation", orientation.ordinal());
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  torqueCell.setEnergy(tag.getDouble("storedEnergy"));
  if(tag.hasKey("owner"))
    {
    owningPlayer = tag.getString("owner");
    }
  if(tag.hasKey("inventoryOverflow"))
    {
    NBTTagList list = tag.getTagList("inventoryOverflow", Constants.NBT.TAG_COMPOUND);
    NBTTagCompound itemTag;
    ItemStack stack;
    for(int i = 0; i < list.tagCount(); i++)
      {
      itemTag = list.getCompoundTagAt(i);
      stack = InventoryTools.readItemStack(itemTag);
      if(stack!=null)
        {
        inventoryOverflow.add(stack);
        }
      }
    }
  int[] ug = tag.getIntArray("upgrades");
  for(int i= 0; i < ug.length; i++)
    {
    upgrades.add(WorksiteUpgrade.values()[ug[i]]);
    }
  if(tag.hasKey("orientation")){orientation = ForgeDirection.values()[tag.getInteger("orientation")];}
  updateEfficiency();
  }

@Override
public final Team getTeam()
  {  
  if(owningPlayer!=null)
    {
    return worldObj.getScoreboard().getPlayersTeam(owningPlayer);
    }
  return null;
  }

@Override
public AxisAlignedBB getRenderBoundingBox()
  {
  if(hasWorkBounds() && getWorkBoundsMin()!=null && getWorkBoundsMax()!=null)
    {
    AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+1, zCoord+1);
    BlockPosition min = getWorkBoundsMin();
    BlockPosition max = getWorkBoundsMax();
    bb.minX = min.x < bb.minX ? min.x : bb.minX;
    bb.minY = min.y < bb.minY ? min.y : bb.minY;
    bb.minZ = min.z < bb.minZ ? min.z : bb.minZ;
    bb.maxX = max.x+1 > bb.maxX ? max.x+1 : bb.maxX;
    bb.maxY = max.y+1 > bb.maxY ? max.y+1 : bb.maxY;
    bb.maxZ = max.z+1 > bb.maxZ ? max.z+1 : bb.maxZ;
    return bb;
    }
  return super.getRenderBoundingBox();
  }

protected NBTTagCompound getDescriptionPacketTag(NBTTagCompound tag)
  {
  int[] ugs = new int[upgrades.size()];
  int i = 0;
  for(WorksiteUpgrade ug : upgrades)
    {
    ugs[i] = ug.ordinal();
    i++;
    }
  tag.setIntArray("upgrades", ugs);
  tag.setInteger("orientation", orientation.ordinal());
  return tag;
  }

@Override
public final Packet getDescriptionPacket()
  {
  NBTTagCompound tag = getDescriptionPacketTag(new NBTTagCompound()); 
  return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, tag);
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {
  super.onDataPacket(net, pkt);
  if(pkt.func_148857_g().hasKey("upgrades"))
    {
    int[] ugs = pkt.func_148857_g().getIntArray("upgrades");
    upgrades.clear();
    for(int i = 0; i < ugs.length; i++)
      {
      upgrades.add(WorksiteUpgrade.values()[ugs[i]]);
      }
    }
  orientation = ForgeDirection.values()[pkt.func_148857_g().getInteger("orientation")];
  this.worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
  }

@Override
public final ForgeDirection getPrimaryFacing()
  {
  return orientation;
  }

@Override
public final void setPrimaryFacing(ForgeDirection face)
  {
  orientation = face;
  this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  this.worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
  }

}
