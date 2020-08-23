package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcWorker;

import java.util.List;
import java.util.Optional;

public class NpcAIPlayerOwnedFindWorksite extends NpcAI<NpcWorker> {

	private int lastExecuted = -1;//set to -1 default to trigger should execute lookup on first run
	private static final int CHECK_FREQUENCY = 200;//how often to recheck if orders and work target are both null
	private static final int RANGE = 40;

	public NpcAIPlayerOwnedFindWorksite(NpcWorker npc) {
		super(npc);
	}

	@Override
	public boolean shouldExecute() {
		if (!super.shouldExecute()) {
			return false;
		}
		return (lastExecuted == -1 || npc.ticksExisted - lastExecuted > CHECK_FREQUENCY) && npc.ordersStack.isEmpty() && npc.autoWorkTarget == null;
	}

	@Override
	public void startExecuting() {
		lastExecuted = npc.ticksExisted;
		if (npc.autoWorkTarget != null)//validate existing position
		{
			BlockPos pos = npc.autoWorkTarget;
			Optional<IWorkSite> te = WorldTools.getTile(npc.world, pos, IWorkSite.class);
			if (te.isPresent()) {
				IWorkSite site = te.get();
				if (!npc.canWorkAt(site.getWorkType()) || npc.hasCommandPermissions(site.getOwner()) || !site.hasWork()) {
					npc.autoWorkTarget = null;
				}
			} else {
				npc.autoWorkTarget = null;
			}
		}
		if (npc.autoWorkTarget == null) {
			findWorkTarget();
		}
	}

	private void findWorkTarget() {
		int x = MathHelper.floor(npc.posX);
		int y = MathHelper.floor(npc.posY);
		int z = MathHelper.floor(npc.posZ);
		List<TileEntity> tiles = WorldTools.getTileEntitiesInArea(npc.world, x - RANGE, y - RANGE / 2, z - RANGE, x + RANGE, y + RANGE / 2, z + RANGE);
		if (tiles.isEmpty()) {
			return;
		}
		npc.autoWorkTarget = getClosestWorksitePos(tiles);
	}

	private BlockPos getClosestWorksitePos(List<TileEntity> tiles) {
		BlockPos closestPos = BlockPos.ORIGIN;
		double closestDist = -1;
		for (TileEntity te : tiles) {
			if (te instanceof IWorkSite) {
				IWorkSite site = (IWorkSite) te;
				if (site.getOwner() != Owner.EMPTY && !npc.hasCommandPermissions(site.getOwner())) {
					continue;
				}
				if (npc.canWorkAt(site.getWorkType()) && site.hasWork()) {
					double dist = npc.getDistanceSq(te.getPos().getX() + 0.5d, te.getPos().getY(), te.getPos().getZ() + 0.5d);
					if (closestDist == -1 || dist < closestDist) {
						closestDist = dist;
						closestPos = te.getPos();
					}
				}
			}
		}
		return closestPos;
	}

}
