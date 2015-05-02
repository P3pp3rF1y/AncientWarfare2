package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionArcherStayAtHome;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionFindCommander;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionRangedAttack;
import net.shadowmage.ancientwarfare.npc.entity.RangeAttackHelper;

public abstract class NpcFactionArcher extends NpcFaction implements IRangedAttackMob {

    public NpcFactionArcher(World par1World) {
        super(par1World);
        IEntitySelector selector = new IEntitySelector() {
            @Override
            public boolean isEntityApplicable(Entity entity) {
                if (!isHostileTowards(entity)) {
                    return false;
                }
                if (hasHome()) {
                    ChunkCoordinates home = getHomePosition();
                    double dist = entity.getDistanceSq(home.posX + 0.5d, home.posY, home.posZ + 0.5d);
                    if (dist > 30 * 30) {
                        return false;
                    }
                }
                return true;
            }
        };

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(1, new NpcAIFactionFindCommander(this));
        this.tasks.addTask(1, new NpcAIFollowPlayer(this));
//  this.tasks.addTask(2, new NpcAIMoveHome(this, 50.f, 5.f, 30.f, 5.f)); 
        this.tasks.addTask(2, new NpcAIFactionArcherStayAtHome(this));
        this.tasks.addTask(3, new NpcAIFactionRangedAttack(this));

        this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(102, new NpcAIWander(this, 0.625D));
        this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 0, true, false, selector));
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2) {
        RangeAttackHelper.doRangedAttack(this, par1EntityLivingBase, par2);
    }

}
