package net.shadowmage.ancientwarfare.automation.container;

import java.util.Collection;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class ContainerWorksiteWorkerView extends ContainerBase
{

public IWorkSite worksite;
public HashMap<String, BlockPosition> workerMap = new HashMap<String, BlockPosition>();
private HashMap<String, BlockPosition> shadowMap = new HashMap<String, BlockPosition>();

public ContainerWorksiteWorkerView(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  worksite = (IWorkSite) player.worldObj.getTileEntity(x, y, z);
  if(!player.worldObj.isRemote)
    {
    Collection<IWorker> workers = worksite.getWorkers();
    for(IWorker worker : workers)
      {
      workerMap.put(worker.getWorkerName(), worker.getPosition().copy());
      }
    }
  }

@Override
public void sendInitData()
  {
  NBTTagCompound tag = new NBTTagCompound();
  NBTTagList workerList = new NBTTagList();
  NBTTagCompound workerTag;
  for(String key : workerMap.keySet())
    {
    workerTag = new NBTTagCompound();
    workerTag.setString("name", key);
    workerMap.get(key).writeToNBT(workerTag);
    workerList.appendTag(workerTag);
    }
  tag.setTag("workerList", workerList);
  sendDataToClient(tag);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("workerList"))
    {
    workerMap.clear();
    NBTTagList list = tag.getTagList("workerList", Constants.NBT.TAG_COMPOUND);
    NBTTagCompound workerTag;
    String name;
    BlockPosition p;
    for(int i = 0; i < list.tagCount(); i++)
      {
      workerTag = list.getCompoundTagAt(i);
      name = workerTag.getString("name");
      p = new BlockPosition(workerTag);
      workerMap.put(name, p);
      }
    }
  refreshGui();
  }

@Override
public void detectAndSendChanges()
  {
  super.detectAndSendChanges();
  shadowMap.clear();
  Collection<IWorker> workers = worksite.getWorkers();
  for(IWorker worker : workers)
    {
    shadowMap.put(worker.getWorkerName(), worker.getPosition().copy());
    }
  if(!shadowMap.equals(workerMap))
    {
    AWLog.logDebug("mismatched set found: "+workerMap +" :: "+shadowMap);
    workerMap.clear();
    workerMap.putAll(shadowMap);
    sendInitData();
    }
  }

}
