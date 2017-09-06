package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.MathHelper;

public class NpcAIDoor extends EntityAIBase {
    protected final EntityLiving theEntity;
    private final boolean close;
    protected int doorPosX, doorPosY, doorPosZ;
    protected Block door;
    private int timer;
    private float interactionPosX, interactionPosZ;
    private boolean allDoors;

    public NpcAIDoor(EntityLiving living, boolean closeBehind) {
        this.theEntity = living;
        this.close = closeBehind;
    }

    public EntityAIBase enableAllDoors(){
        allDoors = true;
        return this;
    }

    @Override
    public final boolean shouldExecute() {
        PathNavigate pathnavigate = this.theEntity.getNavigator();
        if (!pathnavigate.getCanBreakDoors() || pathnavigate.noPath())
            return false;
        Path pathentity = pathnavigate.getPath();
        for (int i = 0; i < Math.min(pathentity.getCurrentPathIndex() + 2, pathentity.getCurrentPathLength()); ++i)
        {
            PathPoint pathpoint = pathentity.getPathPointFromIndex(i);
            this.doorPosX = pathpoint.x;
            this.doorPosZ = pathpoint.z;

            if (this.theEntity.getDistanceSq(this.doorPosX, this.theEntity.posY, this.doorPosZ) <= 2.25D)
            {
                this.doorPosY = pathpoint.y;
                if(findDoor())
                    return true;
                this.doorPosY++;
                if(findDoor())
                    return true;
            }
        }

        if (!this.theEntity.isCollidedHorizontally)
            return false;
        this.doorPosX = MathHelper.floor(this.theEntity.posX);
        this.doorPosY = MathHelper.floor(this.theEntity.posY);
        this.doorPosZ = MathHelper.floor(this.theEntity.posZ);
        if(findDoor())
            return true;
        this.doorPosY++;
        return findDoor();
    }

    @Override
    public final boolean shouldContinueExecuting() {
        return close && timer > 0;
    }

    @Override
    public final void startExecuting() {
        doDoorInteraction(true);
        this.timer = 20;
        this.interactionPosX = (float)((double)(this.doorPosX + 0.5F) - this.theEntity.posX);
        this.interactionPosZ = (float)((double)(this.doorPosZ + 0.5F) - this.theEntity.posZ);
    }

    @Override
    public final void updateTask() {
        this.timer--;
        float f = (float)((double)(this.doorPosX + 0.5F) - this.theEntity.posX);
        float f1 = (float)((double)(this.doorPosZ + 0.5F) - this.theEntity.posZ);
        float f2 = this.interactionPosX * f + this.interactionPosZ * f1;
        if (f2 < 0.0F)
        {
            this.timer = 0;
        }
    }

    @Override
    public final void resetTask() {
        if (this.close) {
            doDoorInteraction(false);
        }
    }

    protected boolean findDoor() {
        this.door = this.theEntity.world.getBlock(this.doorPosX, this.doorPosY, this.doorPosZ);
        if(door instanceof BlockDoor){
            return allDoors || door.getMaterial() == Material.WOOD;
        }
        else if(door instanceof BlockFenceGate){
            return true;
        }
        this.door = null;
        return false;
    }

    protected void doDoorInteraction(boolean isOpening) {
        if(door instanceof BlockDoor) {
            ((BlockDoor)door).func_150014_a(this.theEntity.world, this.doorPosX, this.doorPosY, this.doorPosZ, isOpening);
        }else if(door instanceof BlockFenceGate) {
            int meta = this.theEntity.world.getBlockMetadata(this.doorPosX, this.doorPosY, this.doorPosZ);
            if(isOpening){
                if(!BlockFenceGate.isFenceGateOpen(meta)) {
                    int j1 = (MathHelper.floor((double)(this.theEntity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) % 4;
                    int k1 = meta & 3;
                    if (k1 == (j1 + 2) % 4) {
                        meta = j1;
                    }
                    this.theEntity.world.setBlockMetadataWithNotify(this.doorPosX, this.doorPosY, this.doorPosZ, meta | 4, 2);
                    this.theEntity.world.playAuxSFXAtEntity(null, 1003, this.doorPosX, this.doorPosY, this.doorPosZ, 0);
                    Block block = this.theEntity.world.getBlock(this.doorPosX, this.doorPosY + 1, this.doorPosZ);
                    if(block instanceof BlockFenceGate){
                        meta = this.theEntity.world.getBlockMetadata(this.doorPosX, this.doorPosY + 1, this.doorPosZ);
                        if(!BlockFenceGate.isFenceGateOpen(meta)) {
                            k1 = meta & 3;
                            if (k1 == (j1 + 2) % 4) {
                                meta = j1;
                            }
                            this.theEntity.world.setBlockMetadataWithNotify(this.doorPosX, this.doorPosY + 1, this.doorPosZ, meta | 4, 2);
                        }
                    }
                }
            }else if(BlockFenceGate.isFenceGateOpen(meta)) {
                this.theEntity.world.setBlockMetadataWithNotify(this.doorPosX, this.doorPosY, this.doorPosZ, meta & -5, 2);
                this.theEntity.world.playAuxSFXAtEntity(null, 1003, this.doorPosX, this.doorPosY, this.doorPosZ, 0);
                Block block = this.theEntity.world.getBlock(this.doorPosX, this.doorPosY + 1, this.doorPosZ);
                if(block instanceof BlockFenceGate) {
                    meta = this.theEntity.world.getBlockMetadata(this.doorPosX, this.doorPosY + 1, this.doorPosZ);
                    if (BlockFenceGate.isFenceGateOpen(meta)) {
                        this.theEntity.world.setBlockMetadataWithNotify(this.doorPosX, this.doorPosY + 1, this.doorPosZ, meta & -5, 2);
                    }
                }
            }
        }
    }
}
