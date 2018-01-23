package net.shadowmage.ancientwarfare.npc.event;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.Iterator;

public class EventHandler {
    private EventHandler() {}
    
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
            if (entity.targetTasks.taskEntries.size() > 0) {
                int targetTaskPriority = -1;
                Iterator taskEntriesIterator = entity.targetTasks.taskEntries.iterator();
                // Again search for a task to use for the same priority as our attack NPC task, but
                // look for "EntityAINearestAttackableTarget" instead which is for ranged units
                while (taskEntriesIterator.hasNext()) {
                    EntityAITaskEntry taskEntry = (EntityAITaskEntry) taskEntriesIterator.next();
                    if (taskEntry.action instanceof EntityAINearestAttackableTarget) {
                        if (((EntityAINearestAttackableTarget)taskEntry.action).targetClass == EntityPlayer.class) {
                            // TODO: Task-based exclusions?
                            targetTaskPriority = taskEntry.priority;
                        }
                    }
                }
                if (targetTaskPriority != -1) {
                    entity.targetTasks.addTask(targetTaskPriority, new EntityAINearestAttackableTarget<>(entity, NpcBase.class, 0, AncientWarfareNPC.statics.autoTargettingConfigLos, false, e -> !e.isPassive()));
                    // add this entity to the internal list of hostile mobs, so NPC's know to fight it
                    NpcAI.addHostileEntity(entity);
                    //System.out.println("Injected EntityAINearestAttackableTarget on " + EntityList.getEntityString(entity) + " @" + targetTaskPriority);
                }
            }
        } else {
            // Legacy whitelist/manual method
            EntityCreature e = (EntityCreature) event.getEntity();
            e.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(e, NpcBase.class, 0, false, false, null));
        }
    }

/* TODO any pathfinding fixes necessary in forge?
    @SubscribeEvent
    public void pathfinderCheckCanPathBlock(PathfinderCheckCanPathBlock event) {
        if (event.entity instanceof NpcBase) {
            if (event.block instanceof BlockDoor) {
                event.setResult(Result.ALLOW);
            }
        }
    }
    
    @SubscribeEvent
    public void pathfinderAvoidAdditionals(PathfinderAvoidAdditionalEvent event) {
        //System.out.println("Firing!");
        if (AWNPCStatics.pathfinderAvoidChests || AWNPCStatics.pathfinderAvoidFences || AWNPCStatics.getPathfinderAvoidCustomBlocks() != null) {
            World world = event.entity.world;
            BlockPos pos = event.getPos();
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (AWNPCStatics.pathfinderAvoidChests) {
                if (block.getRenderType() == 22 || block instanceof BlockChest) {
                    event.setResult(Result.DENY);
                    return;
                }
            }
            if (AWNPCStatics.pathfinderAvoidFences) {
                if (block.getRenderType() == 11 || block instanceof BlockFence || block instanceof BlockWall) {
                    event.setResult(Result.DENY);
                    return;
                }
            }
            if (AWNPCStatics.getPathfinderAvoidCustomBlocks() != null) {
                for ( BlockAndMeta blockAndMeta : AWNPCStatics.getPathfinderAvoidCustomBlocks() ) {
                    if ( Block.isEqualTo(blockAndMeta.block, block) ) {
                        if ( blockAndMeta.meta == -1 || blockAndMeta.meta == block.getMetaFromState(state) ) { //TODO can this avoid using meta?
                            event.setResult(Result.DENY);
                            return;
                        }
                    }
                }
            }
        }
    }
*/
}
