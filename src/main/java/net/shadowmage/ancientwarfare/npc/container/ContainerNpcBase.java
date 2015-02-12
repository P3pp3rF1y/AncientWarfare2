package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class ContainerNpcBase extends ContainerBase {

    public NpcBase npc;

    public ContainerNpcBase(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        npc = (NpcBase) player.worldObj.getEntityByID(x);
        if (npc == null) {
            throw new IllegalArgumentException("Npc cannot be null for npc container");
        }
    }

}
