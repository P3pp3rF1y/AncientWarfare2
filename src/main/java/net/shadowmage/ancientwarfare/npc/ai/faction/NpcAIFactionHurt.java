package net.shadowmage.ancientwarfare.npc.ai.faction;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

import javax.annotation.Nullable;

public class NpcAIFactionHurt extends EntityAIHurtByTarget {

	private Predicate<Entity> targetSelector;

	public NpcAIFactionHurt(NpcFaction npc, @Nullable final Predicate<Entity> targetSelector) {
		super(npc, true);
		this.targetSelector = targetSelector;
	}

	@Override
	protected boolean isSuitableTarget(@Nullable EntityLivingBase target, boolean unused) {
		return targetSelector.apply(target);
	}

}
