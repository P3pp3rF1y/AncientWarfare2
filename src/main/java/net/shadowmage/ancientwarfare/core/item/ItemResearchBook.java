package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.MovingObjectPosition;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;

public class ItemResearchBook extends ItemClickable
{

public ItemResearchBook(String localizationKey)
  {
  super(localizationKey);
  this.setCreativeTab(AWCoreBlockLoader.coreTab);
  this.setMaxStackSize(1);
  }

@Override
public void onRightClick(ItemStack stack, EntityPlayer player, MovingObjectPosition hit)
  {
  if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("researcherName"))
    {
    stack.setTagInfo("researcherName", new NBTTagString(player.getCommandSenderName()));
    }
  }

public static final String getResearcherName(ItemStack stack)
  {
  if(stack!=null && stack.getItem()==AWItems.researchBook && stack.hasTagCompound() && stack.getTagCompound().hasKey("researcherName"))
    {
    return stack.getTagCompound().getString("researcherName");
    }
  return null;
  }

}
