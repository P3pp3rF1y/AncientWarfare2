package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.util.ChunkPos;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIMoveHome extends NpcAI<NpcBase> {

    private final float dayRange, nightRange;
    private final float dayLeash, nightLeash;
    
    private int ticker = 0;
    private final static int TICKER_MAX = 10; // TODO: Maybe move to config? 
    private boolean goneHome = false;

    public NpcAIMoveHome(NpcBase npc, float dayRange, float nightRange, float dayLeash, float nightLeash) {
        super(npc);
        this.setMutexBits(MOVE + ATTACK);
        this.dayRange = dayRange;
        this.nightRange = nightRange;
        this.dayLeash = dayLeash;
        this.nightLeash = nightLeash;
    }

    @Override
    public boolean shouldExecute() {
        if (!npc.getIsAIEnabled() || !npc.hasHome()) {
            return false;
        }
        ChunkPos cc = npc.getHomePosition();
        float distSq = (float) npc.getDistanceSq(cc.posX + 0.5d, cc.posY, cc.posZ + 0.5d);
        return npc.shouldBeAtHome() || exceedsRange(distSq);
    }

    protected boolean exceedsRange(float distSq) {
        float range = getRange() * getRange();
        return distSq > range;
    }

    protected boolean exceedsLeash(float distSq) {
        float leash = getLeashRange() * getLeashRange();
        return distSq > leash;
    }

    protected float getLeashRange() {
        return !npc.shouldSleep() ? dayLeash : nightLeash;
    }

    protected float getRange() {
        return !npc.shouldSleep() ? dayRange : nightRange;
    }

    @Override
    public void startExecuting() {
        npc.addAITask(TASK_GO_HOME);
        updateTasks();
        goneHome = false;
    }

    @Override
    public void updateTask() {
        ticker++;
        if (ticker >= TICKER_MAX) {
            if (npc.getSleeping()) {
                if (npc.isBedCacheValid())
                    npc.setPositionToBed();
                else
                    npc.wakeUp();
                return;
            }
        }
        ChunkPos cc = npc.getHomePosition();
        double dist = npc.getDistanceSq(cc.posX + 0.5d, cc.posY, cc.posZ + 0.5d);
        double leash = getLeashRange() * getLeashRange();
        if ((dist > leash) && (!goneHome) && (!npc.getSleeping())) {
            npc.addAITask(TASK_MOVE);
            moveToPosition(cc.posX, cc.posY, cc.posZ, dist);
        } else {
            // NPC is home
            npc.removeAITask(TASK_MOVE);
            goneHome = true;
            if (npc.getOwnerName().isEmpty())
                stopMovement();
            else {
                if ((ticker >= TICKER_MAX) && (npc.shouldSleep())) {
                    BlockPos pos = npc.findBed();
                    if (pos != null) {
                        dist = npc.getDistanceSq(pos.x, pos.y, pos.z);
                        if (dist > AWNPCStatics.npcActionRange * AWNPCStatics.npcActionRange) {
                            moveToPosition(pos, dist, true);
                        } else {
                            if (npc.lieDown(pos)) {
                                npc.setPositionToBed();
                                stopMovement();
                            }
                        }
                    }
                }
            }
        }
        if (ticker >= TICKER_MAX) {
            updateTasks();
            ticker = 0;
        }
    }
    
    private void updateTasks() {
        if (npc.shouldSleep())
            npc.addAITask(TASK_SLEEP);
        else
            npc.removeAITask(TASK_SLEEP);
        if (npc.world.isRaining())
            npc.addAITask(TASK_RAIN);
        else
            npc.removeAITask(TASK_RAIN);
    }

    private void stopMovement() {
        npc.removeAITask(TASK_MOVE);
        npc.getNavigator().clearPathEntity();
    }

    @Override
    public void resetTask() {
        stopMovement();
        npc.removeAITask(TASK_GO_HOME + TASK_RAIN + TASK_SLEEP);
        if (npc.getSleeping())
            npc.wakeUp();
    }
}
