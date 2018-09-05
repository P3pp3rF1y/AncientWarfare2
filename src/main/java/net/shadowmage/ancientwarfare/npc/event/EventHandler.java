package net.shadowmage.ancientwarfare.npc.event;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class EventHandler {
	private Set<Predicate<EntityAIBase>> additionalHostileAIChecks = new HashSet<>();

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

	public static final EventHandler INSTANCE = new EventHandler();

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof NpcBase)
			return;
		if (!(event.getEntity() instanceof EntityCreature))
			return;
		if (!AncientWarfareNPC.statics.shouldEntityTargetNpcs(EntityList.getEntityString(event.getEntity())))
			return;
		if (AncientWarfareNPC.statics.autoTargetting) {
			// Use new "auto injection"
			EntityCreature entity = (EntityCreature) event.getEntity();

			// all mobs that can attack somebody should have targetTasks
			int targetTaskPriority = getHostileAIPriority(entity);
			if (targetTaskPriority != -1) {
				entity.targetTasks.addTask(targetTaskPriority, new EntityAINearestAttackableTarget<>(entity, NpcBase.class, 0, AncientWarfareNPC.statics.autoTargettingConfigLos, false,
						e -> e != null && !e.isPassive() && (!(e instanceof NpcFaction) || FactionRegistry.getFaction(((NpcFaction) e).getFaction()).isTarget(entity))));
				// add this entity to the internal list of hostile mobs, so NPC's know to fight it
				NpcAI.addHostileEntity(entity);
			}
		} else {
			// Legacy whitelist/manual method
			EntityCreature e = (EntityCreature) event.getEntity();
			e.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(e, NpcBase.class, 0, false, false, null));
		}
	}
}
