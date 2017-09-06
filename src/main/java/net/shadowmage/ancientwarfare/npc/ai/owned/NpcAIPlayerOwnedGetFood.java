package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class NpcAIPlayerOwnedGetFood extends NpcAI<NpcPlayerOwned> {

    public NpcAIPlayerOwnedGetFood(NpcPlayerOwned npc) {
        super(npc);
        this.setMutexBits(MOVE + ATTACK + HUNGRY);
    }

    @Override
    public boolean shouldExecute() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        return npc.requiresUpkeep() && npc.getUpkeepPoint() != null && npc.getFoodRemaining() == 0 && npc.getUpkeepDimensionId() == npc.world.provider.getDimension();
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        return npc.requiresUpkeep() && npc.getUpkeepPoint() != null && npc.getFoodRemaining() < npc.getUpkeepAmount() && npc.getUpkeepDimensionId() == npc.world.provider.getDimension();
    }

    /*
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        npc.addAITask(TASK_UPKEEP);
    }

    /*
     * Updates the task
     */
    @Override
    public void updateTask() {
        BlockPos pos = npc.getUpkeepPoint();
        if (pos == null) {
            return;
        }
        double dist = npc.getDistanceSq(pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d);
        if (dist > AWNPCStatics.npcActionRange * AWNPCStatics.npcActionRange) {
            npc.addAITask(TASK_MOVE);
            moveToPosition(pos, dist);
        } else {
            npc.removeAITask(TASK_MOVE);
            tryUpkeep(pos);
        }
    }

    /*
     * Resets the task
     */
    @Override
    public void resetTask() {
        moveRetryDelay = 0;
        npc.removeAITask(TASK_UPKEEP + TASK_MOVE);
    }

    protected void tryUpkeep(BlockPos pos) {
        TileEntity te = npc.world.getTileEntity(pos);
        if (te instanceof IInventory) {
            npc.withdrawFood((IInventory) te, npc.getUpkeepBlockSide());
        }
    }

}
