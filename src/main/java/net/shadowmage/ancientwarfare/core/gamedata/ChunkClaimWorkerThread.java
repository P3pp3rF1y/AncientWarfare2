package net.shadowmage.ancientwarfare.core.gamedata;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import ftb.utils.world.LMPlayerServer;
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
                    // iterate over DIMENSION_CHUNK_CLAIM_ENTRIES every cycle
                    Iterator<Entry<Integer, LinkedHashMap<Integer, ChunkClaimEntry>>> chunkClaimEntriesIterator = ChunkClaims.get(DimensionManager.getWorld(0)).DIMENSION_CHUNK_CLAIM_ENTRIES.entrySet().iterator();
                    while (chunkClaimEntriesIterator.hasNext()) {
                        Entry<Integer, LinkedHashMap<Integer, ChunkClaimEntry>> thisEntry = chunkClaimEntriesIterator.next();
                        // ensure the dimension for this ChunkClaimEntry map is loaded, skip it if not
                        int dimId = thisEntry.getKey();
                        World world = DimensionManager.getWorld(dimId);
                        if (world == null)
                            continue;
                        
                        //LinkedHashSet<ChunkClaimInfo> claimsToRemove = new LinkedHashSet<ChunkClaimInfo>();
                        
                        // iterate over ChunkClaimEntry sets
                        Iterator<Entry<Integer, ChunkClaimEntry>> chunkClaimEntrySets = thisEntry.getValue().entrySet().iterator();
                        while (chunkClaimEntrySets.hasNext()) {
                            Entry<Integer, ChunkClaimEntry> chunkClaimEntryWithIndex = chunkClaimEntrySets.next();
                            ChunkClaimEntry chunkClaimEntry = chunkClaimEntryWithIndex.getValue();
                            ChunkClaimInfo chunkClaimInfo = chunkClaimEntry.getChunkClaimInfo();
                            LMPlayerServer lmPlayerServer;
                            TownHallEntry townHallEntry;
                            Iterator<TownHallEntry> townHallEntryIterator;
                            
                            // First verify that all TownHallsEntries are still valid
                            townHallEntryIterator = chunkClaimEntry.getTownHallEntries().iterator();
                            boolean isFirstTownHall = true;
                            while (townHallEntryIterator.hasNext()) {
                                townHallEntry = townHallEntryIterator.next();
                                Block blockTownHall = world.getBlock(townHallEntry.getPosX(), townHallEntry.getPosY(), townHallEntry.getPosZ());
                                boolean isRemoved = false;
                                if (!(blockTownHall instanceof BlockTownHall)) {
                                    // TownHall has been removed 
                                    townHallEntryIterator.remove();
                                    ChunkClaims.get(world).markDirty();
                                    isRemoved = true;
                                }
                                
                                if (isRemoved || ((TileTownHall)world.getTileEntity(townHallEntry.getPosX(), townHallEntry.getPosY(), townHallEntry.getPosZ())).isInactive()) {
                                    // town hall is removed, or found but inactive - unclaim chunks if it is the first town hall for this chunk
                                    if (isFirstTownHall) {
                                        lmPlayerServer = LMWorldServer.inst.getPlayer(townHallEntry.getOwnerName());
                                        for (int chunkX = chunkClaimInfo.getChunkX() - AWNPCStatics.townChunkClaimRadius; chunkX <= chunkClaimInfo.getChunkX() + AWNPCStatics.townChunkClaimRadius; chunkX++) {
                                            for (int chunkZ = chunkClaimInfo.getChunkZ() - AWNPCStatics.townChunkClaimRadius; chunkZ <= chunkClaimInfo.getChunkZ() + AWNPCStatics.townChunkClaimRadius; chunkZ++) {
                                                lmPlayerServer.unclaimChunk(chunkClaimInfo.getDimensionId(), chunkX, chunkZ);
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
                                if (!((TileTownHall)world.getTileEntity(townHallEntry.getPosX(), townHallEntry.getPosY(), townHallEntry.getPosZ())).isInactive()) {
                                    lmPlayerServer = LMWorldServer.inst.getPlayer(townHallEntry.getOwnerName());
                                    for (int chunkX = chunkClaimInfo.getChunkX() - AWNPCStatics.townChunkClaimRadius; chunkX <= chunkClaimInfo.getChunkX() + AWNPCStatics.townChunkClaimRadius; chunkX++) {
                                        for (int chunkZ = chunkClaimInfo.getChunkZ() - AWNPCStatics.townChunkClaimRadius; chunkZ <= chunkClaimInfo.getChunkZ() + AWNPCStatics.townChunkClaimRadius; chunkZ++) {
                                            lmPlayerServer.claimChunk(chunkClaimInfo.getDimensionId(), chunkX, chunkZ);
                                        }
                                    }
                                }
                            }
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
