package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIRideHorse;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIFactionRideHorse extends NpcAIRideHorse {

    private boolean wasHorseKilled = false;

    public NpcAIFactionRideHorse(NpcBase npc) {
        super(npc, 1.5);
    }

    @Override
    public boolean shouldExecute() {
        return !wasHorseKilled && (npc.ridingEntity == null || horse != npc.ridingEntity);
    }

    @Override
    public void startExecuting() {
        if (horse == null && !wasHorseKilled) {
            if (npc.ridingEntity instanceof EntityHorse) {
                horse = (EntityHorse) npc.ridingEntity;
            } else {
                spawnHorse();
            }
        } else if (horse != null && horse.isDead) {
            wasHorseKilled = true;
            horse = null;
        }
    }

    private void spawnHorse() {
        EntityHorse horse = new EntityHorse(npc.worldObj);
        horse.setLocationAndAngles(npc.posX, npc.posY, npc.posZ, npc.rotationYaw, npc.rotationPitch);
        do {
            horse.setHorseType(0);
            horse.setGrowingAge(0);
            horse.onSpawnWithEgg(null);
        }while (horse.getHorseType()!=0 || horse.isChild());
        horse.setHorseTamed(true);
        this.horse = horse;
        npc.worldObj.spawnEntityInWorld(horse);
        npc.mountEntity(horse);
        onMountHorse();
    }

    public void readFromNBT(NBTTagCompound tag) {
        wasHorseKilled = tag.getBoolean("wasHorseKilled");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setBoolean("wasHorseKilled", wasHorseKilled);
        return tag;
    }

}
