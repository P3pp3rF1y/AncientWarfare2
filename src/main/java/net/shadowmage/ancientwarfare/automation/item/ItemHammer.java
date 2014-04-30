package net.shadowmage.ancientwarfare.automation.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteReedFarm;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.item.ItemClickable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemHammer extends ItemClickable implements IItemKeyInterface
{

public ItemHammer(String regName)
  {
  super(regName);
  this.setCreativeTab(AWAutomationItemLoader.automationTab);
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
public void onRightClick(ItemStack stack, EntityPlayer player, MovingObjectPosition hit)
  {
  AWLog.logDebug("hammer right click...");
  if(player.worldObj.isRemote || hit==null){return;}
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
      ((IWorkSite)te).doPlayerWork(player);
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
    block.rotateBlock(player.worldObj, hit.blockX, hit.blockY, hit.blockZ, ForgeDirection.getOrientation(hit.sideHit));
    player.addChatMessage(new ChatComponentTranslation("guistrings.automation.rotating_block"));
    }
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

}
