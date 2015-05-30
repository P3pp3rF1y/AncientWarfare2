package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcWorker;

import java.util.List;


public class NpcAIPlayerOwnedFindWorksite extends NpcAI {

    int lastExecuted = -1;//set to -1 default to trigger should execute lookup on first run
    int checkFrequency = 200;//how often to recheck if orders and work target are both null
    int range = 40;

    public NpcAIPlayerOwnedFindWorksite(NpcWorker npc) {
        super(npc);
    }

    @Override
    public boolean shouldExecute() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        return npc.ordersStack == null && ((NpcWorker) npc).autoWorkTarget == null && (lastExecuted == -1 || npc.ticksExisted - lastExecuted > checkFrequency);
    }

    @Override
    public boolean continueExecuting() {
        return false;
    }

    @Override
    public void startExecuting() {
        lastExecuted = npc.ticksExisted;
        NpcWorker worker = (NpcWorker) npc;
        if (worker.autoWorkTarget != null)//validate existing position
        {
            BlockPosition pos = worker.autoWorkTarget;
            TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
            if (te instanceof IWorkSite) {
                IWorkSite site = (IWorkSite) te;
                //TODO validate if can work at owner/team
                if (!worker.canWorkAt(site.getWorkType()) || !site.hasWork()) {
                    worker.autoWorkTarget = null;
                }
            } else {
                worker.autoWorkTarget = null;
            }
        }
        if (worker.autoWorkTarget == null) {
            findWorkTarget();
        }
    }

    private void findWorkTarget() {
        int x = MathHelper.floor_double(npc.posX);
        int y = MathHelper.floor_double(npc.posY);
        int z = MathHelper.floor_double(npc.posZ);
        List<TileEntity> tiles = WorldTools.getTileEntitiesInArea(npc.worldObj, x - range, y - range / 2, z - range, x + range, y + range / 2, z + range);
        IWorkSite site;
        NpcWorker worker = (NpcWorker) npc;
        TileEntity closestSite = null;
        double closestDist = -1;
        double dist;
        for (TileEntity te : tiles) {
            if (te instanceof IWorkSite) {
                //TODO validate canBeCommandedBy to check if owner is valid....
                site = (IWorkSite) te;
                if (site.getTeam() != npc.getTeam()) {
                    continue;
                }
                if (worker.canWorkAt(site.getWorkType()) && site.hasWork()) {
                    dist = npc.getDistanceSq(te.xCoord + 0.5d, te.yCoord, te.zCoord + 0.5d);
                    if (closestDist == -1 || dist < closestDist) {
                        closestDist = dist;
                        closestSite = te;
                    }
                }
            }
        }
        if (closestSite != null) {
            worker.autoWorkTarget = new BlockPosition(closestSite.xCoord, closestSite.yCoord, closestSite.zCoord);
        }
    }

}
