package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.List;

public class NpcAIPlayerOwnedCommander extends NpcAI<NpcBase> {

    protected int lastExecuted = -1;

    private final IEntitySelector selector;

    public NpcAIPlayerOwnedCommander(NpcBase npc) {
        super(npc);
        selector = new IEntitySelector() {
            @Override
            public boolean isEntityApplicable(Entity var1) {
                if (var1 instanceof NpcBase) {
                    NpcBase e = (NpcBase) var1;
                    if (canBeCommanded(e) && !isCommander(e) && !e.isPotionActive(Potion.damageBoost)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    protected boolean canBeCommanded(NpcBase npc){
        return npc.canBeCommandedBy(this.npc.getOwnerName());
    }

    @Override
    public boolean shouldExecute() {
        if (!npc.getIsAIEnabled() || !isCommander(npc)) {
            return false;
        }
        return (lastExecuted == -1 || npc.ticksExisted - lastExecuted > 40);//Wait 2 seconds
    }

    @Override
    public boolean continueExecuting() {
        return false;
    }

    protected boolean isCommander(NpcBase npc) {
        return npc.getNpcSubType().equals("commander");
    }


    @SuppressWarnings("unchecked")
    @Override
    public void startExecuting() {
        lastExecuted = npc.ticksExisted;
        double dist = npc.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
        AxisAlignedBB bb = npc.boundingBox.expand(dist, dist / 2, dist);
        List<NpcBase> potentialTargets = npc.worldObj.selectEntitiesWithinAABB(NpcBase.class, bb, selector);
        for(NpcBase npcBase : potentialTargets){
            npcBase.addPotionEffect(new PotionEffect(Potion.damageBoost.getId(), 20));//apply 1 second strength potion, times 1.3 damage
        }
    }

    @Override
    public void updateTask() {
    }

    @Override
    public void resetTask() {
    }

}
