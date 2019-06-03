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
	private int checkFrequency = 200;//how often to recheck if orders and work target are both null
	private int range = 40;

	public NpcAIPlayerOwnedFindWorksite(NpcWorker npc) {
		super(npc);
	}

	@Override
	public boolean shouldExecute() {
		if (!npc.getIsAIEnabled()) {
			return false;
		}
		return npc.ordersStack.isEmpty() && npc.autoWorkTarget == null && (lastExecuted == -1 || npc.ticksExisted - lastExecuted > checkFrequency);
	}

	@Override
	public boolean shouldContinueExecuting() {
		return false;
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
				if (!npc.canWorkAt(site.getWorkType()))
					npc.autoWorkTarget = null;
				if (npc.hasCommandPermissions(site.getOwner()))
					npc.autoWorkTarget = null;
				if (!site.hasWork())
					npc.autoWorkTarget = null;
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
		List<TileEntity> tiles = WorldTools.getTileEntitiesInArea(npc.world, x - range, y - range / 2, z - range, x + range, y + range / 2, z + range);
		IWorkSite site;
		TileEntity closestSite = null;
		double closestDist = -1;
		double dist;
		for (TileEntity te : tiles) {
			if (te instanceof IWorkSite) {
				site = (IWorkSite) te;
				if (site.getOwner() != Owner.EMPTY && !npc.hasCommandPermissions(site.getOwner()))
					continue;
				if (npc.canWorkAt(site.getWorkType()) && site.hasWork()) {
					dist = npc.getDistanceSq(te.getPos().getX() + 0.5d, te.getPos().getY(), te.getPos().getZ() + 0.5d);
					if (closestDist == -1 || dist < closestDist) {
						closestDist = dist;
						closestSite = te;
					}
				}
			}
		}
		if (closestSite != null) {
			npc.autoWorkTarget = closestSite.getPos();
		}
	}

}
