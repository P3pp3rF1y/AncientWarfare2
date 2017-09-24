package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerResearchBook extends ContainerBase {

    public ContainerResearchBook(EntityPlayer player, int x, int y, int z) {
        super(player);
        addPlayerSlots();
        removeSlots();
    }

}
