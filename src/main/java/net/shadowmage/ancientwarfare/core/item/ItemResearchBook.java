package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

import javax.annotation.Nullable;
import java.util.List;

public class ItemResearchBook extends Item {

    public ItemResearchBook() {
        this.setCreativeTab(AWCoreBlockLoader.coreTab);
        this.setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        String name = getResearcherName(stack);
        if (name == null) {
            tooltip.add(I18n.format("guistrings.research.researcher_name") + ": " + I18n.format("guistrings.research.no_researcher"));
            tooltip.add(I18n.format("guistrings.research.right_click_to_bind"));
        } else {
            tooltip.add(I18n.format("guistrings.research.researcher_name") + ": " + name);
            tooltip.add(I18n.format("guistrings.research.right_click_to_view"));
        }
    }

    @Nullable
    public static String getResearcherName(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() == AWItems.researchBook && stack.hasTagCompound() && stack.getTagCompound().hasKey("researcherName")) {
            return stack.getTagCompound().getString("researcherName");
        }
        return null;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(!world.isRemote) {
            if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("researcherName")) {
                stack.setTagInfo("researcherName", new NBTTagString(player.getName()));
                player.sendMessage(new TextComponentTranslation("guistrings.research.book_bound"));
            } else {
                NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_RESEARCH_BOOK, 0, 0, 0);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
