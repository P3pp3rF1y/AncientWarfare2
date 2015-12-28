package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

public class NpcAIPlayerOwnedAlarmResponse extends NpcAI<NpcPlayerOwned> {

    public NpcAIPlayerOwnedAlarmResponse(NpcPlayerOwned npc) {
        super(npc);
        this.setMutexBits(ATTACK + MOVE);
    }

    @Override
    public boolean shouldExecute() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        return npc.getUpkeepPoint() != null && npc.getUpkeepDimensionId() == npc.worldObj.provider.dimensionId && npc.isAlarmed;
    }
    
    @Override
    public boolean continueExecuting() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        return npc.getUpkeepPoint() != null && npc.getUpkeepDimensionId() == npc.worldObj.provider.dimensionId && npc.isAlarmed;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        npc.addAITask(TASK_GO_HOME);
    }
    
    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        BlockPosition pos = npc.getUpkeepPoint();
        if (pos == null) {
            return;
        }
        double dist = npc.getDistanceSq(pos.x + 0.5d, pos.y, pos.z + 0.5d);
        if (dist > ACTION_RANGE) {
            npc.addAITask(TASK_MOVE);
            moveToPosition(pos, dist);
        } else {
            npc.removeAITask(TASK_MOVE);
        }
    }
    
    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        moveRetryDelay = 0;
        npc.removeAITask(TASK_GO_HOME + TASK_MOVE);
    }
}
