package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall.NpcDeathEntry;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NpcAIPlayerOwnedPriest extends NpcAI<NpcPlayerOwned> {

	private static final int UPDATE_FREQ = 200;
	private static final int RESURRECTION_TIME = 100;
	private int lastCheckTicks = -1;
	private NpcDeathEntry entryToRes;
	private int resurrectionDelay = 0;

	public NpcAIPlayerOwnedPriest(NpcPlayerOwned npc) {
		super(npc);
		this.setMutexBits(ATTACK + MOVE);
	}

	@Override
	public boolean shouldExecute() {
		if (!npc.getIsAIEnabled()) {
			return false;
		}
		return (lastCheckTicks == -1 || npc.ticksExisted - lastCheckTicks > UPDATE_FREQ) && npc.getTownHall().map(t -> !t.getDeathList().isEmpty()).orElse(false);
	}

	@Override
	public boolean shouldContinueExecuting() {
		if (!npc.getIsAIEnabled()) {
			return false;
		}
		return npc.getTownHall().isPresent() && entryToRes != null && !entryToRes.resurrected && entryToRes.beingResurrected;
	}

	@Override
	public void startExecuting() {
		List<NpcDeathEntry> list = npc.getTownHall().map(TileTownHall::getDeathList).orElse(Collections.emptyList());
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
		Optional<BlockPos> townHallPos = npc.getTownHallPosition();
		if (!townHallPos.isPresent()) {
			return;
		}
		BlockPos pos = townHallPos.get();
		double dist = npc.getDistanceSq(pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d);
		if (dist > AWNPCStatics.npcActionRange * AWNPCStatics.npcActionRange) {
			moveToPosition(pos, dist);
			resurrectionDelay = 0;
		} else {
			resurrectionDelay++;
			npc.swingArm(EnumHand.MAIN_HAND);
			if (resurrectionDelay > RESURRECTION_TIME) {
				resurrectionDelay = 0;
				resurrectTarget();
			}
		}
	}

	private void resurrectTarget() {
		NpcBase resdNpc = ItemNpcSpawner.createNpcFromItem(npc.world, entryToRes.stackToSpawn);
		entryToRes.beingResurrected = false;
		if (resdNpc != null) {
			if (!AWNPCStatics.persistOrdersOnDeath) {
				resdNpc.ordersStack = ItemStack.EMPTY;
				resdNpc.upkeepStack = ItemStack.EMPTY;
			}
			for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
				resdNpc.setItemStackToSlot(slot, ItemStack.EMPTY);
			}
			resdNpc.setShieldStack(ItemStack.EMPTY);
			resdNpc.setOwner(npc.getOwner());
			resdNpc.setHealth(resdNpc.getMaxHealth() / 2);
			resdNpc.setPositionAndRotation(npc.posX, npc.posY, npc.posZ, npc.rotationYaw, npc.rotationPitch);
			resdNpc.knockBack(npc, 0, 2 * npc.getRNG().nextDouble() - 1, 2 * npc.getRNG().nextDouble() - 1);
			resdNpc.motionY = 0;
			entryToRes.resurrected = npc.world.spawnEntity(resdNpc);
		}
		npc.getTownHall().ifPresent(TileTownHall::informViewers);
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
