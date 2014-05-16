package net.shadowmage.ancientwarfare.core.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

public class ItemResearchNotes extends Item implements IItemClickable
{

private List<ItemStack> displayCache = null;

public ItemResearchNotes(String localizationKey)
  {
  this.setUnlocalizedName(localizationKey);
  this.setCreativeTab(AWCoreBlockLoader.coreTab);
  setTextureName("ancientwarfare:core/research_note");
  }

@Override
public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
  {  
  NBTTagCompound tag = par1ItemStack.getTagCompound();
  String researchName = "corrupt_item";
  boolean known = false;
  if(tag!=null && tag.hasKey("researchName"))
    {
    String name = tag.getString("researchName");
    ResearchGoal goal = ResearchGoal.getGoal(name);
    if(goal!=null)
      {
      researchName = StatCollector.translateToLocal(name);
      known = ResearchTracker.instance().hasPlayerCompleted(par2EntityPlayer.worldObj, par2EntityPlayer.getCommandSenderName(), goal.getId()); 
      }
    else
      {
      researchName = "missing_goal_for_id_"+researchName;
      }
    }
  par3List.add(researchName);
  if(known)
    {
    par3List.add(StatCollector.translateToLocal("guistrings.research.known_research"));
    par3List.add(StatCollector.translateToLocal("guistrings.research.click_to_add_progress"));
    }
  else
    {
    par3List.add(StatCollector.translateToLocal("guistrings.research.unknown_research"));
    par3List.add(StatCollector.translateToLocal("guistrings.research.click_to_learn"));     
    }
  }

@Override
public void getSubItems(Item item, CreativeTabs tab, List list)
  {
  if(displayCache!=null)
    {
    list.addAll(displayCache);
    return;
    }
  displayCache = new ArrayList<ItemStack>();
  List<ResearchGoal> goals = new ArrayList<ResearchGoal>();
  goals.addAll(ResearchGoal.getResearchGoals());
  /**
   * TODO sort list by ??
   */
  ItemStack stack;
  for(ResearchGoal goal : goals)
    {
    stack = new ItemStack(this);
    stack.setTagInfo("researchName", new NBTTagString(goal.getName()));
    displayCache.add(stack);
    list.add(stack);
    }  
  }

@Override
public void onRightClick(EntityPlayer player, ItemStack stack)
  {
  NBTTagCompound tag = stack.getTagCompound();
  boolean known = false;
  if(tag!=null && tag.hasKey("researchName"))
    {
    String name = tag.getString("researchName");
    ResearchGoal goal = ResearchGoal.getGoal(name);
    if(goal!=null)
      {
      known = ResearchTracker.instance().hasPlayerCompleted(player.worldObj, player.getCommandSenderName(), goal.getId());      
      if(!known)
        {
        if(ResearchTracker.instance().addResearchFromNotes(player.worldObj, player.getCommandSenderName(), goal.getId()) && !player.capabilities.isCreativeMode)
          {
          stack.stackSize--;
          if(stack.stackSize<=0)
            {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
          }
        }
      else
        {
        if(ResearchTracker.instance().addProgressFromNotes(player.worldObj, player.getCommandSenderName(), goal.getId()) && !player.capabilities.isCreativeMode)
          {
          stack.stackSize--;
          if(stack.stackSize<=0)
            {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }          
          }
        }
      }    
    }
  }

@Override
public boolean onRightClickClient(EntityPlayer player, ItemStack stack)
  {
  return true;
  }

@Override
public boolean onLeftClickClient(EntityPlayer player, ItemStack stack)
  {
  return false;
  }

@Override
public void onLeftClick(EntityPlayer player, ItemStack stack)
  {
  
  }

}
