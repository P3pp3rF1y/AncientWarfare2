package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.HashSet;

/**
 * AI template class with utility methods and member access for a non-specific NPC type
 *
 * @author Shadowmage
 */
public abstract class NpcAI<T extends NpcBase> extends EntityAIBase {
    /**
     * used during npc-ai task-rendering to determine how many bits to loop through
     * of the task bitfield -- needs to be increased if more task types / bits are added
     */
    public static final int NUMBER_OF_TASKS = 14;
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
    public static final int TASK_ALARM = 1024;
    public static final int TASK_FLEE = 2048;
    public static final int TASK_SLEEP = 4096;
    public static final int TASK_RAIN = 8192;
    

    /**
     * internal flag used to determine exclusion types
     */
    public static final int MOVE = 1;
    public static final int ATTACK = 2;
    public static final int SWIM = 4;
    public static final int HUNGRY = 8;

    public static final int MIN_RANGE = 9;
    
    private static HashSet<String> HOSTILE_ENTITIES = new HashSet<String>();

    protected int moveRetryDelay;
    protected double moveSpeed = 1.d;
    private double maxPFDist;
    private double maxPFDistSq;

    protected T npc;

    public NpcAI(T npc) {
        this.npc = npc;
        maxPFDist = npc.getEntityAttribute(SharedMonsterAttributes.followRange).getBaseValue() * 0.90d;

        maxPFDistSq = maxPFDist * maxPFDist;
    }

    protected final void moveToEntity(Entity target, double sqDist) {
        moveToPosition(target.posX, target.boundingBox.minY, target.posZ, sqDist);
    }

    protected final void moveToPosition(int x, int y, int z, double sqDist) {
        moveToPosition(x + 0.5d, y, z + 0.5d, sqDist);
    }

    protected final void moveToPosition(BlockPos pos, double sqDist) {
        moveToPosition(pos.x, pos.y, pos.z, sqDist);
    }
    
    /**
     * Forced move without delay. Should only be used in logic that has it's own delay.
     * @param pos
     * @param sqDist
     * @param forced
     */
    protected final void moveToPosition(BlockPos pos, double sqDist, boolean forced) {
        moveRetryDelay = 0;
        moveToPosition(pos, sqDist);
    }

    protected final void moveToPosition(double x, double y, double z, double sqDist) {
        moveRetryDelay--;
        if (moveRetryDelay <= 0) {
            if (sqDist > maxPFDistSq) {
                moveLongDistance(x, y, z);
                moveRetryDelay = 60;//3 second delay between PF attempts, give the entity time to get a bit closer to target
            } else {
                setPath(x, y, z);
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
        setPath(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    protected final void returnHome(){
        ChunkCoordinates cc = npc.getHomePosition();
        setPath(cc.posX, cc.posY, cc.posZ);
    }

    protected final void setPath(double x, double y, double z){
        PathEntity pathEntity = trimPath(npc.getNavigator().getPathToXYZ(x, y, z));
        npc.getNavigator().setPath(pathEntity, moveSpeed);
    }

    protected PathEntity trimPath(PathEntity pathEntity){
        if(pathEntity!=null){
            int index = pathEntity.getCurrentPathIndex();
            PathPoint pathpoint = pathEntity.getPathPointFromIndex(index);
            if(npc.getBlockPathWeight(pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord) >= 0) {

                for (int i = index + 1; i < pathEntity.getCurrentPathLength() ; i++){
                    pathpoint = pathEntity.getPathPointFromIndex(i);
                    if (npc.getBlockPathWeight(pathpoint.xCoord, pathpoint.yCoord, pathpoint.zCoord)<0){
                        pathEntity.setCurrentPathLength(i - 1);
                        break;
                    }
                }
            }else{
                Vec3 vec = RandomPositionGenerator.findRandomTargetBlockAwayFrom(npc, MIN_RANGE, MIN_RANGE, Vec3.createVectorHelper(npc.posX, npc.posY, npc.posZ));
                if(vec!=null)
                    return npc.getNavigator().getPathToXYZ(vec.xCoord, vec.yCoord, vec.zCoord);
            }
        }
        return pathEntity;
    }
    
    /**
     * Returns true if the given entity targets/attacks NPC's
     */
    public static boolean isAlwaysHostileToNpcs(Entity entity) {
        if (HOSTILE_ENTITIES.contains(EntityList.getEntityString(entity)))
            return true;
        return false;
    }
    
    public static void addHostileEntity(Entity entity) {
        HOSTILE_ENTITIES.add(EntityList.getEntityString(entity));
    }
}
