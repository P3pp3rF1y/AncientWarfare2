package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.inventory.SlotArmor;
import net.shadowmage.ancientwarfare.npc.inventory.InventoryNpcEquipment;

public class ContainerNpcInventory extends ContainerNpcBase
{

InventoryNpcEquipment inventory;
public int guiHeight;
String name;

public ContainerNpcInventory(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  inventory = new InventoryNpcEquipment(npc);//weapon slot
  addSlotToContainer(new Slot(inventory, 0, 8, 8)); 
  addSlotToContainer(new SlotArmor(inventory, 1, 8, 8+18*4, 3, npc));//helm
  addSlotToContainer(new SlotArmor(inventory, 2, 8, 8+18*3, 2, npc));//chest
  addSlotToContainer(new SlotArmor(inventory, 3, 8, 8+18*2, 1, npc));//legs
  addSlotToContainer(new SlotArmor(inventory, 4, 8, 8+18*1, 0, npc));//boots  
  addSlotToContainer(new Slot(inventory, 6, 8+18*2, 8+18*2));//upkeep orders slot  
  addSlotToContainer(new Slot(inventory, 5, 8+18*2, 8+18*3));//work/combat/route orders slot        
  guiHeight = addPlayerSlots(player, 8, 8+5*18+8, 4)+8;
  name = npc.getCustomNameTag();
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("customName"))
    {    
    npc.setCustomNameTag(tag.getString("customName"));
    AWLog.logDebug("setting npc custom name from packet input: "+npc.getCustomNameTag());
    }
  if(tag.hasKey("repack"))
    {
    npc.repackEntity(player);
    }
  if(tag.hasKey("setHome")){npc.setHomeArea(MathHelper.floor_double(npc.posX), MathHelper.floor_double(npc.posY), MathHelper.floor_double(npc.posZ), 40);}
  if(tag.hasKey("clearHome")){npc.detachHome();}
  }

public void handleNpcNameUpdate(String newName)
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setString("customName", newName);
  sendDataToServer(tag);
  }

}
