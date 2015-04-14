package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SpawnerSettings {

    List<EntitySpawnGroup> spawnGroups = new ArrayList<EntitySpawnGroup>();

    private InventoryBasic inventory = new InventoryBasic(9);

    boolean debugMode;
    boolean transparent;
    boolean respondToRedstone;//should this spawner respond to redstone impulses
    boolean redstoneMode;//false==toggle, true==pulse/tick to spawn
    boolean prevRedstoneState;//used to cache the powered status from last tick, to compare to this tick

    int playerRange;
    int mobRange;
    int range = 4;

    int maxDelay = 20 * 20;
    int minDelay = 20 * 10;

    int spawnDelay = maxDelay;

    int maxNearbyMonsters;

    boolean lightSensitive;

    int xpToDrop;

    float blockHardness = 2.f;


    /**
     * fields for a 'fake' tile-entity...set from the real tile-entity when it has its
     * world set (which is before first updateEntity() is called)
     */
    public World worldObj;
    int xCoord;
    int yCoord;
    int zCoord;

    public SpawnerSettings() {

    }

    public static SpawnerSettings getDefaultSettings() {
        SpawnerSettings settings = new SpawnerSettings();
        settings.maxDelay = 20 * 20;
        settings.minDelay = 10 * 20;
        settings.playerRange = 16;
        settings.mobRange = 4;
        settings.maxNearbyMonsters = 8;
        settings.respondToRedstone = false;

        EntitySpawnGroup group = new EntitySpawnGroup();
        group.groupWeight = 1;
        settings.addSpawnGroup(group);

        EntitySpawnSettings entity = new EntitySpawnSettings();
        entity.setEntityToSpawn("Pig");
        entity.setSpawnCountMin(2);
        entity.setSpawnCountMax(4);
        entity.remainingSpawnCount = -1;
        group.addSpawnSetting(entity);

        return settings;
    }

    public void setWorld(World world, int x, int y, int z) {
        this.worldObj = world;
        this.xCoord = x;
        this.yCoord = y;
        this.zCoord = z;
    }

    public void onUpdate() {
        if (!respondToRedstone) {
            updateNormalMode();
        } else if (redstoneMode) {
            updateRedstoneModePulse();
        } else {
            updateRedstoneModeToggle();
        }
        if (spawnGroups.isEmpty()) {
            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        }
    }

    private void updateRedstoneModeToggle() {
        prevRedstoneState = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) || worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) > 0;
        if (respondToRedstone && !redstoneMode && !prevRedstoneState) {
            //noop
            return;
        }
        updateNormalMode();
    }

    private void updateRedstoneModePulse() {
        boolean powered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) || worldObj.getBlockPowerInput(xCoord, yCoord, zCoord) > 0;
        if (!prevRedstoneState && powered) {
            spawnEntities();
        }
        prevRedstoneState = powered;
    }

    private void updateNormalMode() {
        if (spawnDelay > 0) {
            spawnDelay--;
        }
        if (spawnDelay <= 0) {
            int range = maxDelay - minDelay;
            spawnDelay = minDelay + (range <= 0 ? 0 :  worldObj.rand.nextInt(range));
            spawnEntities();
        }
    }

    @SuppressWarnings("unchecked")
    private void spawnEntities() {
        if (lightSensitive) {
            int light = worldObj.getBlockLightValue(xCoord, yCoord, zCoord);

            //TODO check this light calculation stuff...
            if (light >= 8) {
                return;
            }
        }
        if (playerRange > 0) {
            List<EntityPlayer> nearbyPlayers = worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(playerRange, playerRange, playerRange));
            if (nearbyPlayers.isEmpty()) {
                return;
            }
            boolean doSpawn = false;
            for (EntityPlayer player : nearbyPlayers) {
                if (!debugMode && player.capabilities.isCreativeMode) {
                    continue;
                }//iterate until a single non-creative mode player is found
                doSpawn = true;
                break;
            }
            if (!doSpawn) {
                return;
            }
        }

        if (maxNearbyMonsters > 0 && mobRange > 0) {
            int nearbyCount = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(mobRange, mobRange, mobRange)).size();
            if (nearbyCount >= maxNearbyMonsters) {
                AWLog.logDebug("skipping spawning because of too many nearby entities");
                return;
            }
        }

        int totalWeight = 0;
        for (EntitySpawnGroup group : this.spawnGroups)//count total weights
        {
            totalWeight += group.groupWeight;
        }
        int rand = totalWeight == 0 ? 0 : worldObj.rand.nextInt(totalWeight);//select an object
        int check = 0;
        EntitySpawnGroup toSpawn = null;
        int index = 0;
        for (EntitySpawnGroup group : this.spawnGroups)//iterate to find selected object
        {
            check += group.groupWeight;
            if (rand < check)//object found, break
            {
                toSpawn = group;
                break;
            }
            index++;
        }

        if (toSpawn != null) {
            toSpawn.spawnEntities(worldObj, xCoord, yCoord, zCoord, index, range);
            if (toSpawn.shouldRemove()) {
                spawnGroups.remove(toSpawn);
            }
        }
    }

    public void writeToNBT(NBTTagCompound tag) {
        tag.setBoolean("respondToRedstone", respondToRedstone);
        if (respondToRedstone) {
            tag.setBoolean("redstoneMode", redstoneMode);
            tag.setBoolean("prevRedstoneState", prevRedstoneState);
        }
        tag.setInteger("minDelay", minDelay);
        tag.setInteger("maxDelay", maxDelay);
        tag.setInteger("spawnDelay", spawnDelay);
        tag.setInteger("playerRange", playerRange);
        tag.setInteger("mobRange", mobRange);
        tag.setInteger("spawnRange", range);
        tag.setInteger("maxNearbyMonsters", maxNearbyMonsters);
        tag.setInteger("xpToDrop", xpToDrop);
        tag.setBoolean("lightSensitive", lightSensitive);
        tag.setBoolean("transparent", transparent);
        tag.setBoolean("debugMode", debugMode);
        NBTTagList groupList = new NBTTagList();
        NBTTagCompound groupTag;
        for (EntitySpawnGroup group : this.spawnGroups) {
            groupTag = new NBTTagCompound();
            group.writeToNBT(groupTag);
            groupList.appendTag(groupTag);
        }
        tag.setTag("spawnGroups", groupList);

        NBTTagCompound invTag = new NBTTagCompound();
        inventory.writeToNBT(invTag);
        tag.setTag("inventory", invTag);
    }

    public void readFromNBT(NBTTagCompound tag) {
        spawnGroups.clear();
        respondToRedstone = tag.getBoolean("respondToRedstone");
        if (respondToRedstone) {
            redstoneMode = tag.getBoolean("redstoneMode");
            prevRedstoneState = tag.getBoolean("prevRedstoneState");
        }
        minDelay = tag.getInteger("minDelay");
        maxDelay = tag.getInteger("maxDelay");
        spawnDelay = tag.getInteger("spawnDelay");
        playerRange = tag.getInteger("playerRange");
        mobRange = tag.getInteger("mobRange");
        range = tag.getInteger("spawnRange");
        maxNearbyMonsters = tag.getInteger("maxNearbyMonsters");
        xpToDrop = tag.getInteger("xpToDrop");
        lightSensitive = tag.getBoolean("lightSensitive");
        transparent = tag.getBoolean("transparent");
        debugMode = tag.getBoolean("debugMode");
        NBTTagList groupList = tag.getTagList("spawnGroups", Constants.NBT.TAG_COMPOUND);
        EntitySpawnGroup group;
        for (int i = 0; i < groupList.tagCount(); i++) {
            group = new EntitySpawnGroup();
            group.readFromNBT(groupList.getCompoundTagAt(i));
            spawnGroups.add(group);
        }
        if (tag.hasKey("inventory")) {
            inventory.readFromNBT(tag.getCompoundTag("inventory"));
        }
    }

    public void addSpawnGroup(EntitySpawnGroup group) {
        spawnGroups.add(group);
    }

    public List<EntitySpawnGroup> getSpawnGroups() {
        return spawnGroups;
    }

    public final boolean isLightSensitive() {
        return lightSensitive;
    }

    public final void setLightSensitive(boolean lightSensitive) {
        this.lightSensitive = lightSensitive;
    }

    public final boolean isRespondToRedstone() {
        return respondToRedstone;
    }

    public final void setRespondToRedstone(boolean respondToRedstone) {
        this.respondToRedstone = respondToRedstone;
    }

    public final boolean getRedstoneMode() {
        return redstoneMode;
    }

    public final void setRedstoneMode(boolean redstoneMode) {
        this.redstoneMode = redstoneMode;
    }

    public final int getPlayerRange() {
        return playerRange;
    }

    public final void setPlayerRange(int playerRange) {
        this.playerRange = playerRange;
    }

    public final int getMobRange(){
        return mobRange;
    }

    public final void setMobRange(int mobRange){
        this.mobRange = mobRange;
    }

    public final int getSpawnRange(){
        return this.range;
    }

    public final void setSpawnRange(int range){
        this.range = range;
    }

    public final int getMaxDelay() {
        return maxDelay;
    }

    public final void setMaxDelay(int maxDelay) {
        if(minDelay>maxDelay)
            minDelay = maxDelay;
        this.maxDelay = maxDelay;
    }

    public final int getMinDelay() {
        return minDelay;
    }

    public final void setMinDelay(int minDelay) {
        if(minDelay>maxDelay)
            maxDelay = minDelay;
        this.minDelay = minDelay;
    }

    public final int getSpawnDelay() {
        return spawnDelay;
    }

    public final void setSpawnDelay(int spawnDelay) {
        if(spawnDelay>maxDelay)
            maxDelay = spawnDelay;
        if(spawnDelay<minDelay)
            minDelay = spawnDelay;
        this.spawnDelay = spawnDelay;
    }

    public final int getMaxNearbyMonsters() {
        return maxNearbyMonsters;
    }

    public final void setMaxNearbyMonsters(int maxNearbyMonsters) {
        this.maxNearbyMonsters = maxNearbyMonsters;
    }

    public final void setXpToDrop(int xp) {
        this.xpToDrop = xp;
    }

    public final void setBlockHardness(float hardness) {
        this.blockHardness = hardness;
    }

    public final int getXpToDrop() {
        return xpToDrop;
    }

    public final float getBlockHardness() {
        return blockHardness;
    }

    public final InventoryBasic getInventory() {
        return inventory;
    }

    public final boolean isDebugMode() {
        return debugMode;
    }

    public final void setDebugMode(boolean mode) {
        debugMode = mode;
    }

    public final boolean isTransparent() {
        return transparent;
    }

    public final void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    public static final class EntitySpawnGroup {
        private int groupWeight;
        List<EntitySpawnSettings> entitiesToSpawn = new ArrayList<EntitySpawnSettings>();

        public EntitySpawnGroup() {

        }

        public void setWeight(int weight) {
            if (weight <= 0) {
                weight = 1;
            }
            this.groupWeight = weight;
        }

        public void addSpawnSetting(EntitySpawnSettings setting) {
            entitiesToSpawn.add(setting);
        }

        public void spawnEntities(World world, int x, int y, int z, int grpIndex, int range) {
            EntitySpawnSettings settings;
            Iterator<EntitySpawnSettings> it = entitiesToSpawn.iterator();
            int index = 0;
            while (it.hasNext() && (settings = it.next()) != null) {
                settings.spawnEntities(world, x, y, z, range);
                if (settings.shouldRemove()) {
                    it.remove();
                }

                int a1 = 0;
                int b2 = settings.remainingSpawnCount;
                int a = (a1 << 16) | (grpIndex & 0x0000ffff);
                int b = (index << 16) | (b2 & 0x0000ffff);
                world.addBlockEvent(x, y, z, AWBlocks.advancedSpawner, a, b);
                index++;
            }
        }

        public boolean shouldRemove() {
            return entitiesToSpawn.isEmpty();
        }

        public List<EntitySpawnSettings> getEntitiesToSpawn() {
            return entitiesToSpawn;
        }

        public int getWeight() {
            return groupWeight;
        }

        public void writeToNBT(NBTTagCompound tag) {
            tag.setInteger("groupWeight", groupWeight);
            NBTTagList settingsList = new NBTTagList();

            NBTTagCompound settingTag;
            for (EntitySpawnSettings setting : this.entitiesToSpawn) {
                settingTag = new NBTTagCompound();
                setting.writeToNBT(settingTag);
                settingsList.appendTag(settingTag);
            }
            tag.setTag("settingsList", settingsList);
        }

        public void readFromNBT(NBTTagCompound tag) {
            groupWeight = tag.getInteger("groupWeight");
            NBTTagList settingsList = tag.getTagList("settingsList", Constants.NBT.TAG_COMPOUND);
            EntitySpawnSettings setting;
            for (int i = 0; i < settingsList.tagCount(); i++) {
                setting = new EntitySpawnSettings();
                setting.readFromNBT(settingsList.getCompoundTagAt(i));
                this.entitiesToSpawn.add(setting);
            }
        }
    }

    public static final class EntitySpawnSettings {
        String entityId = "Pig";
        NBTTagCompound customTag;
        int minToSpawn = 1;
        int maxToSpawn = 4;
        int remainingSpawnCount = -1;

        public EntitySpawnSettings() {

        }

        public EntitySpawnSettings(String entityId) {
            setEntityToSpawn(entityId);
        }

        public final void writeToNBT(NBTTagCompound tag) {
            tag.setString("entityId", entityId);
            if (customTag != null) {
                tag.setTag("customTag", customTag);
            }
            tag.setInteger("minToSpawn", minToSpawn);
            tag.setInteger("maxToSpawn", maxToSpawn);
            tag.setInteger("remainingSpawnCount", remainingSpawnCount);
        }

        public final void readFromNBT(NBTTagCompound tag) {
            setEntityToSpawn(tag.getString("entityId"));
            if (tag.hasKey("customTag")) {
                customTag = tag.getCompoundTag("customTag");
            }
            minToSpawn = tag.getInteger("minToSpawn");
            maxToSpawn = tag.getInteger("maxToSpawn");
            remainingSpawnCount = tag.getInteger("remainingSpawnCount");
        }

        public final void setEntityToSpawn(String entityId) {
            this.entityId = entityId;
            if (!EntityList.stringToClassMapping.containsKey(this.entityId)) {
                AWLog.logError(entityId + " is not a valid entityId.  Spawner default to Zombie.");
                this.entityId = "Zombie";
            }
            if (AWStructureStatics.excludedSpawnerEntities.contains(this.entityId)) {
                AWLog.logError(entityId + " has been set as an invalid entity for spawners!  Spawner default to Zombie.");
                this.entityId = "Zombie";
            }
        }

        public final void setCustomSpawnTag(NBTTagCompound tag) {
            this.customTag = tag;
        }

        public final void setSpawnCountMin(int min) {
            this.minToSpawn = min;
        }

        public final void setSpawnCountMax(int max) {
            if(minToSpawn<max)
                this.maxToSpawn = max;
            else
                this.maxToSpawn = this.minToSpawn;
        }

        public final void setSpawnLimitTotal(int total) {
            this.remainingSpawnCount = total;
        }

        private boolean shouldRemove() {
            return remainingSpawnCount == 0;
        }

        public final String getEntityId() {
            return entityId;
        }

        public final String getEntityName(){
            return "entity." + entityId + ".name";
        }

        public final int getSpawnMin() {
            return minToSpawn;
        }

        public final int getSpawnMax() {
            return maxToSpawn;
        }

        public final int getSpawnTotal() {
            return remainingSpawnCount;
        }

        public final NBTTagCompound getCustomTag() {
            return customTag;
        }

        private int getNumToSpawn(Random rand) {
            int randRange = maxToSpawn - minToSpawn;
            int toSpawn = 0;
            if (randRange <= 0) {
                toSpawn = minToSpawn;
            } else {
                toSpawn = minToSpawn + rand.nextInt(randRange);
            }
            if (remainingSpawnCount >= 0 && toSpawn > remainingSpawnCount) {
                toSpawn = remainingSpawnCount;
            }
            return toSpawn;
        }

        private void spawnEntities(World world, int xCoord, int yCoord, int zCoord, int range) {
            int toSpawn = getNumToSpawn(world.rand);

            for (int i = 0; i < toSpawn; i++) {
                Entity e = EntityList.createEntityByName(entityId, world);
                if (e != null) {
                    if (customTag != null) {
                        e.readFromNBT(customTag);
                    }
                }else
                    return;
                boolean doSpawn = false;
                int spawnTry = 0;
                while (!doSpawn && spawnTry < range + 5) {
                    int x = xCoord - range + world.rand.nextInt(range*2+1);
                    int z = zCoord - range + world.rand.nextInt(range*2+1);
                    for (int y = yCoord - range; y <= yCoord + range; y++) {
                        e.setLocationAndAngles(x + 0.5d, y, z + 0.5d, world.rand.nextFloat() * 360, 0);
                        if (e instanceof EntityLiving) {
                            doSpawn = ((EntityLiving) e).getCanSpawnHere();
                            if(doSpawn)
                                break;
                        }else{
                            doSpawn = true;
                            break;
                        }
                    }
                    spawnTry++;
                }
                if (doSpawn) {
                    spawnEntityAt(e, world);
                    if (remainingSpawnCount > 0) {
                        remainingSpawnCount--;
                    }
                }
            }
        }

        //  sendSoundPacket(world, xCoord, yCoord, zCoord);
        //TODO
        private void spawnEntityAt(Entity e, World world) {
            if(e instanceof EntityLiving){
                ((EntityLiving)e).onSpawnWithEgg(null);
                ((EntityLiving) e).spawnExplosionParticle();
            }
            world.spawnEntityInWorld(e);
        }

    }

}
