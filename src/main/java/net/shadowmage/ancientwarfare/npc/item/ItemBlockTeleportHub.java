package net.shadowmage.ancientwarfare.npc.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemBlockTeleportHub extends ItemBlock {

    public ItemBlockTeleportHub(Block block) {
        super(block);
    }
    
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List tooltip, boolean par4) {
        tooltip.add(I18n.format("tile.teleportHub.tooltip.1"));
        tooltip.add(I18n.format("tile.teleportHub.tooltip.2"));
        tooltip.add(I18n.format("tile.teleportHub.tooltip.3"));
    }
}