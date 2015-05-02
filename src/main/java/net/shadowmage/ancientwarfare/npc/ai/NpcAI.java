package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

/**
 * AI template class with utility methods and member access for a non-specific NPC type
 *
 * @author Shadowmage
 */
public abstract class NpcAI extends EntityAIBase {
    /**
     * used during npc-ai task-rendering to determine how many bits to loop through
     * of the task bitfield -- needs to be increased if more task types / bits are added
     */
    public static final int NUMBER_OF_TASKS = 10;
    public static final int TASK_ATTACK = 1;
    public static final int TASK_UPKEEP = 2;
    public static final int TASK_IDLE_HUNGRY = 4;
    public static final int TASK_GO_HOME = 8;
    public static final int TASK_WORK = 16;
    public static final int TASK_PATROL = 32;
    public static final int TASK_GUARD = 64;
    public static final int TASK_FOLLOW = 128;
    public static final int TASK_WANDER = 256;
    public static final int TASK_MOVE = 512;

    /**
     * internal flag used to determine exclusion types
     */
    public static final int MOVE = 1;
    public static final int ATTACK = 2;
    public static final int SWIM = 4;
    public static final int HUNGRY = 8;

    protected int moveRetryDelay;
    protected double moveSpeed = 1.d;
    private double maxPFDist;
    private double maxPFDistSq;


    protected NpcBase npc;

    public NpcAI(NpcBase npc) {
        this.npc = npc;
        maxPFDist = AWNPCStatics.npcPathfindRange * 0.90d;

        maxPFDistSq = maxPFDist * maxPFDist;
    }

    //Inserting Item#onUpdate, to let it do whatever it needs to do. Used by QuiverBow for burst fire
    protected final void updateHeldItem(){
        ItemStack stack = npc.getHeldItem();
        if (stack != null) {
            stack.getItem().onUpdate(stack, npc.worldObj, npc, 0, true);
        }
    }

    protected final void moveToEntity(Entity target, double sqDist) {
        moveToPosition(target.posX, target.boundingBox.minY, target.posZ, sqDist);
    }

    protected final void moveToPosition(int x, int y, int z, double sqDist) {
        moveToPosition(x + 0.5d, y, z + 0.5d, sqDist);
    }

    protected final void moveToPosition(BlockPosition pos, double sqDist) {
        moveToPosition(pos.x + 0.5d, pos.y, pos.z + 0.5d, sqDist);
    }

    protected final void moveToPosition(double x, double y, double z, double sqDist) {
        moveRetryDelay--;
        if (moveRetryDelay <= 0) {
            if (sqDist > maxPFDistSq) {
                moveLongDistance(x, y, z);
                moveRetryDelay = 60;//3 second delay between PF attempts, give the entity time to get a bit closer to target
            } else {
                npc.getNavigator().tryMoveToXYZ(x, y, z, moveSpeed);
                moveRetryDelay = 10;//base .5 second retry delay
                if (sqDist > 256) {
                    moveRetryDelay += 10;
                }//add .5 seconds if distance>16
                if (sqDist > 1024) {
                    moveRetryDelay += 20;
                }//add another 1 second if distance>32 (delay will be 2 seconds total at this point)
            }
        }
    }

    protected final void moveLongDistance(double x, double y, double z) {
        Vec3 vec = Vec3.createVectorHelper(x - npc.posX, y - npc.posY, z - npc.posZ);

        //normalize vector to a -1 <-> 1 value range
        double w = Math.sqrt(vec.xCoord * vec.xCoord + vec.yCoord * vec.yCoord + vec.zCoord * vec.zCoord);
        if (w != 0) {
            vec.xCoord /= w;
            vec.yCoord /= w;
            vec.zCoord /= w;
        }

        //then mult by PF distance to find the proper vector for our PF length
        vec.xCoord *= maxPFDist;
        vec.yCoord *= maxPFDist;
        vec.zCoord *= maxPFDist;

        //finally re-offset by npc position to get an actual target position
        vec.xCoord += npc.posX;
        vec.yCoord += npc.posY;
        vec.zCoord += npc.posZ;

        //move npc towards the calculated partial target
        npc.getNavigator().tryMoveToXYZ(vec.xCoord, vec.yCoord, vec.zCoord, moveSpeed);
    }

}
