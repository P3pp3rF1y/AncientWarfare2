package net.shadowmage.ancientwarfare.core.gamedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;

public class ChunkClaims extends WorldSavedData {
    public static final String ID = "AW2_ChunkClaimData";
    
    /**
     *  DIMENSION_CHUNK_CLAIM_ENTRIES<Integer, LinkedHashMap<Integer, ChunkClaimEntry>>
     *      Integer [dimId]
     *      LinkedHashMap<Integer, ChunkClaimEntry>
     *          Integer [index]
     *          ChunkClaimEntry
     *              ChunkClaimInfo chunkClaimInfo;
     *                  int chunkX;
     *                  int chunkZ;
     *                  int dimensionId;
     *              LinkedHashSet<TownHallEntry> townHallEntries;
     *                  TownHallEntry
     *                      String ownerName
     *                      int posX
     *                      int posY
     *                      int posZ
     *              
     */
    
    protected Map<Integer, LinkedHashMap<Integer, ChunkClaimEntry>> DIMENSION_CHUNK_CLAIM_ENTRIES = new LinkedHashMap<Integer, LinkedHashMap<Integer, ChunkClaimEntry>>();
    
    public ChunkClaims(String tagName) {
        super(tagName);
    }
    
    public ChunkClaims() {
        super(ID);
    }

    public synchronized void addTownHallEntry(ChunkClaimInfo reqChunkClaimInfo, TownHallEntry newTownHallEntry) {
        // get ChunkClaimEntries collection for the requested dimension, if any
        LinkedHashMap<Integer, ChunkClaimEntry> chunkClaimEntries = DIMENSION_CHUNK_CLAIM_ENTRIES.get(reqChunkClaimInfo.getDimensionId());
        // make a new ChunkClaimEntry for this dimension if it wasn't found
        if (chunkClaimEntries == null)
            chunkClaimEntries = new LinkedHashMap<Integer, ChunkClaimEntry>();
        
        // find the ChunkClaimEntry for this ChunkClaimInfo, if any
        int chunkClaimEntryIndex = chunkClaimEntries.size();
        for (Entry<Integer, ChunkClaimEntry> chunkClaimEntryWithIndex : chunkClaimEntries.entrySet())
            if (chunkClaimEntryWithIndex.getValue().chunkClaimInfo == reqChunkClaimInfo)
                chunkClaimEntryIndex = chunkClaimEntryWithIndex.getKey();
        ChunkClaimEntry chunkClaimEntry = chunkClaimEntries.get(chunkClaimEntryIndex);
        // make a new chunkClaimEntry if it wasn't found
        if (chunkClaimEntry == null)
            chunkClaimEntry = new ChunkClaimEntry(reqChunkClaimInfo, new LinkedHashSet<TownHallEntry>());
        
        // add the provided TownHallEntry to this ChunkClaimEntry's townHallEntries collection
        chunkClaimEntry.townHallEntries.add(newTownHallEntry);
        // put our modified chunkClaimEntry back into the ChunkClaimEntry collection
        chunkClaimEntries.put(chunkClaimEntryIndex, chunkClaimEntry);
        // put the ChunkClaimEntry collection back into our master collection
        DIMENSION_CHUNK_CLAIM_ENTRIES.put(reqChunkClaimInfo.dimensionId, chunkClaimEntries);
        this.markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtLoad) {
        DIMENSION_CHUNK_CLAIM_ENTRIES.clear();
        NBTTagList chunkClaimsTag = nbtLoad.getTagList("DIMENSION_CHUNK_CLAIM_ENTRIES", NBT.TAG_COMPOUND);
        for (int i = 0; i < chunkClaimsTag.tagCount(); i++) {
            NBTTagCompound chuckClaimTag = chunkClaimsTag.getCompoundTagAt(i);
            
            // get the key (ChunkLocation) from String
            String savedChunkClaimEntryInfo = chuckClaimTag.getString("savedChunkClaimEntryInfo");
            ChunkClaimInfo chunkClaimEntryInfo = ChunkClaimInfo.fromString(savedChunkClaimEntryInfo);
            
            // get the value (TownHallEntry list as semicolon-delimitered string)
            String savedTownHallEntries = chuckClaimTag.getString("savedTownHallEntries");
            List<String> savedTownHallEntriesAsList = Arrays.asList(savedTownHallEntries.split(";"));
            
            // loop over the entries and add them as normal
            for (String savedTownHallEntry : savedTownHallEntriesAsList) {
                if (!savedTownHallEntry.isEmpty()) {
                    TownHallEntry townHallEntry = TownHallEntry.fromString(savedTownHallEntry);
                    addTownHallEntry(chunkClaimEntryInfo, townHallEntry);
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtSave) {
        NBTTagList chunkClaimsTag = new NBTTagList();
        for (Entry<Integer, LinkedHashMap<Integer, ChunkClaimEntry>> chunkClaimEntries : DIMENSION_CHUNK_CLAIM_ENTRIES.entrySet()) {
            // we don't care about the key (dimId) of our masterlist here, since it's already stored in each ChunkClaimInfo
            for (Entry<Integer, ChunkClaimEntry> chunkClaimEntryWithIndex : chunkClaimEntries.getValue().entrySet()) {
                ChunkClaimEntry chunkClaimEntry = chunkClaimEntryWithIndex.getValue();
                
                String savedChunkClaimEntryInfo = chunkClaimEntry.chunkClaimInfo.toString();
                List<String> savedTownHallEntriesAsList = new ArrayList<String>();
                for (TownHallEntry townHallEntry : chunkClaimEntry.townHallEntries) {
                    savedTownHallEntriesAsList.add(townHallEntry.toString());
                }
                String savedTownHallEntries = String.join(";", savedTownHallEntriesAsList);
                
                // saved data built, now build the actual NBT
                NBTTagCompound chuckClaimTag = new NBTTagCompound();
                chuckClaimTag.setString("savedChunkClaimEntryInfo", savedChunkClaimEntryInfo);
                chuckClaimTag.setString("savedTownHallEntries", savedTownHallEntries);
                chunkClaimsTag.appendTag(chuckClaimTag);
            }
        }
        nbtSave.setTag("DIMENSION_CHUNK_CLAIM_ENTRIES", chunkClaimsTag);
    }
    
    public class ChunkClaimEntry {
        private final ChunkClaimInfo chunkClaimInfo;
        private final LinkedHashSet<TownHallEntry> townHallEntries;
        
        public ChunkClaimEntry(ChunkClaimInfo chunkClaimInfo, LinkedHashSet<TownHallEntry> townHallEntries) {
            this.chunkClaimInfo = chunkClaimInfo;
            this.townHallEntries = townHallEntries;
        }

        public ChunkClaimInfo getChunkClaimInfo() {
            return chunkClaimInfo;
        }

        public LinkedHashSet<TownHallEntry> getTownHallEntries() {
            return townHallEntries;
        }
    }

    public static class ChunkClaimInfo {
        private int chunkX;
        private int chunkZ;
        private int dimensionId;
        
        public ChunkClaimInfo(int chunkX, int chunkZ, int dimensionId) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.dimensionId = dimensionId;
        }
        
        public int getChunkX() {
            return chunkX;
        }
        
        public int getChunkZ() {
            return chunkZ;
        }
        
        public int getDimensionId() {
            return dimensionId;
        }
        
        @Override
        public String toString() {
            return chunkX + "," + chunkZ + "," + dimensionId;
        }
        
        public static ChunkClaimInfo fromString(String chunkLocationRaw) {
            String[] split = chunkLocationRaw.split(",");
            return new ChunkClaimInfo(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj == null || obj.getClass() != this.getClass())
                return false;
            ChunkClaimInfo chunkLocationOther = (ChunkClaimInfo) obj;
            if (chunkLocationOther.chunkX != chunkX)
                return false;
            if (chunkLocationOther.chunkZ != chunkZ)
                return false;
            if (chunkLocationOther.dimensionId != dimensionId)
                return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + chunkX;
            result = prime * result + chunkZ;
            result = prime * result + dimensionId;
            return result;
        }
    }
    
    public static class TownHallEntry {
        private String ownerName;
        private int posX;
        private int posY;
        private int posZ;
        
        public TownHallEntry(String ownerName, int posX, int posY, int posZ) {
            this.ownerName = ownerName;
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public int getPosX() {
            return posX;
        }

        public int getPosY() {
            return posY;
        }

        public int getPosZ() {
            return posZ;
        }
        
        @Override
        public String toString() {
            return ownerName + "," + posX + "," + posY + "," + posZ;
        }
        
        public static TownHallEntry fromString(String townHallOwnerRaw) {
            String[] split = townHallOwnerRaw.split(",");
            return new TownHallEntry(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj == null || obj.getClass() != this.getClass())
                return false;
            TownHallEntry townHallOther = (TownHallEntry) obj;
            if (!townHallOther.ownerName.equals(ownerName))
                return false;
            if (townHallOther.posX != posX)
                return false;
            if (townHallOther.posY != posY)
                return false;
            if (townHallOther.posZ != posZ)
                return false;
            return true;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((ownerName == null) ? 0 : ownerName.hashCode());
            result = prime * result + posX;
            result = prime * result + posY;
            result = prime * result + posZ;
            return result;
        }
    }
    
    private static boolean IS_STALE = true;
    private static ChunkClaims INSTANCE;
    
    public static ChunkClaims get(World world) {
        if (IS_STALE) {
            // mapStorage == one set of data for all worlds
            INSTANCE = (ChunkClaims) world.mapStorage.loadData(ChunkClaims.class, ID);
            if (INSTANCE == null) {
                INSTANCE = new ChunkClaims();
                world.setItemData(ID, INSTANCE);
            }
            IS_STALE = false;
        }
        return INSTANCE;
    }
    
    @Override
    public void markDirty() {
        super.markDirty();
        setStale();
    }
    
    public static void setStale() {
        IS_STALE = true;
    }
    
    
    //public static 
}
