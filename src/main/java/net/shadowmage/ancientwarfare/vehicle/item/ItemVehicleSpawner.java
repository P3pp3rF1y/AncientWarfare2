package net.shadowmage.ancientwarfare.vehicle.item;

import java.util.HashMap;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.IIcon;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.vehicle.entity.AWVehicleEntityLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemVehicleSpawner extends Item implements IItemClickable
{

/**
 * TODO this can probably be removed in favor of modeled items
 */
private static HashMap<String, IIcon> regNameToIcon = new HashMap<String, IIcon>();

public ItemVehicleSpawner(String regName)
  {
  this.setUnlocalizedName(regName);
  this.setHasSubtypes(true);
  setCreativeTab(AWVehicleItemLoader.vehicleTab);
  }

@SuppressWarnings({ "rawtypes"})
@Override
@SideOnly(Side.CLIENT)
public void addInformation(ItemStack stack, EntityPlayer player, List tooltipList, boolean displayDetailedInformation)
  {
  // TODO add info regarding vehicle type and any potentially stored stats therein
  super.addInformation(stack, player, tooltipList, displayDetailedInformation);
  }

@SuppressWarnings({ "rawtypes", "unchecked" })
@Override
@SideOnly(Side.CLIENT)
public void getSubItems(Item item, CreativeTabs tab, List list)
  {
  List<String> types = AWVehicleEntityLoader.getVehicleTypes();
  ItemStack stack;
  for(String t : types)
    {
    stack = new ItemStack(item, 1);
    stack.setTagInfo("type", new NBTTagString(t));
    list.add(stack);
    }  
  }

@Override
@SideOnly(Side.CLIENT)
public void registerIcons(IIconRegister reg)
  {
  List<String> types = AWVehicleEntityLoader.getVehicleTypes();
  for(String t : types)
    {
    regNameToIcon.put(t, reg.registerIcon(AWVehicleEntityLoader.getIcon(t)));
    }
  }

@Override
public IIcon getIcon(ItemStack stack, int pass)
  {
  if(stack.hasTagCompound() && stack.getTagCompound().hasKey("type"))
    {
    return regNameToIcon.get(stack.getTagCompound().getString("type"));
    }
  //TODO return a default placeholder Icon?
  return super.getIcon(stack, pass);
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  // TODO lookup entity spawn type, spawn entity in world
  AWLog.logDebug("right click on spawner!!");
  if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("type"))
    {
    AWLog.logDebug("Invalid spawner item!!");
    return;
    }
  String type = stack.getTagCompound().getString("type");
  Entity e = AWEntityRegistry.createEntity(type, player.worldObj);
  if(e!=null)
    {
    e.setPosition(player.posX, player.posY, player.posZ);//TODO set position from player clicked-on target
    player.worldObj.spawnEntityInWorld(e);
    }
  }

@Override
public boolean onLeftClickClient(EntityPlayer player, ItemStack stack){return false;}//NOOP

@Override
public boolean cancelLeftClick(EntityPlayer player, ItemStack stack){return false;}//NOOP

@Override
public void onLeftClick(EntityPlayer player, ItemStack stack){}//NOOP

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack){return true;}

@Override
public boolean cancelRightClick(EntityPlayer player, ItemStack stack){return true;}

}
