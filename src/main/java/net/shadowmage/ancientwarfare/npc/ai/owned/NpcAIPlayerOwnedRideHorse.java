package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityHorse;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.ArrayList;
import java.util.List;

public class NpcAIPlayerOwnedRideHorse extends NpcAI {

    AttributeModifier followRangeModifier;
    AttributeModifier moveSpeedModifier;

    EntityHorse horse;
    List<EntityAITaskEntry> horseAI = new ArrayList<EntityAITaskEntry>();
    boolean saddled = false;

    public NpcAIPlayerOwnedRideHorse(NpcBase npc) {
        super(npc);
        this.moveSpeedModifier = new AttributeModifier("modifier.npc_ride_speed", 1.0d, 2);
        this.moveSpeedModifier.setSaved(false);
        this.followRangeModifier = new AttributeModifier("modifier.npc_horse_path_extension", 24.d, 0);
        this.followRangeModifier.setSaved(false);
    }

    public void onKilled() {
        if (horse != null) {
            onDismountHorse();
        }
        horse = null;
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

    @Override
    public boolean continueExecuting() {
        return horse != null;
    }

    @Override
    public void startExecuting() {

    }

    @Override
    public void updateTask() {
        if (horse != npc.ridingEntity && horse != null) {
            onDismountHorse();
            horse = null;
        }
    }

    @Override
    public void resetTask() {
        if (horse != null) {
            onDismountHorse();
        }
        horse = null;
    }

    private void onMountHorse() {
        this.saddled = horse.isHorseSaddled();
        removeHorseAI();
        horse.setHorseSaddled(false);
        applyModifiers();
    }

    private void onDismountHorse() {
        addHorseAI();
        removeModifiers();
        horse.setHorseSaddled(saddled);
    }

    private void applyModifiers() {
        horse.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(moveSpeedModifier);
        horse.getEntityAttribute(SharedMonsterAttributes.followRange).removeModifier(followRangeModifier);
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
        for (EntityAITaskEntry task : horseAI) {
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
