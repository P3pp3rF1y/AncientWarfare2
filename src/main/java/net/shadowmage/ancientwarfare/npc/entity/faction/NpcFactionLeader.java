package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackMeleeLongRange;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackNearest;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIHurt;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWatchClosest;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionCommander;

public abstract class NpcFactionLeader extends NpcFaction {

    public NpcFactionLeader(World par1World) {
        super(par1World);
//  this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(0, new NpcAIDoor(this, true));
        this.tasks.addTask(1, new NpcAIFactionCommander(this));
        this.tasks.addTask(1, new NpcAIFollowPlayer(this));
        this.tasks.addTask(2, new NpcAIMoveHome(this, 50F, 5F, 30F, 5F));
        this.tasks.addTask(3, new NpcAIAttackMeleeLongRange(this));

        this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(102, new NpcAIWander(this));
        this.tasks.addTask(103, new NpcAIWatchClosest(this, EntityLiving.class, 8.0F));

        this.targetTasks.addTask(1, new NpcAIHurt(this));
        this.targetTasks.addTask(2, new NpcAIAttackNearest(this, selector));
    }

    @Override
    public boolean worksInRain() {
        return true;
    }

    @Override
    public boolean shouldSleep() {
        return false;
    }
}
