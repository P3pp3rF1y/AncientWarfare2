//TODO fix ftb utils integration
package net.shadowmage.ancientwarfare.core.gamedata;

import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import ftb.utils.world.LMWorldServer;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.gamedata.ChunkClaims.ChunkClaimEntry;
import net.shadowmage.ancientwarfare.core.gamedata.ChunkClaims.ChunkClaimInfo;
import net.shadowmage.ancientwarfare.core.gamedata.ChunkClaims.TownHallEntry;
import net.shadowmage.ancientwarfare.npc.block.BlockTownHall;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class ChunkClaimWorkerThread extends Thread {

    private static boolean IS_ENABLED = false;
    
    public ChunkClaimWorkerThread() {
        AncientWarfareCore.log.info("ChunkClaimWorkerThread starting as disabled");
        IS_ENABLED = false;
        this.start();
    }
    
    @Override
    public void run() {
        final String orgThreadName = Thread.currentThread().getName();
        Thread.currentThread().setName(orgThreadName + " - AW2 ChunkClaimWorkerThread");
        try {
            while (true) {
                if (IS_ENABLED) {
                    Iterator<Entry<Integer, LinkedHashMap<Integer, ChunkClaimEntry>>> chunkClaimEntriesIterator = ChunkClaims.get(DimensionManager.getWorld(0)).chunkClaimsPerDimension.entrySet().iterator();
                    while (chunkClaimEntriesIterator.hasNext()) {
                        Entry<Integer, LinkedHashMap<Integer, ChunkClaimEntry>> thisEntry = chunkClaimEntriesIterator.next();
                        // ensure the dimension for this ChunkClaimEntry map is loaded, skip it if not
                        int dimId = thisEntry.getKey();
                        World world = DimensionManager.getWorld(dimId);
                        if (world == null)
                            continue;
                        
                        HashMap<ChunkDimPos, String> chunksToClaim = new HashMap<>();
                        HashMap<ChunkDimPos, String> chunksToUnclaim = new HashMap<>();
                        
                        // iterate over ChunkClaimEntry sets
                        Iterator<Entry<Integer, ChunkClaimEntry>> chunkClaimEntrySets = thisEntry.getValue().entrySet().iterator();
                        while (chunkClaimEntrySets.hasNext()) {
                            Entry<Integer, ChunkClaimEntry> chunkClaimEntryWithIndex = chunkClaimEntrySets.next();
                            ChunkClaimEntry chunkClaimEntry = chunkClaimEntryWithIndex.getValue();
                            ChunkClaimInfo chunkClaimInfo = chunkClaimEntry.getChunkClaimInfo();
                            TownHallEntry townHallEntry;
                            Iterator<TownHallEntry> townHallEntryIterator;
                            
                            // First verify that all TownHallsEntries are still valid. Also note the HQ chunks.
                            townHallEntryIterator = chunkClaimEntry.getTownHallEntries().iterator();
                            boolean isFirstTownHall = true;
                            while (townHallEntryIterator.hasNext()) {
                                townHallEntry = townHallEntryIterator.next();
                                Block blockTownHall = world.getBlockState(townHallEntry.getPos()).getBlock();
                                boolean isRemoved = false;
                                if (!(blockTownHall instanceof BlockTownHall)) {
                                    // TownHall has been removed 
                                    townHallEntryIterator.remove();
                                    ChunkClaims.get(world).markDirty();
                                    isRemoved = true;
                                }
                                
                                if (isRemoved || ((TileTownHall)world.getTileEntity(townHallEntry.getPos())).isInactive()) {
                                    // town hall is removed, or found but inactive - unclaim chunks if it is the first town hall for this chunk
                                    if (isFirstTownHall) {
                                        for (int chunkX = chunkClaimInfo.getChunkX() - AWNPCStatics.townChunkClaimRadius; chunkX <= chunkClaimInfo.getChunkX() + AWNPCStatics.townChunkClaimRadius; chunkX++) {
                                            for (int chunkZ = chunkClaimInfo.getChunkZ() - AWNPCStatics.townChunkClaimRadius; chunkZ <= chunkClaimInfo.getChunkZ() + AWNPCStatics.townChunkClaimRadius; chunkZ++) {
                                                chunksToUnclaim.put(new Point(chunkX, chunkZ), townHallEntry.getOwnerName());
                                            }
                                        }
                                    }
                                }
                                isFirstTownHall = false;
                            }
                            
                            // loop over all Town Halls again, this time (re-)setting the claims
                            townHallEntryIterator = chunkClaimEntry.getTownHallEntries().iterator();
                            while (townHallEntryIterator.hasNext()) {
                                townHallEntry = townHallEntryIterator.next();
                                TileTownHall tileEntity = ((TileTownHall)world.getTileEntity(townHallEntry.getPos()ß));
                                if (tileEntity.isHq || !tileEntity.isInactive()) {
                                    for (int chunkX = chunkClaimInfo.getChunkX() - AWNPCStatics.townChunkClaimRadius; chunkX <= chunkClaimInfo.getChunkX() + AWNPCStatics.townChunkClaimRadius; chunkX++) {
                                        for (int chunkZ = chunkClaimInfo.getChunkZ() - AWNPCStatics.townChunkClaimRadius; chunkZ <= chunkClaimInfo.getChunkZ() + AWNPCStatics.townChunkClaimRadius; chunkZ++) {
                                            chunksToClaim.put(new Point(chunkX, chunkZ), townHallEntry.getOwnerName());
                                        }
                                    }
                                }
                            }
                        }
                        
                        // done walking through chunkClaimEntrySets, built our claim/unclaim maps...
                        // ... now check the claim/unclaim maps to decide what needs to be done
                        for (Entry<ChunkDimPos, String> chunkToUnclaimEntry : chunksToUnclaim.entrySet()) {
                            boolean shouldUnclaim = false;
                            if (chunksToClaim.containsKey(chunkToUnclaimEntry.getKey())) {
                                // this particular chunk is being unclaimed by one townhall and another is also requesting to claim
                                if (!chunksToClaim.get(chunkToUnclaimEntry.getKey()).equals(chunkToUnclaimEntry.getValue())) {
                                    // conflicting claim has a different owner so remove the old one 
                                    shouldUnclaim = true;
                                }
                            } else {
                                // no conflicting/overlapping claim at all, so still unclaim it
                                shouldUnclaim = true;
                            }
                            
                            if (shouldUnclaim) {
                                FTBUtilitiesAPI_Impl.INSTANCE.getClaimedChunks().getChunkOwner(chunkToUnclaimEntry.getKey()).
                                LMWorldServer.inst.getPlayer(chunkToUnclaimEntry.getValue()).unclaimChunk(dimId, chunkToUnclaimEntry.getKey().x, chunkToUnclaimEntry.getKey().y);
                            }
                        }
                        
                        for (Entry<ChunkDimPos, String> chunkToClaimEntry : chunksToClaim.entrySet()) {
                            LMWorldServer.inst.getPlayer(chunkToClaimEntry.getValue()).claimChunk(dimId, chunkToClaimEntry.getKey().x, chunkToClaimEntry.getKey().y);
                        }
                        
                    }
                }
                
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {}
            }
        } finally {
            // restore the original thread name
            Thread.currentThread().setName(orgThreadName);
            AncientWarfareCore.log.error("...ChunkClaimWorkerThread has stopped. Server going down or crashed?");
            
        }
    }

    public synchronized void enable() {
        IS_ENABLED = true;
        ChunkClaims.setStale();
        AncientWarfareCore.log.info("ChunkClaimWorkerThread enabled (resumed)!");
    }
    
    public synchronized void disable() {
        IS_ENABLED = false;
        ChunkClaims.setStale();
        AncientWarfareCore.log.info("ChunkClaimWorkerThread disabled (paused)!");
    }
}
