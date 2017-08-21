package net.shadowmage.ancientwarfare.npc.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.npc.gamedata.HeadquartersTracker;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class TileTeleportHub extends TileEntity {
    private int COUNTER = 0;
    private HashSet<String> arrivals = new HashSet(); // used to cache the new arrivals so they're not instantly teleported away again
    
    @Override
    public void updateEntity() {
        if (this.world.isRemote)
            return;
        COUNTER++;
        if (COUNTER == 10) {
            COUNTER = 0;
            AxisAlignedBB blockSpaceAbove = new AxisAlignedBB(this.xCoord, this.yCoord + 1, this.zCoord, this.xCoord + 1, this.yCoord + 2, this.zCoord + 1);
            List entitiesAbove = this.world.getEntitiesWithinAABB(EntityPlayer.class, blockSpaceAbove);
            for (Object obj : entitiesAbove) {
                EntityPlayer entityPlayer = (EntityPlayer)obj;
                if (arrivals.contains(entityPlayer.getName()))
                    continue; // a player that's only just arrived and not yet alighted 
                int[] hqPos = HeadquartersTracker.get(entityPlayer.world).getHqPos(entityPlayer.getName(), entityPlayer.world);
                if (hqPos != null) {
                    final float randomPitch = (float) (Math.random() * (1.1f - 0.9f) + 0.9f);
                    this.world.playSoundAtEntity(entityPlayer, "ancientwarfare:teleport.out", 0.6F, randomPitch);
                    EntityTools.teleportPlayerToBlock(entityPlayer, entityPlayer.world, hqPos, false);
                    entityPlayer.world.playSoundAtEntity(entityPlayer, "ancientwarfare:teleport.in", 0.6F, randomPitch);
                }
            }
            
            // remove any arrivals from the cache that have alighted
            if (!arrivals.isEmpty()) {
                for (Iterator<String> arrivalsIterator = arrivals.iterator(); arrivalsIterator.hasNext();) {
                    String arrival = arrivalsIterator.next();
                    boolean isStillAbove = false;
                    for (Object obj : entitiesAbove) {
                        if (((EntityPlayer) obj).getName().equals(arrival))
                            isStillAbove = true;
                    }
                    if (!isStillAbove) {
                        arrivalsIterator.remove();
                    }
                }
            }
        }
    }
    
    /**
     * Be sure to call this BEFORE a teleport request 
     * @param entityPlayerName The name of the player that's teleporting here
     */
    public void addArrival(String entityPlayerName) {
        arrivals.add(entityPlayerName);
    }
}
