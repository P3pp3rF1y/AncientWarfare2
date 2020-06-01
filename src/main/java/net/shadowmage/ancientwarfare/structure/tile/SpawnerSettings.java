package net.shadowmage.ancientwarfare.structure.tile;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionLeader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionMounted;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionPriest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionSiegeEngineer;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFactionSoldier;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.util.CapabilityRespawnData;
import net.shadowmage.ancientwarfare.structure.util.IRespawnData;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

@SuppressWarnings("SpellCheckingInspection")
public class SpawnerSettings {
	private static final String RESPOND_TO_REDSTONE_TAG = "respondToRedstone";
	private static final String REDSTONE_MODE_TAG = "redstoneMode";
	private static final String PREV_REDSTONE_STATE_TAG = "prevRedstoneState";
	private static final String MIN_DELAY_TAG = "minDelay";
	private static final String MAX_DELAY_TAG = "maxDelay";
	private static final String SPAWN_DELAY_TAG = "spawnDelay";
	private static final String PLAYER_RANGE_TAG = "playerRange";
	private static final String MOB_RANGE_TAG = "mobRange";
	private static final String SPAWN_RANGE_TAG = "spawnRange";
	private static final String MAX_NEARBY_MONSTERS_TAG = "maxNearbyMonsters";
	private static final String XP_TO_DROP_TAG = "xpToDrop";
	private static final String LIGHT_SENSITIVE_TAG = "lightSensitive";
	private static final String TRANSPARENT_TAG = "transparent";
	private static final String DEBUG_MODE_TAG = "debugMode";
	private static final String SPAWN_GROUPS_TAG = "spawnGroups";
	private static final String INVENTORY_TAG = "inventory";
	private static final String HOSTILE_TAG = "hostile";
	private static final String FACTION_NAME_TAG = "factionName";
	private static final String CUSTOM_NAME_TAG = "CustomName";
	private List<EntitySpawnGroup> spawnGroups = new ArrayList<>();

	private ItemStackHandler inventory = new ItemStackHandler(9);

	private boolean debugMode;
	private boolean transparent;
	private boolean respondToRedstone;//should this spawner respond to redstone impulses
	private boolean redstoneMode;//false==toggle, true==pulse/tick to spawn
	private boolean prevRedstoneState;//used to cache the powered status from last tick, to compare to this tick

	private int playerRange;
	private int mobRange;
	private int range = 4;

	private int maxDelay = 20 * 20;
	private int minDelay = 20 * 10;

	private int spawnDelay = maxDelay;

	private int maxNearbyMonsters;

	private boolean lightSensitive;

	private int xpToDrop;

	private boolean isOneShotSpawner;
	private String factionName = "";

	float blockHardness = 2.f;

	/*
	 * fields for a 'fake' tile-entity...set from the real tile-entity when it has its
	 * world set (which is before first updateEntity() is called)
	 */
	private World world;
	private BlockPos pos;

	public boolean hasWorld() {
		return world != null;
	}

	public static SpawnerSettings getDefaultSettings() {
		SpawnerSettings settings = new SpawnerSettings();
		settings.playerRange = 16;
		settings.mobRange = 4;
		settings.maxNearbyMonsters = 8;

		EntitySpawnGroup group = new EntitySpawnGroup(settings);
		group.addSpawnSetting(new EntitySpawnSettings(group));
		settings.addSpawnGroup(group);

		return settings;
	}

	void setWorld(World world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
	}

	void onUpdate() {
		if (!respondToRedstone) {
			updateNormalMode();
		} else if (redstoneMode) {
			updateRedstoneModePulse();
		} else {
			updateRedstoneModeToggle();
		}
		if (spawnGroups.isEmpty()) {
			world.setBlockToAir(pos);
		}
	}

	private void updateRedstoneModeToggle() {
		prevRedstoneState = world.isBlockIndirectlyGettingPowered(pos) > 0 || world.getStrongPower(pos) > 0;
		if (respondToRedstone && !redstoneMode && !prevRedstoneState) {
			//noop
			return;
		}
		updateNormalMode();
	}

	private void updateRedstoneModePulse() {
		boolean powered = world.isBlockIndirectlyGettingPowered(pos) > 0 || world.getStrongPower(pos) > 0;
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
			int delayRange = maxDelay - minDelay;
			spawnDelay = minDelay + (delayRange <= 0 ? 0 : world.rand.nextInt(delayRange));
			spawnEntities();
		}
	}

	private void spawnEntities() {
		if (checkSpawnConditions()) {
			return;
		}

		int totalWeight = 0;
		for (EntitySpawnGroup group : this.spawnGroups)//count total weights
		{
			totalWeight += group.groupWeight;
		}
		int rand = totalWeight == 0 ? 0 : world.rand.nextInt(totalWeight);//select an object
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
			toSpawn.spawnEntities(world, pos, index, range);
			if (toSpawn.shouldRemove()) {
				spawnGroups.remove(toSpawn);
			}
		}
	}

	private boolean checkSpawnConditions() {
		if (checkLight()) {
			return true;
		}
		if (!checkPlayerConditions()) {
			return true;
		}

		return checkNearbyMobs();
	}

	private boolean checkLight() {
		if (lightSensitive) {
			int light = world.getBlockState(pos).getLightValue(world, pos);

			return light >= 8;
		}
		return false;
	}

	private boolean checkNearbyMobs() {
		if (maxNearbyMonsters > 0 && mobRange > 0) {
			int nearbyCount = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(mobRange, mobRange, mobRange)).size();
			if (nearbyCount >= maxNearbyMonsters) {
				AncientWarfareStructure.LOG.debug("skipping spawning because of too many nearby entities");
				return true;
			}
		}
		return false;
	}

	private boolean checkPlayerConditions() {
		if (playerRange > 0) {
			List<EntityPlayer> nearbyPlayers = getPlayersWithinAABB();
			if (nearbyPlayers.isEmpty()) {
				return false;
			}

			for (EntityPlayer player : nearbyPlayers) {
				if ((debugMode || (!player.isCreative() && !player.isSpectator())) && !isContinuousSpawnerOfFriendlyFaction(player)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isContinuousSpawnerOfFriendlyFaction(EntityPlayer player) {
		return !isOneShotSpawner && !factionName.isEmpty() && !FactionTracker.INSTANCE.isHostileToPlayer(world, player.getUniqueID(), player.getName(), factionName);
	}

	private List<EntityPlayer> getPlayersWithinAABB() {
		List<EntityPlayer> players = new ArrayList<>();

		for (EntityPlayer player : world.playerEntities) {
			if (player.getEntityBoundingBox().intersects(new AxisAlignedBB(pos, pos.add(1, 1, 1)).grow(playerRange, playerRange, playerRange))) {
				players.add(player);
			}
		}
		return players;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setBoolean(RESPOND_TO_REDSTONE_TAG, respondToRedstone);
		if (respondToRedstone) {
			tag.setBoolean(REDSTONE_MODE_TAG, redstoneMode);
			tag.setBoolean(PREV_REDSTONE_STATE_TAG, prevRedstoneState);
		}
		tag.setInteger(MIN_DELAY_TAG, minDelay);
		tag.setInteger(MAX_DELAY_TAG, maxDelay);
		tag.setInteger(SPAWN_DELAY_TAG, spawnDelay);
		tag.setInteger(PLAYER_RANGE_TAG, playerRange);
		tag.setInteger(MOB_RANGE_TAG, mobRange);
		tag.setInteger(SPAWN_RANGE_TAG, range);
		tag.setInteger(MAX_NEARBY_MONSTERS_TAG, maxNearbyMonsters);
		tag.setInteger(XP_TO_DROP_TAG, xpToDrop);
		tag.setBoolean(LIGHT_SENSITIVE_TAG, lightSensitive);
		tag.setBoolean(TRANSPARENT_TAG, transparent);
		tag.setBoolean(DEBUG_MODE_TAG, debugMode);
		NBTTagList groupList = new NBTTagList();
		NBTTagCompound groupTag;
		for (EntitySpawnGroup group : this.spawnGroups) {
			groupTag = new NBTTagCompound();
			group.writeToNBT(groupTag);
			groupList.appendTag(groupTag);
		}
		tag.setTag(SPAWN_GROUPS_TAG, groupList);

		tag.setTag(INVENTORY_TAG, inventory.serializeNBT());

		return tag;
	}

	public void readFromNBT(NBTTagCompound tag) {
		spawnGroups.clear();
		respondToRedstone = tag.getBoolean(RESPOND_TO_REDSTONE_TAG);
		if (respondToRedstone) {
			redstoneMode = tag.getBoolean(REDSTONE_MODE_TAG);
			prevRedstoneState = tag.getBoolean(PREV_REDSTONE_STATE_TAG);
		}
		minDelay = Math.max(tag.getInteger(MIN_DELAY_TAG), 10);
		maxDelay = Math.max(tag.getInteger(MAX_DELAY_TAG), 10);
		spawnDelay = tag.getInteger(SPAWN_DELAY_TAG);
		playerRange = tag.getInteger(PLAYER_RANGE_TAG);
		mobRange = tag.getInteger(MOB_RANGE_TAG);
		range = tag.getInteger(SPAWN_RANGE_TAG);
		maxNearbyMonsters = tag.getInteger(MAX_NEARBY_MONSTERS_TAG);
		xpToDrop = tag.getInteger(XP_TO_DROP_TAG);
		lightSensitive = tag.getBoolean(LIGHT_SENSITIVE_TAG);
		transparent = tag.getBoolean(TRANSPARENT_TAG);
		debugMode = tag.getBoolean(DEBUG_MODE_TAG);
		NBTTagList groupList = tag.getTagList(SPAWN_GROUPS_TAG, Constants.NBT.TAG_COMPOUND);
		EntitySpawnGroup group;
		for (int i = 0; i < groupList.tagCount(); i++) {
			group = new EntitySpawnGroup(this);
			group.readFromNBT(groupList.getCompoundTagAt(i));
			spawnGroups.add(group);
		}
		if (tag.hasKey(INVENTORY_TAG)) {
			inventory.deserializeNBT(tag.getCompoundTag(INVENTORY_TAG));
		}

		updateSpawnProperties();
	}

	void updateSpawnProperties() {
		if (world == null || world.isRemote) {
			return;
		}

		isOneShotSpawner = false;
		factionName = "";
		if (spawnGroups.size() == 1 && spawnGroups.get(0).entitiesToSpawn.size() == 1) {
			EntitySpawnSettings entitySettings = spawnGroups.get(0).entitiesToSpawn.get(0);
			if (entitySettings.maxToSpawn == 1 && entitySettings.minToSpawn == 1 && entitySettings.remainingSpawnCount == 1) {
				isOneShotSpawner = true;
			}
			Entity entity = EntityList.createEntityByIDFromName(entitySettings.entityId, world);
			factionName = entity instanceof NpcFaction ? entitySettings.customTag.getString(FACTION_NAME_TAG) : "";
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

	public final void toggleLightSensitive() {
		this.lightSensitive = !lightSensitive;
	}

	public final boolean isRespondToRedstone() {
		return respondToRedstone;
	}

	public final void toggleRespondToRedstone() {
		this.respondToRedstone = !respondToRedstone;
	}

	public final boolean getRedstoneMode() {
		return redstoneMode;
	}

	public final void toggleRedstoneMode() {
		this.redstoneMode = !redstoneMode;
	}

	public final int getPlayerRange() {
		return playerRange;
	}

	public final void setPlayerRange(int playerRange) {
		this.playerRange = playerRange;
	}

	public final int getMobRange() {
		return mobRange;
	}

	public final void setMobRange(int mobRange) {
		this.mobRange = mobRange;
	}

	public final int getSpawnRange() {
		return this.range;
	}

	public final void setSpawnRange(int range) {
		this.range = range;
	}

	public final int getMaxDelay() {
		return maxDelay;
	}

	public final void setMaxDelay(int maxDelay) {
		if (minDelay > maxDelay)
			minDelay = maxDelay;
		this.maxDelay = maxDelay;
	}

	public final int getMinDelay() {
		return minDelay;
	}

	public final void setMinDelay(int minDelay) {
		if (minDelay > maxDelay)
			maxDelay = minDelay;
		this.minDelay = minDelay;
	}

	public final int getSpawnDelay() {
		return spawnDelay;
	}

	public final void setSpawnDelay(int spawnDelay) {
		if (spawnDelay > maxDelay)
			maxDelay = spawnDelay;
		if (spawnDelay < minDelay)
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

	public final IItemHandler getInventory() {
		return inventory;
	}

	public final boolean isDebugMode() {
		return debugMode;
	}

	public final void toggleDebugMode() {
		debugMode = !debugMode;
	}

	public final boolean isTransparent() {
		return transparent;
	}

	public final void toggleTransparent() {
		this.transparent = !transparent;
	}

	public void setPos(BlockPos posIn) {
		this.pos = posIn;
	}

	public static boolean spawnsHostileNpcs(SpawnerSettings spawnerSettings) {
		return spawnerSettings.spawnsEntity(spawnerSettings::isHostileNpc);
	}

	private static final Set<Class<? extends Entity>> HOSTILE_NPC_CLASS_TYPES = ImmutableSet.of(
			NpcFactionLeader.class, NpcFactionPriest.class, NpcFactionArcher.class, NpcFactionSiegeEngineer.class, NpcFactionMounted.class, NpcFactionSoldier.class
	);

	private boolean isHostileNpc(Class<? extends Entity> entityClass) {
		return HOSTILE_NPC_CLASS_TYPES.stream().anyMatch(entityClass::isAssignableFrom);
	}

	private boolean spawnsEntity(Predicate<Class<? extends Entity>> isEntityOfType) {
		List<EntitySpawnGroup> groups = getSpawnGroups();
		if (groups.isEmpty()) {
			return false;
		}
		EntitySpawnGroup firstGroup = groups.get(0);
		List<EntitySpawnSettings> spawnEntities = firstGroup.getEntitiesToSpawn();
		if (spawnEntities.isEmpty()) {
			return false;
		}

		EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(spawnEntities.get(0).getEntityId());

		return entityEntry != null && isEntityOfType.test(entityEntry.getEntityClass());
	}

	public static final class EntitySpawnGroup {
		private int groupWeight = 1;
		private List<EntitySpawnSettings> entitiesToSpawn = new ArrayList<>();
		private SpawnerSettings settings;

		public EntitySpawnGroup(SpawnerSettings settings) {
			this.settings = settings;
		}

		public SpawnerSettings getParentSettings() {
			return settings;
		}

		public void setWeight(int weight) {
			this.groupWeight = weight <= 0 ? 1 : weight;
		}

		public void addSpawnSetting(EntitySpawnSettings setting) {
			entitiesToSpawn.add(setting);
		}

		private void spawnEntities(World world, BlockPos spawnPos, int grpIndex, int range) {
			Iterator<EntitySpawnSettings> it = entitiesToSpawn.iterator();
			int index = 0;
			EntitySpawnSettings entitySpawnSettings;
			while (it.hasNext() && (entitySpawnSettings = it.next()) != null) {
				entitySpawnSettings.spawnEntities(world, spawnPos, range);
				if (entitySpawnSettings.shouldRemove()) {
					it.remove();
				}

				int a1 = 0;
				int b2 = entitySpawnSettings.remainingSpawnCount;
				int a = (a1 << 16) | (grpIndex & 0x0000ffff);
				int b = (index << 16) | (b2 & 0x0000ffff);
				world.addBlockEvent(spawnPos, AWStructureBlocks.ADVANCED_SPAWNER, a, b);
				index++;
			}
		}

		private boolean shouldRemove() {
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
				setting = new EntitySpawnSettings(this);
				setting.readFromNBT(settingsList.getCompoundTagAt(i));
				if (!setting.shouldRemove()) {
					this.entitiesToSpawn.add(setting);
				}
			}
		}
	}

	public static final class EntitySpawnSettings {
		private static final String ENTITY_ID_TAG = "entityId";
		private static final String CUSTOM_TAG = "customTag";
		private static final String MIN_TO_SPAWN_TAG = "minToSpawn";
		private static final String MAX_TO_SPAWN_TAG = "maxToSpawn";
		private static final String REMAINING_SPAWN_COUNT_TAG = "remainingSpawnCount";
		private static final String FACTION_NAME_TAG = SpawnerSettings.FACTION_NAME_TAG;
		private static final String CUSTOM_NAME_TAG = SpawnerSettings.CUSTOM_NAME_TAG;
		private ResourceLocation entityId = new ResourceLocation("pig");
		private NBTTagCompound customTag;
		private int minToSpawn = 2;
		private int maxToSpawn = 4;
		int remainingSpawnCount = -1;
		private boolean hostile = true;
		private EntitySpawnGroup group;

		public EntitySpawnSettings(EntitySpawnGroup group) {
			this.group = group;
		}

		private EntitySpawnGroup getParentSettings() {
			return group;
		}

		public final void writeToNBT(NBTTagCompound tag) {
			tag.setBoolean(HOSTILE_TAG, hostile);
			tag.setString(ENTITY_ID_TAG, entityId.toString());
			if (customTag != null) {
				tag.setTag(CUSTOM_TAG, customTag);
			}
			tag.setInteger(MIN_TO_SPAWN_TAG, minToSpawn);
			tag.setInteger(MAX_TO_SPAWN_TAG, maxToSpawn);
			tag.setInteger(REMAINING_SPAWN_COUNT_TAG, remainingSpawnCount);
		}

		public final void readFromNBT(NBTTagCompound tag) {
			hostile = !tag.hasKey(HOSTILE_TAG) || tag.getBoolean(HOSTILE_TAG);
			remainingSpawnCount = tag.getInteger(REMAINING_SPAWN_COUNT_TAG);
			setEntityToSpawn(new ResourceLocation(tag.getString(ENTITY_ID_TAG)));
			if (tag.hasKey(CUSTOM_TAG)) {
				customTag = tag.getCompoundTag(CUSTOM_TAG);
			}
			minToSpawn = tag.getInteger(MIN_TO_SPAWN_TAG);
			maxToSpawn = tag.getInteger(MAX_TO_SPAWN_TAG);
		}

		@SuppressWarnings("ConstantConditions")
		public final void setEntityToSpawn(Entity entity) {
			hostile = !(entity instanceof EntityAgeable);
			ResourceLocation registryName = EntityRegistry.getEntry(entity.getClass()).getRegistryName();
			setEntityToSpawn(registryName);
		}

		public final void setEntityToSpawn(ResourceLocation entityId) {
			this.entityId = entityId;
			if (!ForgeRegistries.ENTITIES.containsKey(this.entityId)) {
				if (hostile) {
					AncientWarfareStructure.LOG.debug("{} is not a valid entityId.  Spawner default to Zombie.", entityId);
					this.entityId = new ResourceLocation("zombie");
				} else {
					remainingSpawnCount = 0;
				}
			}
			if (AWStructureStatics.excludedSpawnerEntities.contains(this.entityId.toString())) {
				if (hostile) {
					AncientWarfareStructure.LOG.warn("{} has been set as an invalid entity for spawners!  Spawner default to Zombie.", entityId);
					this.entityId = new ResourceLocation("zombie");
				} else {
					remainingSpawnCount = 0;
				}
			}
		}

		public final void setCustomSpawnTag(@Nullable NBTTagCompound tag) {
			this.customTag = tag;
		}

		public final void setSpawnCountMin(int min) {
			this.minToSpawn = min;
		}

		public final void setSpawnCountMax(int max) {
			this.maxToSpawn = Math.max(minToSpawn, max);
		}

		public final void setSpawnLimitTotal(int total) {
			this.remainingSpawnCount = total;
		}

		private boolean shouldRemove() {
			return remainingSpawnCount == 0;
		}

		public final ResourceLocation getEntityId() {
			return entityId;
		}

		public final String getEntityName() {
			if (customTag != null && customTag.hasKey(FACTION_NAME_TAG)) {
				return EntityTools.getUnlocName(entityId).replace("faction", getCustomTag().getString(FACTION_NAME_TAG));
			}
			return EntityTools.getUnlocName(entityId);
		}

		public final String getCustomNameOrEntityName() {
			if (customTag != null && customTag.hasKey(CUSTOM_NAME_TAG)) {
				return customTag.getString(CUSTOM_NAME_TAG);
			}
			return getEntityName();
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
			int toSpawn;
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

		private void spawnEntities(World world, BlockPos spawnPos, int range) {
			int toSpawn = getNumToSpawn(world.rand);

			for (int i = 0; i < toSpawn; i++) {
				Entity e = EntityList.createEntityByIDFromName(entityId, world);
				if (e == null)
					return;
				boolean doSpawn = findAndSetSpawnLocation(world, spawnPos, range, e);
				if (doSpawn) {
					spawnEntityAt(e, world);
					if (remainingSpawnCount > 0) {
						remainingSpawnCount--;
					}
				}
			}
		}

		private boolean findAndSetSpawnLocation(World world, BlockPos spawnPos, int range, Entity e) {
			int spawnTry = 0;
			while (spawnTry < range + 5) {
				int x = spawnPos.getX() - range + world.rand.nextInt(range * 2 + 1);
				int z = spawnPos.getZ() - range + world.rand.nextInt(range * 2 + 1);
				for (int y = spawnPos.getY() - range; y <= spawnPos.getY() + range; y++) {
					e.setLocationAndAngles(x + 0.5d, y, z + 0.5d, world.rand.nextFloat() * 360, 0);
					if (range == 0 || checkEntityIsNotColliding(e)) {
						return true;
					}
				}
				spawnTry++;
			}
			return false;
		}

		private boolean checkEntityIsNotColliding(Entity e) {
			return e.world.getCollisionBoxes(e, e.getEntityBoundingBox()).isEmpty() && e.world.checkNoEntityCollision(e.getEntityBoundingBox(), e);
		}

		private static final Method CAN_DESPAWN = ObfuscationReflectionHelper.findMethod(EntityLiving.class, "func_70692_ba", boolean.class);

		private boolean canDespawn(Entity e) {
			if (!(e instanceof EntityLiving)) {
				return true;
			}

			try {
				return (boolean) CAN_DESPAWN.invoke(e);
			}
			catch (IllegalAccessException | InvocationTargetException ex) {
				AncientWarfareStructure.LOG.error("Error calling canDespawn on entity: ", ex);
			}
			return true;
		}

		private void spawnEntityAt(Entity e, World world) {
			if (e instanceof EntityLiving) {
				((EntityLiving) e).onInitialSpawn(world.getDifficultyForLocation(e.getPosition()), null);
				((EntityLiving) e).spawnExplosionParticle();
			}
			setDataFromTag(e); //some data needs to be set before spawning entity in the world (like factionName)
			world.spawnEntity(e);
			setDataFromTag(e); //and some data needs to be set after onInitialSpawn fires for entity]
			if (e instanceof NpcFaction) {
				((NpcFaction) e).setCanDespawn();
			}
			if (getParentSettings().getParentSettings().isOneShotSpawner && canDespawn(e)) {
				setRespawnData(e);
			}
		}

		@SuppressWarnings("ConstantConditions")
		private void setRespawnData(Entity e) {
			if (e.hasCapability(CapabilityRespawnData.RESPAWN_DATA_CAPABILITY, null)) {
				IRespawnData respawnData = e.getCapability(CapabilityRespawnData.RESPAWN_DATA_CAPABILITY, null);
				respawnData.setRespawnPos(e.getPosition());
				respawnData.setSpawnerSettings(getParentSettings().getParentSettings().writeToNBT(new NBTTagCompound()));
				respawnData.setSpawnTime(e.world.getTotalWorldTime());
			}
		}

		private void setDataFromTag(Entity e) {
			if (customTag != null) {
				NBTTagCompound temp = new NBTTagCompound();
				e.writeToNBT(temp);
				Set<String> keys = customTag.getKeySet();
				for (String key : keys) {
					temp.setTag(key, customTag.getTag(key));
				}
				e.readFromNBT(temp);
				if (e instanceof NpcFaction && customTag.hasKey(FACTION_NAME_TAG)) {
					((NpcFaction) e).setFactionNameAndDefaults(customTag.getString(FACTION_NAME_TAG));
				}
			}
		}
	}
}
