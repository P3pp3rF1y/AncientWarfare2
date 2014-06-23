package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
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
  inventory = new InventoryNpcEquipment(npc);
  addSlotToContainer(new Slot(inventory, 0, 8, 8)); //weapon slot
  addSlotToContainer(new Slot(inventory, 7, 8, 8+18*1));//shield slot
  addSlotToContainer(new SlotArmor(inventory, 1, 8, 8+18*5, 3, npc));//boots
  addSlotToContainer(new SlotArmor(inventory, 2, 8, 8+18*4, 2, npc));//legs
  addSlotToContainer(new SlotArmor(inventory, 3, 8, 8+18*3, 1, npc));//chest
  addSlotToContainer(new SlotArmor(inventory, 4, 8, 8+18*2, 0, npc));//helm
  addSlotToContainer(new Slot(inventory, 6, 8+18*2, 8+18*2));//upkeep orders slot  TODO add slot validation
  addSlotToContainer(new Slot(inventory, 5, 8+18*2, 8+18*3));//work/combat/route orders slot   TODO add slot validation
  
  guiHeight = addPlayerSlots(player, 8, 8+5*18+8+18, 4)+8;
  name = npc.getCustomNameTag();
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("customName"))
    {    
    npc.setCustomNameTag(tag.getString("customName"));
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
