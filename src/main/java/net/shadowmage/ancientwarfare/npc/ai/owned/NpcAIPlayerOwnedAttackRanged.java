package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.entity.IRangedAttackMob;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttack;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIPlayerOwnedAttackRanged extends NpcAIAttack<NpcBase> {

    private final IRangedAttackMob rangedAttacker;
    private double attackDistance = AWNPCStatics.archerRange * AWNPCStatics.archerRange;

    public NpcAIPlayerOwnedAttackRanged(NpcBase npc) {
        super(npc);
        this.rangedAttacker = (IRangedAttackMob) npc;//will classcastexception if improperly used..
        this.moveSpeed = 1.d;
    }

    @Override
    protected boolean shouldCloseOnTarget(double dist) {
        return (dist > attackDistance || !this.npc.getEntitySenses().canSee(getTarget()));
    }

    @Override
    protected void doAttack(double dist) {
        npc.removeAITask(TASK_MOVE);
        this.npc.getNavigator().clearPathEntity();
        if (this.getAttackDelay() <= 0) {
//  TODO readd quiverbow integration or just remove
//            int val = AIHelper.doQuiverBowThing(npc, getTarget());
//            if(val>0){
//                this.setAttackDelay(val);
//                return;
//            }
            float pwr = (float) (attackDistance / dist);
            pwr = pwr < 0.1f ? 0.1f : pwr > 1.f ? 1.f : pwr;
            this.rangedAttacker.attackEntityWithRangedAttack(getTarget(), pwr);
            this.setAttackDelay(35);
        }
    }
}
