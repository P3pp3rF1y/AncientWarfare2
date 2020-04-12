package net.shadowmage.ancientwarfare.npc.event;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.core.util.Zone;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;
import net.shadowmage.ancientwarfare.npc.registry.NpcDefaultsRegistry;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureEntry;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;
import net.shadowmage.ancientwarfare.structure.tile.ISpecialLootContainer;
import net.shadowmage.ancientwarfare.structure.tile.TileProtectionFlag;
import net.shadowmage.ancientwarfare.structure.util.CapabilityRespawnData;
import net.shadowmage.ancientwarfare.structure.util.ConquerHelper;
import net.shadowmage.ancientwarfare.structure.util.IRespawnData;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class EventHandler {
	public static final String NO_SPAWN_PREVENTION_TAG = "noSpawnPrevention";
	private Set<Predicate<EntityAIBase>> additionalHostileAIChecks = new HashSet<>();
	public static final EventHandler INSTANCE = new EventHandler();

	private EventHandler() {
	}

	public void registerAdditionalHostileAICheck(Predicate<EntityAIBase> aiCheck) {
		additionalHostileAIChecks.add(aiCheck);
	}

	private int getHostileAIPriority(EntityCreature entity) {
		// Again search for a task to use for the same priority as our attack NPC task, but
		// look for "EntityAINearestAttackableTarget" instead which is for ranged units
		for (EntityAITaskEntry taskEntry : entity.targetTasks.taskEntries) {
			if (((taskEntry.action instanceof EntityAINearestAttackableTarget && ((EntityAINearestAttackableTarget) taskEntry.action).targetClass == EntityPlayer.class)
					|| anyAdditionalHostileAIChecksMatch(taskEntry)) && taskEntry.priority != -1) {
				return taskEntry.priority;
			}
		}
		return -1;
	}

	private boolean anyAdditionalHostileAIChecksMatch(EntityAITaskEntry taskEntry) {
		for (Predicate<EntityAIBase> additionalCheck : additionalHostileAIChecks) {
			if (additionalCheck.test(taskEntry.action)) {
				return true;
			}
		}
		return false;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		injectAi(event);
		preventHostileSpawnsInStructures(event);
	}

	private static final Cache<Zone, Set<StructureEntry>> CHUNK_STRUCTURE_ENTRIES = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();
	private static final Cache<StructureBB, Boolean> STRUCTURE_BB_CONQUERED = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).build();

	private void preventHostileSpawnsInStructures(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		if (entity.getEntityWorld().isRemote || !IMob.MOB_SELECTOR.apply(entity) || isMarkedWithNoPreventionTag(entity)) {
			return;
		}

		World world = event.getWorld();

		BlockPos pos = event.getEntity().getPosition();

		Set<StructureEntry> structures = getStructuresInChunk(world, pos);

		for (StructureEntry entry : structures) {
			if (entry.getBB().contains(pos) && entry.shouldPreventHostileNaturalSpawns() && (entry.hasProtectionFlag() || checkNotConquered(world, entry))) {
				event.setCanceled(true);
			}
		}
	}

	private boolean checkNotConquered(World world, StructureEntry entry) {
		boolean conquered = entry.getConquered();
		if (!conquered) {
			try {
				conquered = STRUCTURE_BB_CONQUERED.get(entry.getBB(), () -> ConquerHelper.checkBBConquered(world, entry.getBB()));
			}
			catch (ExecutionException e) {
				AncientWarfareNPC.LOG.error("Error getting conquered structureBB info ", e);
				return false;
			}
		}

		if (conquered) {
			entry.setConquered();
		}

		return !conquered;
	}

	private Set<StructureEntry> getStructuresInChunk(World world, BlockPos pos) {
		Set<StructureEntry> structures;
		ChunkPos chunkPos = new ChunkPos(pos);
		BlockPos min = new BlockPos(chunkPos.x * 16, 1, chunkPos.z * 16);
		BlockPos max = new BlockPos(chunkPos.x * 16 + 15, 255, chunkPos.z * 16 + 15);
		Zone chunkZone = new Zone(min, max);
		try {
			structures = CHUNK_STRUCTURE_ENTRIES.get(chunkZone, () -> AWGameData.INSTANCE.getData(world, StructureMap.class).getStructuresIn(world, chunkZone));
		}
		catch (ExecutionException e) {
			AncientWarfareNPC.LOG.error("Error getting structure entries in chunk for hostile entity check: ", e);
			return new HashSet<>();
		}
		return structures;
	}

	private boolean isMarkedWithNoPreventionTag(Entity entity) {
		if (entity.getTags().contains(NO_SPAWN_PREVENTION_TAG)) {
			return true;
		}
		if (entity.hasCapability(CapabilityRespawnData.RESPAWN_DATA_CAPABILITY, null)) {
			IRespawnData respawnData = entity.getCapability(CapabilityRespawnData.RESPAWN_DATA_CAPABILITY, null);

			//noinspection ConstantConditions
			return respawnData.canRespawn();
		}
		return false;
	}

	private void injectAi(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof NpcBase) {
			return;
		}
		if (!(event.getEntity() instanceof EntityCreature)) {
			return;
		}
		// Use new "auto injection"
		EntityCreature entity = (EntityCreature) event.getEntity();

		// all mobs that can attack somebody should have targetTasks
		int targetTaskPriority = getHostileAIPriority(entity);
		if (targetTaskPriority != -1) {
			entity.targetTasks.addTask(targetTaskPriority, new EntityAINearestAttackableTarget<>(entity, NpcBase.class, 0, true, false,
					e -> entityShouldTarget(entity, e)
			));
		}
	}

	private boolean entityShouldTarget(EntityCreature entity, @Nullable NpcBase e) {
		if (e == null) {
			return false;
		}

		if ((e instanceof NpcFaction) && !e.isPassive() && FactionRegistry.getFaction(((NpcFaction) e).getFaction()).isTarget(entity)) {
			return true;
		}

		return (e instanceof NpcPlayerOwned) && NpcDefaultsRegistry.getOwnedNpcDefault((NpcPlayerOwned) e).isTarget(entity);

	}

	@SubscribeEvent
	public void onChestClicked(PlayerInteractEvent.RightClickBlock evt) {
		World world = evt.getWorld();
		BlockPos pos = evt.getPos();
		EntityPlayer player = evt.getEntityPlayer();
		if (!player.capabilities.isCreativeMode && isContainer(world, pos)) {
			AWGameData.INSTANCE.getData(world, StructureMap.class).getStructureAt(world, pos).ifPresent(structure -> {
				Optional<TileProtectionFlag> tile = WorldTools.getTile(world, structure.getProtectionFlagPos(), TileProtectionFlag.class);
				if (tile.isPresent() && tile.get().shouldProtectAgainst(player)) {
					evt.setCanceled(true);
					evt.setCancellationResult(EnumActionResult.FAIL);
					if (world.isRemote) {
						player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarenpc.no_chest_access_flag_not_claimed"), true);
					}
				} else {
					for (NpcFaction factionNpc : world.getEntitiesWithinAABB(NpcFaction.class, structure.getBB().getAABB())) {
						if (!factionNpc.isPassive()) {
							evt.setCanceled(true);
							evt.setCancellationResult(EnumActionResult.FAIL);
							factionNpc.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 200));
							if (world.isRemote) {
								player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarenpc.no_chest_access",
										StringUtils.capitalize(factionNpc.getFaction())), true);
							}
							return;
						}
					}
				}
			});
		}
	}

	@SubscribeEvent
	public void playerDigSpeed(PlayerEvent.BreakSpeed evt) {
		if (evt.getEntityPlayer().isCreative() || evt.getEntityPlayer().isSpectator()) {
			return;
		}

		World world = evt.getEntityPlayer().world;
		AWGameData.INSTANCE.getData(world, StructureMap.class).getStructureAt(world, evt.getPos())
				.flatMap(structureEntry -> WorldTools.getTile(world, structureEntry.getProtectionFlagPos(), TileProtectionFlag.class)).ifPresent(tile -> {
			if (tile.shouldProtectAgainst(evt.getEntityPlayer()) && shouldBlockSlowDownDigging(world, evt.getPos())) {
				evt.setNewSpeed(evt.getOriginalSpeed() * 0.01f);
			}
		});
	}

	private boolean shouldBlockSlowDownDigging(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (!state.isFullBlock()) {
			return isContainer(world, pos);
		}

		Block block = state.getBlock();
		return !(block == Blocks.MOB_SPAWNER || block == AWStructureBlocks.ADVANCED_SPAWNER);
	}

	private boolean isContainer(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		return block == Blocks.CHEST || (state.getBlock().hasTileEntity(state) && WorldTools.getTile(world, pos, ISpecialLootContainer.class).isPresent());
	}
}
