package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockTeleportHub extends ItemBlockBase {

    public ItemBlockTeleportHub(Block block) {
        super(block);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("tile.teleport_hub.tooltip.1"));
        tooltip.add(I18n.format("tile.teleport_hub.tooltip.2"));
        tooltip.add(I18n.format("tile.teleport_hub.tooltip.3"));
    }
}