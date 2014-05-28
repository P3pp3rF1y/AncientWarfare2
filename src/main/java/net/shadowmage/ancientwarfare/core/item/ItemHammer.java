package net.shadowmage.ancientwarfare.core.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemHammer extends Item implements IItemKeyInterface, IItemClickable
{

public ItemHammer(String regName)
  {
  this.setUnlocalizedName(regName);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
  this.setTextureName("ancientwarfare:automation/hammer");
  }

@Override
@SideOnly(Side.CLIENT)
public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
  {
  boolean mode = false;
  if(stack.hasTagCompound())
    {
    mode = stack.getTagCompound().getBoolean("workMode");      
    }
  super.addInformation(stack, par2EntityPlayer, par3List, par4);
  }

@Override
public boolean onKeyActionClient(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public void onKeyAction(EntityPlayer player, ItemStack stack)
  {
  if(player.worldObj.isRemote){return;}
  boolean mode = false;
  if(stack.hasTagCompound())
    {
    mode = stack.getTagCompound().getBoolean("workMode");      
    }
  else
    {
    stack.setTagCompound(new NBTTagCompound());
    }
  mode = !mode;
  stack.getTagCompound().setBoolean("workMode", mode);
  player.addChatMessage(new ChatComponentTranslation("guistrings.automation.work_mode_change"));
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  MovingObjectPosition hit = getMovingObjectPositionFromPlayer(player.worldObj, player, false);
  if(hit==null){return;}
  boolean mode = false;
  if(stack.hasTagCompound())
    {
    mode = stack.getTagCompound().getBoolean("workMode");      
    }
  else
    {
    stack.setTagCompound(new NBTTagCompound());
    }  
  if(mode)
    {
    TileEntity te = player.worldObj.getTileEntity(hit.blockX, hit.blockY, hit.blockZ);
    if(te instanceof IWorkSite)
      {
      if(((IWorkSite) te).hasWork())
        {
        ((IWorkSite) te).addEnergyFromPlayer(player);
        }
      player.addChatMessage(new ChatComponentTranslation("guistrings.automation.doing_player_work"));
      }
    else
      {
      player.addChatMessage(new ChatComponentTranslation("guistrings.automation.wrong_hammer_mode"));
      }
    }
  else
    {
    Block block = player.worldObj.getBlock(hit.blockX, hit.blockY, hit.blockZ);
    if(block==null){return;}    
    player.addChatMessage(new ChatComponentTranslation("guistrings.automation.rotating_block"));
    block.rotateBlock(player.worldObj, hit.blockX, hit.blockY, hit.blockZ, ForgeDirection.getOrientation(hit.sideHit));
    }
  }

/**
 * Returns True is the item is renderer in full 3D when hold.
 */
@SideOnly(Side.CLIENT)
@Override
public boolean isFull3D()
  {
  return true;
  }

@Override
public boolean onLeftClickClient(EntityPlayer player, ItemStack stack)
  {
  return false;
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack)
  {
  MovingObjectPosition hit = getMovingObjectPositionFromPlayer(player.worldObj, player, false);
  if(hit==null){return false;}
  return true;
  }

@Override
public void onLeftClick(EntityPlayer player, ItemStack stack)
  {
  
  }

}
