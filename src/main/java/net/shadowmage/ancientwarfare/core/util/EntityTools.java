package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityTools {
    
    /**
     * Teleport the player to the specified block position. Will try to place the player at once of the four positions beside
     * the block, if they can't fit anywhere beside then it will try on-top.
     * @param entityPlayer The player you want to teleport
     * @param world World object that the entity exists in and the teleport target exists in (cross-dimension teleport not possible)
     * @param targetPos An array containing the x/y/z co-ord of the target. IS NOT VALIDATED.
     * @param doRaw if true, will update the posX/posY/posZ fields instead of calling setPositionAndUpdate. Different parts of Minecraft need one or the other method for whatever reason so if one doesn't work try the other.
     * @return true is successful, otherwise false
     */
    public static boolean teleportPlayerToBlock(EntityPlayer entityPlayer, World world, int[] targetPos, boolean doRaw) {
        int[] tpPos = null;
        if (EntityTools.canPlayerFit(entityPlayer, world, targetPos[0] + 1, targetPos[1], targetPos[2]))
            tpPos = new int[]{targetPos[0] + 1, targetPos[1], targetPos[2]};
        else if (EntityTools.canPlayerFit(entityPlayer, world, targetPos[0], targetPos[1], targetPos[2] + 1))
            tpPos = new int[]{targetPos[0], targetPos[1], targetPos[2] + 1};
        else if (EntityTools.canPlayerFit(entityPlayer, world, targetPos[0] - 1, targetPos[1], targetPos[2]))
            tpPos = new int[]{targetPos[0] - 1, targetPos[1], targetPos[2]};
        else if (EntityTools.canPlayerFit(entityPlayer, world, targetPos[0], targetPos[1], targetPos[2] - 1))
            tpPos = new int[]{targetPos[0], targetPos[1], targetPos[2] - 1};
        // try on top of the block too
        else if (EntityTools.canPlayerFit(entityPlayer, world, targetPos[0], targetPos[1] + 1, targetPos[2]))
            tpPos = new int[]{targetPos[0], targetPos[1] + 1, targetPos[2]};
        
        if (tpPos != null) {
            if (doRaw) {
                entityPlayer.posX = tpPos[0] + 0.5;
                entityPlayer.posY = tpPos[1];
                entityPlayer.posZ = tpPos[2] + 0.5;
            } else {
                entityPlayer.setPositionAndUpdate(tpPos[0] + 0.5, tpPos[1], tpPos[2] + 0.5);
            }
            return true;
        }
        
        return false;
    }
    
    public static boolean canPlayerFit(Entity entity, World world, int posX, int posY, int posZ) {
        if (world.getBlock(posX, posY, posZ).getMaterial().blocksMovement())
            return false;
        if (world.getBlock(posX, posY + 1, posZ).getMaterial().blocksMovement())
            return false;
        return true;
    }
}
