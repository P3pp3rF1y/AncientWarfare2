package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.minecraft.entity.IRangedAttackMob;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttack;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionMountedArcher;

public class NpcAIFactionRangedAttack extends NpcAIAttack<NpcBase> {

    private final IRangedAttackMob rangedAttacker;
    private double attackDistanceSq = AWNPCStatics.archerRange * AWNPCStatics.archerRange;

    public NpcAIFactionRangedAttack(NpcBase npc) {
        super(npc);
        this.rangedAttacker = (IRangedAttackMob) npc;//will classcastexception if improperly used..
        this.moveSpeed = 1.d;
        if (npc instanceof NpcFactionMountedArcher)
            attackDistanceSq /= 2;
    }

    @Override
    protected boolean shouldCloseOnTarget(double dist) {
        return (dist > attackDistanceSq || !this.npc.getEntitySenses().canSee(this.getTarget()));
    }

    @Override
    protected void doAttack(double dist){
        double homeDist = npc.getDistanceSqFromHome();
        if (homeDist > MIN_RANGE && dist < 8 * 8) {
            npc.addAITask(TASK_MOVE);
            this.moveToPosition(npc.getHomePosition(), homeDist);
        } else {
            npc.removeAITask(TASK_MOVE);
            npc.getNavigator().clearPathEntity();
        }
        if (this.getAttackDelay() <= 0) {
            int val = 0;//AIHelper.doQuiverBowThing(npc, getTarget()); TODO quiver bow alternative integration?
            if(val>0){
                this.setAttackDelay(val);
                return;
            }
            float pwr = (float) (attackDistanceSq / dist);
            pwr = pwr < 0.1f ? 0.1f : pwr > 1.f ? 1.f : pwr;
            this.rangedAttacker.attackEntityWithRangedAttack(getTarget(), pwr);
            this.setAttackDelay(35);
        }
    }
}
