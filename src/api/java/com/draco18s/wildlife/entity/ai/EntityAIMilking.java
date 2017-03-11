package com.draco18s.wildlife.entity.ai;

import com.draco18s.wildlife.entity.CowStats;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityCow;

/*
 * Skeleton class
 */
public class EntityAIMilking extends EntityAIBase {
	
	
	public void startExecuting() {
		
	}
	
	@Override
	public boolean shouldExecute() {
		return true;
	}
	
	@Override
	public void updateTask() {
		
	}
	
	public static void sendUpdatePacket(EntityCow self, CowStats stats) {
		
	}

	public boolean getMilkable() {
		return true;
	}
	
	public void doMilking() {
		
	}
}
