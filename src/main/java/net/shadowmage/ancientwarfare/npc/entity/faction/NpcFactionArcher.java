package net.shadowmage.ancientwarfare.npc.entity.faction;

import com.google.common.base.Predicate;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.*;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionArcherStayAtHome;
import net.shadowmage.ancientwarfare.npc.ai.faction.NpcAIFactionRangedAttack;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.RangeAttackHelper;

public abstract class NpcFactionArcher extends NpcFaction implements IRangedAttackMob {

    private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.createKey(NpcFactionArcher.class, DataSerializers.BOOLEAN);

    public NpcFactionArcher(World par1World) {
        super(par1World);
        Predicate<NpcBase> selector = entity -> {
			if (!isHostileTowards(entity)) {
				return false;
			}
			if (hasHome()) {
				BlockPos home = getHomePosition();
				double dist = entity.getDistanceSq(home.getX() + 0.5d, home.getY(), home.getZ() + 0.5d);
				if (dist > 30 * 30) {
					return false;
				}
			}
			return true;
		};

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(0, new NpcAIDoor(this, true));
        this.tasks.addTask(1, new NpcAIFollowPlayer(this));
//  this.tasks.addTask(2, new NpcAIMoveHome(this, 50.f, 5.f, 30.f, 5.f)); 
        this.tasks.addTask(2, new NpcAIFactionArcherStayAtHome(this));
        this.tasks.addTask(3, new NpcAIFactionRangedAttack(this));

        this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(102, new NpcAIWander(this));
        this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));

        this.targetTasks.addTask(1, new NpcAIHurt(this));
        this.targetTasks.addTask(2, new NpcAIAttackNearest(this, selector));
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float force) { 
        RangeAttackHelper.doRangedAttack(this, target, force, 1.0f);
    }

    @Override
    public boolean canAttackClass(Class claz) {
        return true;
    }

    @Override
    public void setSwingingArms(boolean swingingArms) {
        this.dataManager.set(SWINGING_ARMS, swingingArms);
    } //TODO add use of this data in rendering
}
