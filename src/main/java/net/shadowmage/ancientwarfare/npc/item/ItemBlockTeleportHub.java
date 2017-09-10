package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockTeleportHub extends ItemBlock {

    public ItemBlockTeleportHub(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("tile.teleportHub.tooltip.1"));
        tooltip.add(I18n.format("tile.teleportHub.tooltip.2"));
        tooltip.add(I18n.format("tile.teleportHub.tooltip.3"));
    }
}