package net.shadowmage.ancientwarfare.core.gamedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;

public class ChunkClaims extends WorldSavedData {
    public static final String ID = "AW2_InteropFtbuChunkData";
    
    private Map<ChunkLocation, LinkedHashSet<TownHallOwner>> chunkClaims = new HashMap<ChunkLocation, LinkedHashSet<TownHallOwner>>();
    
    public ChunkClaims(String tagName) {
        super(tagName);
    }
    
    public ChunkClaims() {
        super(ID);
    }

    
    
    /*
    public synchronized LinkedHashSet<TownHallOwner> chunkClaimsGet(ChunkLocation chunkLocation) {
        return chunkClaims.get(chunkLocation);
    }
    
    public synchronized LinkedHashSet<TownHallOwner> chunkClaimsPut(ChunkLocation chunkLocation, LinkedHashSet<TownHallOwner> townHallOwners) {
        return chunkClaims.put(chunkLocation, townHallOwners);
    }
    
    public synchronized LinkedHashSet<TownHallOwner> chunkClaimsRemove(ChunkLocation chunkLocation) {
        return chunkClaims.remove(chunkLocation);
    }
    */

    @Override
    public void readFromNBT(NBTTagCompound nbtLoad) {
        chunkClaims.clear();
        NBTTagList chunkClaimsTag = nbtLoad.getTagList("ChunkClaims", NBT.TAG_COMPOUND);
        NBTTagCompound chuckClaimTag;
        String keyChunkLocation;
        String valueTownHallOwnerList;
        for (int i = 0; i < chunkClaimsTag.tagCount(); i++) {
            chuckClaimTag = chunkClaimsTag.getCompoundTagAt(i);
            
            // get the key (ChunkLocation) from String
            keyChunkLocation = chuckClaimTag.getString("keyChunkLocation");
            ChunkLocation thisChunk = ChunkLocation.fromString(keyChunkLocation);
            
            // get the value list (TownHallOwner's) from semicolon-delimitered String...
            valueTownHallOwnerList = chuckClaimTag.getString("valueTownHallOwnerList");
            List<String> townHallOwnerListRaw = Arrays.asList(valueTownHallOwnerList.split(";"));
            // ... and rebuild a TownHallOwner list
            LinkedHashSet<TownHallOwner> townHallOwners = new LinkedHashSet<TownHallOwner>();
            for (String townHallOwnerRaw : townHallOwnerListRaw)
                townHallOwners.add(TownHallOwner.fromString(townHallOwnerRaw));
            // all done, add the chunkclaim entry
            chunkClaims.put(thisChunk, townHallOwners);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtSave) {
        NBTTagList chunkClaimsTag = new NBTTagList();
        for(Map.Entry<ChunkLocation, LinkedHashSet<TownHallOwner>> entry : chunkClaims.entrySet()) {
            // convert the key (ChunkLocation) to String
            String keyChunkLocation = entry.getKey().toString();
            
            // convert the value list (TownHallOwner's) to semicolon-delimitered String
            List<String> townHallOwnerListRaw = new ArrayList<String>();
            for (TownHallOwner townHallOwners : entry.getValue()) {
                townHallOwnerListRaw.add(townHallOwners.toString());
            }
            String valueTownHallOwnerList = String.join(";", townHallOwnerListRaw);
            
            // all done, build and save NBT
            NBTTagCompound chuckClaimTag = new NBTTagCompound();
            chuckClaimTag.setString("keyChunkLocation", keyChunkLocation);
            chuckClaimTag.setString("valueTownHallOwnerList", valueTownHallOwnerList);
            chunkClaimsTag.appendTag(chuckClaimTag);
        }
        nbtSave.setTag("ChunkClaims", chunkClaimsTag);
    }

    public static class ChunkLocation {
        private int chunkX;
        private int chunkZ;
        private int dimensionId;
        
        public ChunkLocation(int chunkX, int chunkZ, int dimensionId) {
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
        
        public static ChunkLocation fromString(String chunkLocationRaw) {
            String[] split = chunkLocationRaw.split(",");
            return new ChunkLocation(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj == null || obj.getClass() != this.getClass())
                return false;
            ChunkLocation chunkLocationOther = (ChunkLocation) obj;
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
    
    public static class TownHallOwner {
        private String ownerName;
        private int posX;
        private int posY;
        private int posZ;
        
        public TownHallOwner(String ownerName, int posX, int posY, int posZ) {
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
        
        public static TownHallOwner fromString(String townHallOwnerRaw) {
            String[] split = townHallOwnerRaw.split(",");
            return new TownHallOwner(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj == null || obj.getClass() != this.getClass())
                return false;
            TownHallOwner townHallOther = (TownHallOwner) obj;
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
    
    public static ChunkClaims get(World world) {
        // mapStorage == one set of data for every world
        ChunkClaims data = (ChunkClaims) world.mapStorage.loadData(ChunkClaims.class, ID);
        if (data == null) {
            data = new ChunkClaims();
            world.setItemData(ID, data);
        }
        return data;
    }
}
