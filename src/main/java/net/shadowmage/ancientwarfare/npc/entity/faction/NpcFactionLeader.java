package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.*;

public abstract class NpcFactionLeader extends NpcFaction {

    public NpcFactionLeader(World par1World) {
        super(par1World);
//  this.setCurrentItemOrArmor(0, new ItemStack(Items.diamond_sword));

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(1, new NpcAIFollowPlayer(this));
        this.tasks.addTask(2, new NpcAIMoveHome(this, 50F, 5F, 30F, 5F));
        this.tasks.addTask(3, new NpcAIAttackMeleeLongRange(this));

        this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(102, new NpcAIWander(this));
        this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));

        this.targetTasks.addTask(1, new NpcAIHurt(this));
        this.targetTasks.addTask(2, new NpcAIAttackNearest(this, selector));
    }

}
