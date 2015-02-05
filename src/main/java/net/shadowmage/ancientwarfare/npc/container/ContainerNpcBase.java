package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.container.ContainerEntityBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class ContainerNpcBase<T extends NpcBase> extends ContainerEntityBase<T> {

    public ContainerNpcBase(EntityPlayer player, int x) {
        super(player, x);
    }
}
