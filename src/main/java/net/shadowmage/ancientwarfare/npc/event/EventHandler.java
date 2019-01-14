package net.shadowmage.ancientwarfare.npc.event;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.npc.ai.AIHelper;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;
import net.shadowmage.ancientwarfare.npc.registry.NpcDefaultsRegistry;
import net.shadowmage.ancientwarfare.npc.registry.OwnedNpcDefault;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class EventHandler {
	private Set<Predicate<EntityAIBase>> additionalHostileAIChecks = new HashSet<>();
	private Set<String> modsCoveredByTargetLists = new HashSet<>();
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
			if ((taskEntry.action instanceof EntityAINearestAttackableTarget && ((EntityAINearestAttackableTarget) taskEntry.action).targetClass == EntityPlayer.class)
					|| additionalHostileAIChecks.stream().anyMatch(p -> p.test(taskEntry.action))) {
				if (taskEntry.priority != -1) {
					return taskEntry.priority;
				}
			}
		}
		return -1;
	}

	public void initModsCoveredByTargets() {
		for (String factionName : FactionRegistry.getFactionNames()) {
			FactionRegistry.getFaction(factionName).getTargetList().forEach(this::addTargetModToList);
		}

		for (OwnedNpcDefault npcDefault : NpcDefaultsRegistry.getOwnedNpcDefaults()) {
			npcDefault.getTargetList().forEach(this::addTargetModToList);
		}
	}

	private boolean addTargetModToList(String target) {
		return modsCoveredByTargetLists.add(target.split(":")[0]);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof NpcBase)
			return;
		if (!(event.getEntity() instanceof EntityCreature))
			return;
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

		if ((e instanceof NpcPlayerOwned) && NpcDefaultsRegistry.getOwnedNpcDefault((NpcPlayerOwned) e).isTarget(entity)) {
			return true;
		}

		EntityEntry entityEntry = EntityRegistry.getEntry(entity.getClass());
		if (entityEntry == null) {
			return false;
		}

		//noinspection ConstantConditions
		if (!modsCoveredByTargetLists.contains(entityEntry.getRegistryName().getResourcePath())) {
			AIHelper.addHostileEntityToTarget(entity);
			return true;
		}

		return false;
	}

	@SubscribeEvent
	public void onChestClicked(PlayerInteractEvent.RightClickBlock evt) {
		World world = evt.getWorld();
		BlockPos pos = evt.getPos();
		EntityPlayer player = evt.getEntityPlayer();
		if (!player.capabilities.isCreativeMode && !player.isSneaking() && isChest(world, pos)) {
			AWGameData.INSTANCE.getData(world, StructureMap.class).getStructureAt(world, pos).ifPresent(structure -> {
				for (NpcFaction factionNpc : world.getEntitiesWithinAABB(NpcFaction.class, structure.getBB().getAABB())) {
					if (!factionNpc.isPassive()) {
						evt.setCanceled(true);
						evt.setCancellationResult(EnumActionResult.FAIL);
						player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarenpc.no_chest_access",
								StringUtils.capitalize(factionNpc.getFaction())), true);
					}
				}
			});
		}
	}

	private boolean isChest(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		return block == Blocks.CHEST || block == AWStructureBlocks.ADVANCED_LOOT_CHEST;
	}
}
