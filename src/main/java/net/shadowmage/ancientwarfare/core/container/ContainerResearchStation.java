package net.shadowmage.ancientwarfare.core.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.tile.TileResearchStation;

public class ContainerResearchStation extends ContainerBase
{

public String researcherName;
public int currentGoal = -1;
public int progress = 0;
public TileResearchStation tile;
public List<Integer> queuedResearch = new ArrayList<Integer>();

public ContainerResearchStation(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  tile = (TileResearchStation) player.worldObj.getTileEntity(x, y, z);
  if(!player.worldObj.isRemote)
    {
    researcherName = tile.getCrafterName();
    if(researcherName!=null)
      {
      currentGoal = ResearchTracker.instance().getCurrentGoal(player.worldObj, researcherName);
      progress = ResearchTracker.instance().getProgress(player.worldObj, researcherName);
      queuedResearch.addAll(ResearchTracker.instance().getResearchQueueFor(player.worldObj, researcherName));
      }    
    }
  
  Slot slot;
  slot = new Slot(tile.bookInventory, 0, 8, 18+8)
    {
    @Override
    public boolean isItemValid(ItemStack par1ItemStack)
      {
      return par1ItemStack!=null && par1ItemStack.getItem()==AWItems.researchBook && ItemResearchBook.getResearcherName(par1ItemStack)!=null;
      }
    };
  addSlotToContainer(slot);
  
  int y1 = 8+3*18+8 + 2*18 + 4;
  y1 = this.addPlayerSlots(player, 8, y1, 4);
  }

@Override
public void sendInitData()
  {
  NBTTagCompound tag = new NBTTagCompound();
  if(researcherName!=null)
    {
    tag.setString("researcherName", researcherName);    
    }
  else
    {
    tag.setBoolean("clearResearcher", true);
    }
  tag.setInteger("currentGoal", currentGoal);
  tag.setInteger("progress", progress);
  if(!queuedResearch.isEmpty())
    {
    int[] queueData = new int[queuedResearch.size()];
    for(int i = 0; i < queuedResearch.size(); i++)
      {
      queueData[i] = queuedResearch.get(i);
      }
    tag.setIntArray("queuedResearch", queueData);
    }
  this.sendDataToClient(tag);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  AWLog.logDebug("receiving packet data. client: "+player.worldObj.isRemote);
  if(tag.hasKey("researcherName"))
    {
    researcherName = tag.getString("researcherName");
    }
  if(tag.hasKey("clearResearcher"))
    {
    researcherName = null;
    }
  if(tag.hasKey("currentGoal")){currentGoal = tag.getInteger("currentGoal");}
  if(tag.hasKey("progress")){progress = tag.getInteger("progress");}
  if(tag.hasKey("queuedResearch"))
    {
    queuedResearch.clear();
    int [] data = tag.getIntArray("queuedResearch");
    for(int i : data)
      {
      queuedResearch.add(i);
      }
    }
  this.refreshGui();
  }

@Override
public void detectAndSendChanges()
  {
  super.detectAndSendChanges();
  if(player.worldObj.isRemote){return;}
  NBTTagCompound tag = null;
  String name = tile.getCrafterName();
  
  boolean checkGoal = true;
  /**
   * synch researcher name
   */
  if(name==null && researcherName==null)
    {
    //noop
    }
  else if(name==null && researcherName!=null)
    {
    checkGoal = false;
    tag = new NBTTagCompound();
    researcherName = null;
    tag.setBoolean("clearResearcher", true);
    tag.setInteger("currentGoal", -1);
    tag.setInteger("progress", 0);
    }
  else if(name!=null && researcherName==null)
    {
    checkGoal = false;
    tag = new NBTTagCompound();
    researcherName = name;
    tag.setString("researcherName", name);
    currentGoal = ResearchTracker.instance().getCurrentGoal(player.worldObj, researcherName);
    tag.setInteger("currentGoal", currentGoal);
    progress = ResearchTracker.instance().getProgress(player.worldObj, researcherName);
    tag.setInteger("progress", progress);
    }
  else if(!name.equals(researcherName))//updated book/name
    {
    checkGoal = false;
    tag = new NBTTagCompound();
    researcherName = name;
    tag.setString("researcherName", name);
    currentGoal = ResearchTracker.instance().getCurrentGoal(player.worldObj, researcherName);
    tag.setInteger("currentGoal", currentGoal);
    progress = ResearchTracker.instance().getProgress(player.worldObj, researcherName);
    tag.setInteger("progress", progress);
    }  
  
  /**
   * synch progress and current goal -- 
   */
  if(checkGoal && researcherName!=null)
    {
    int g = ResearchTracker.instance().getCurrentGoal(player.worldObj, researcherName);
    if(g!=currentGoal)
      {
      if(tag==null){tag = new NBTTagCompound();}
      currentGoal = g;
      tag.setInteger("currentGoal", currentGoal);
      }
    int p = ResearchTracker.instance().getProgress(player.worldObj, researcherName);
    if(p!=progress)
      {
      if(tag==null){tag = new NBTTagCompound();}
      progress = p;
      tag.setInteger("progress", progress);
      }
    }
  
  if(researcherName!=null)
    {
    List<Integer> queue = ResearchTracker.instance().getResearchQueueFor(player.worldObj, researcherName);
    if(!queue.equals(queuedResearch))
      {
      if(tag==null){tag = new NBTTagCompound();}
      queuedResearch.clear();
      queuedResearch.addAll(queue);
      int[] queueData = new int[queue.size()];
      for(int i = 0; i < queue.size(); i++)
        {
        queueData[i] = queue.get(i);
        }
      tag.setIntArray("queuedResearch", queueData);
      }
    }

  if(tag!=null)
    {
    this.sendDataToClient(tag);
    }
  }

public void removeSlots()
  {
  for(Slot s : ((List<Slot>)this.inventorySlots))
    {
    if(s.yDisplayPosition>=0)
      {
      s.yDisplayPosition-=10000;
      }
    }
  }

public void addSlots()
  {
  for(Slot s : ((List<Slot>)this.inventorySlots))
    {
    if(s.yDisplayPosition < 0)
      {
      s.yDisplayPosition+=10000;
      }
    }
  }

}
