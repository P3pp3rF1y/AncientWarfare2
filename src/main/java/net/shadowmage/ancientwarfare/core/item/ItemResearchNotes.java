package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemResearchNotes extends ItemBaseCore {

    private List<ItemStack> displayCache = null;

    public ItemResearchNotes() {
        super("research_note");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
        NBTTagCompound tag = stack.getTagCompound();
        String researchName = "corrupt_item";
        boolean known = false;
        if (tag != null && tag.hasKey("researchName")) {
            String name = tag.getString("researchName");
            ResearchGoal goal = ResearchGoal.getGoal(name);
            if (goal != null && Minecraft.getMinecraft().player != null && world != null) {
                researchName = I18n.format(name);
                known = ResearchTracker.INSTANCE.hasPlayerCompleted(world, Minecraft.getMinecraft().player.getName(), goal.getId());
            } else {
                researchName = "missing_goal_for_id_" + researchName;
            }
        }
        tooltip.add(researchName);
        if (known) {
            tooltip.add(I18n.format("guistrings.research.known_research"));
            tooltip.add(I18n.format("guistrings.research.click_to_add_progress"));
        } else {
            tooltip.add(I18n.format("guistrings.research.unknown_research"));
            tooltip.add(I18n.format("guistrings.research.click_to_learn"));
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }

        if (displayCache != null && displayCache.size() > 0) {
            items.addAll(displayCache);
            return;
        }
        displayCache = new ArrayList<>();
        List<ResearchGoal> goals = new ArrayList<>();
        goals.addAll(ResearchGoal.getResearchGoals());
        /*
         * TODO sort list by ??
         */
        @Nonnull ItemStack stack;
        for (ResearchGoal goal : goals) {
            stack = new ItemStack(this);
            stack.setTagInfo("researchName", new NBTTagString(goal.getName()));
            displayCache.add(stack);
            items.add(stack);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
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
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

}
