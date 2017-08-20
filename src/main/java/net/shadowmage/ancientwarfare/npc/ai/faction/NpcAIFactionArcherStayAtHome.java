package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.minecraft.util.ChunkCoordinates;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIFactionArcherStayAtHome extends NpcAI<NpcBase> {

    BlockPos target;

    public NpcAIFactionArcherStayAtHome(NpcBase npc) {
        super(npc);
        setMutexBits(ATTACK + MOVE);
    }

    @Override
    public boolean shouldExecute() {
        return npc.getAttackTarget() == null || npc.getAttackTarget().isDead && npc.hasHome();
    }

    @Override
    public void startExecuting() {
        ChunkCoordinates cc = npc.getHomePosition();
        if (cc != null) {
            target = new BlockPos(cc.posX, cc.posY, cc.posZ);
        }
    }

    @Override
    public boolean continueExecuting() {
        return target != null && shouldExecute();
    }

    @Override
    public void updateTask() {
        if (target == null) {
            return;
        }
        double d = npc.getDistanceSq(target);
        if (d > MIN_RANGE) {
            npc.addAITask(TASK_MOVE);
            moveToPosition(target, d);
        } else {
            npc.removeAITask(TASK_MOVE);
        }
    }

    @Override
    public void resetTask() {
        target = null;
        npc.removeAITask(TASK_MOVE);
    }

}
