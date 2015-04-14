package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.util.ChunkCoordinates;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIPlayerOwnedIdleWhenHungry extends NpcAI {

    int moveTimer = 0;

    public NpcAIPlayerOwnedIdleWhenHungry(NpcBase npc) {
        super(npc);
        this.setMutexBits(MOVE + ATTACK + HUNGRY);
    }

    @Override
    public boolean shouldExecute() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        return npc.getAttackTarget() == null && npc.requiresUpkeep() && npc.getFoodRemaining() == 0;
    }

    @Override
    public boolean continueExecuting() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        return npc.getAttackTarget() == null && npc.requiresUpkeep() && npc.getFoodRemaining() == 0;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        npc.addAITask(TASK_IDLE_HUNGRY);
        moveTimer = 0;
        if (npc.hasHome()) {
            ChunkCoordinates cc = npc.getHomePosition();
            npc.getNavigator().tryMoveToXYZ(cc.posX, cc.posY, cc.posZ, 1.0d);
        }
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        npc.removeAITask(TASK_IDLE_HUNGRY);
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        if (npc.hasHome()) {
            moveTimer--;
            if (moveTimer <= 0) {
                ChunkCoordinates cc = npc.getHomePosition();
                npc.getNavigator().tryMoveToXYZ(cc.posX, cc.posY, cc.posZ, 1.0d);
                moveTimer = 10;
            }
        }
    }


}
