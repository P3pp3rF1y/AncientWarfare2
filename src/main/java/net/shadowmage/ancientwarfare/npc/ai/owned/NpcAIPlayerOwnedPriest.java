package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall.NpcDeathEntry;

import java.util.List;

public class NpcAIPlayerOwnedPriest extends NpcAI {

    private static final int UPDATE_FREQ = 200, RESURRECTION_TIME = 100;
    int lastCheckTicks = -1;
    NpcDeathEntry entryToRes;
    int resurrectionDelay = 0;

    public NpcAIPlayerOwnedPriest(NpcBase npc) {
        super(npc);
        this.setMutexBits(ATTACK + MOVE);
    }

    @Override
    public boolean shouldExecute() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        return (lastCheckTicks == -1 || npc.ticksExisted - lastCheckTicks > UPDATE_FREQ) && npc.getTownHall() != null && !npc.getTownHall().getDeathList().isEmpty();
    }

    @Override
    public boolean continueExecuting() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        return npc.getTownHall() != null && entryToRes != null && !entryToRes.resurrected && entryToRes.beingResurrected;
    }

    @Override
    public void startExecuting() {
        List<NpcDeathEntry> list = npc.getTownHall().getDeathList();
        for (NpcDeathEntry entry : list) {
            if (entry.canRes && !entry.resurrected && !entry.beingResurrected) {
                this.entryToRes = entry;
                entry.beingResurrected = true;
                break;
            }
        }
    }

    @Override
    public void updateTask() {
        if (entryToRes == null || entryToRes.resurrected) {
            return;
        }
        BlockPosition pos = npc.getTownHallPosition();
        double dist = npc.getDistanceSq(pos.x + 0.5d, pos.y, pos.z + 0.5d);
        if (dist > ACTION_RANGE) {
            moveToPosition(pos, dist);
            resurrectionDelay = 0;
        } else {
            resurrectionDelay++;
            npc.swingItem();
            if (resurrectionDelay > RESURRECTION_TIME) {
                resurrectionDelay = 0;
                resurrectTarget();
            }
        }
    }

    protected void resurrectTarget() {
        entryToRes.resurrected = true;
        entryToRes.beingResurrected = false;
        npc.getTownHall().informViewers();
        ItemStack stack = entryToRes.stackToSpawn;
        NpcBase resdNpc = ItemNpcSpawner.createNpcFromItem(npc.worldObj, stack);
        if (resdNpc != null) {
            resdNpc.ordersStack = null;
            resdNpc.upkeepStack = null;
            for (int i = 0; i < 5; i++) {
                resdNpc.setCurrentItemOrArmor(i, null);
            }
            resdNpc.setShieldStack(null);
            resdNpc.setHealth(resdNpc.getMaxHealth() / 2);
            resdNpc.setPositionAndRotation(npc.posX, npc.posY, npc.posZ, npc.rotationYaw, npc.rotationPitch);
            npc.worldObj.spawnEntityInWorld(resdNpc);
        }
        entryToRes = null;
    }

    @Override
    public void resetTask() {
        if (entryToRes != null && !entryToRes.resurrected) {
            entryToRes.beingResurrected = false;
        }
        entryToRes = null;
    }

}
