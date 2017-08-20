package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

import java.util.ArrayList;
import java.util.List;

public class ItemResearchNotes extends Item {

    private List<ItemStack> displayCache = null;

    public ItemResearchNotes() {
        this.setCreativeTab(AWCoreBlockLoader.coreTab);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        NBTTagCompound tag = par1ItemStack.getTagCompound();
        String researchName = "corrupt_item";
        boolean known = false;
        if (tag != null && tag.hasKey("researchName")) {
            String name = tag.getString("researchName");
            ResearchGoal goal = ResearchGoal.getGoal(name);
            if (goal != null) {
                researchName = I18n.format(name);
                known = ResearchTracker.INSTANCE.hasPlayerCompleted(par2EntityPlayer.world, par2EntityPlayer.getName(), goal.getId());
            } else {
                researchName = "missing_goal_for_id_" + researchName;
            }
        }
        par3List.add(researchName);
        if (known) {
            par3List.add(I18n.format("guistrings.research.known_research"));
            par3List.add(I18n.format("guistrings.research.click_to_add_progress"));
        } else {
            par3List.add(I18n.format("guistrings.research.unknown_research"));
            par3List.add(I18n.format("guistrings.research.click_to_learn"));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        if (displayCache != null) {
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
        for (ResearchGoal goal : goals) {
            stack = new ItemStack(this);
            stack.setTagInfo("researchName", new NBTTagString(goal.getName()));
            displayCache.add(stack);
            list.add(stack);
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        NBTTagCompound tag = stack.getTagCompound();
        if (!world.isRemote && tag != null && tag.hasKey("researchName")) {
            String name = tag.getString("researchName");
            ResearchGoal goal = ResearchGoal.getGoal(name);
            if (goal != null) {
                boolean known = ResearchTracker.INSTANCE.hasPlayerCompleted(player.world, player.getName(), goal.getId());
                if (!known) {
                    if (ResearchTracker.INSTANCE.addResearchFromNotes(player.world, player.getName(), goal.getId()) && !player.capabilities.isCreativeMode) {
                        player.sendMessage(new TextComponentTranslation("guistrings.research.learned_from_item"));
                        stack.shrink(1);
                    }
                } else {
                    if (ResearchTracker.INSTANCE.addProgressFromNotes(player.world, player.getName(), goal.getId()) && !player.capabilities.isCreativeMode) {
                        player.sendMessage(new TextComponentTranslation("guistrings.research.added_progress"));
                        stack.shrink(1);
                    }
                }
            }
        }
        return stack;
    }

}
