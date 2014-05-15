package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class ContainerWorksiteInventorySideSelection extends ContainerBase
{

//public HashMap<RelativeSide, InventorySide> sideMap = new HashMap<RelativeSide, InventorySide>();
public TileWorksiteBase worksite;

public ContainerWorksiteInventorySideSelection(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);  
  worksite = (TileWorksiteBase) player.worldObj.getTileEntity(x, y, z);
  }

@Override
public void sendInitData()
  {  
//  InventorySide accessedSide;
//  
//  NBTTagList tagList = new NBTTagList();
//  NBTTagCompound inner;    
//  
//  for(RelativeSide side : RelativeSide.values())
//    {
//    inner = new NBTTagCompound();
//    inner.setInteger("baseSide", side.ordinal());
//    accessedSide = worksite.inventory.getAccessSideFor(side);
//    inner.setInteger("accessSide", accessedSide.ordinal());
//    sideMap.put(side, accessedSide);
//    tagList.appendTag(inner);
//    } 
//  PacketGui pkt = new PacketGui();
//  pkt.packetData.setTag("slotMap", tagList);  
//  NetworkHandler.sendToPlayer((EntityPlayerMP) player, pkt);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("slotMap"))
    {
    readSlotMap(tag.getTagList("slotMap", Constants.NBT.TAG_COMPOUND));
    if(!player.worldObj.isRemote)
      {
      setSettingsToTile();
      }
    }
  }

protected void setSettingsToTile()
  {
//  for(RelativeSide side : RelativeSide.values())
//    {
//    worksite.inventory.setSideMapping(side, sideMap.get(side));
//    }
  }

public void sendSettingsToServer()
  {
//  InventorySide accessedSide;
//  
//  NBTTagList tagList = new NBTTagList();
//  NBTTagCompound inner;    
//  
//  for(RelativeSide side : RelativeSide.values())
//    {
//    inner = new NBTTagCompound();
//    inner.setInteger("baseSide", side.ordinal());
//    accessedSide = sideMap.get(side);
//    inner.setInteger("accessSide", accessedSide.ordinal());
//    tagList.appendTag(inner);
//    } 
//  PacketGui pkt = new PacketGui();
//  pkt.packetData.setTag("slotMap", tagList);  
//  NetworkHandler.sendToServer(pkt);
  }

protected void readSlotMap(NBTTagList list)
  {
//  NBTTagCompound tag;
//  RelativeSide base;
//  InventorySide access;
//  int b, a;
//  for(int i = 0; i < list.tagCount(); i++)
//    {
//    tag = list.getCompoundTagAt(i);
//    b = tag.getInteger("baseSide");
//    a = tag.getInteger("accessSide");
//    base = RelativeSide.values()[b];
//    access = InventorySide.values()[a];
//    sideMap.put(base, access);  
//    }
  this.refreshGui();
  }

@Override
public void onContainerClosed(EntityPlayer par1EntityPlayer)
  {
  if(!par1EntityPlayer.worldObj.isRemote)
    {
    setSettingsToTile();
    }
  super.onContainerClosed(par1EntityPlayer);
  }

}
