package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityHorse;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Olivier on 15/06/2015.
 */
public class NpcAIRideHorse extends NpcAI{
    private final AttributeModifier followRangeModifier;
    private final AttributeModifier moveSpeedModifier;

    protected EntityHorse horse;
    List<EntityAITasks.EntityAITaskEntry> horseAI = new ArrayList<EntityAITasks.EntityAITaskEntry>();

    public NpcAIRideHorse(NpcBase npc, double speedFactor) {
        super(npc);
        this.moveSpeedModifier = new AttributeModifier("modifier.npc_ride_speed", speedFactor, 2);
        this.moveSpeedModifier.setSaved(false);
        this.followRangeModifier = new AttributeModifier("modifier.npc_horse_path_extension", 24.d, 0);
        this.followRangeModifier.setSaved(false);
    }

    @Override
    public boolean shouldExecute() {
        if (horse == null) {
            if (npc.ridingEntity instanceof EntityHorse) {
                horse = (EntityHorse) npc.ridingEntity;
                onMountHorse();
                return true;
            }
        }
        return false;
    }

    protected void onMountHorse() {
        removeHorseAI();
        horse.setHorseSaddled(false);
        applyModifiers();
    }

    public void onKilled() {
        if (horse != null) {
            onDismountHorse();
        }
        horse = null;
    }

    protected void onDismountHorse() {
        addHorseAI();
        horse.setHorseSaddled(true);
        removeModifiers();
    }

    private void applyModifiers() {
        removeModifiers();
        horse.getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(moveSpeedModifier);
        horse.getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(followRangeModifier);
    }

    private void removeModifiers() {
        horse.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(moveSpeedModifier);
        horse.getEntityAttribute(SharedMonsterAttributes.followRange).removeModifier(followRangeModifier);
    }

    @SuppressWarnings("unchecked")
    private void removeHorseAI() {
        horseAI.clear();
        horseAI.addAll(horse.tasks.taskEntries);
        for (EntityAITasks.EntityAITaskEntry task : horseAI) {
            horse.tasks.removeTask(task.action);
        }
    }

    @SuppressWarnings("unchecked")
    private void addHorseAI() {
        if (horse.tasks.taskEntries.isEmpty()) {
            horse.tasks.taskEntries.addAll(horseAI);
        }
        horseAI.clear();
    }
}
