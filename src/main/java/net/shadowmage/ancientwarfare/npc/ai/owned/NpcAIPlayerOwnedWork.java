package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcWorker;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder.WorkEntry;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder.WorkPriorityType;

public class NpcAIPlayerOwnedWork extends NpcAI {

    public int ticksAtSite = 0;
    public int workIndex;
    public WorkOrder order;
    ItemStack orderStack;
    boolean init = false;
    NpcWorker worker;

    public NpcAIPlayerOwnedWork(NpcBase npc) {
        super(npc);
        if (!(npc instanceof NpcWorker)) {
            throw new IllegalArgumentException("cannot instantiate work ai task on non-worker npc");
        }
        worker = (NpcWorker) npc;
        this.setMutexBits(MOVE + ATTACK);
    }

    @Override
    public boolean shouldExecute() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        if (npc.getFoodRemaining() <= 0 || npc.shouldBeAtHome()) {
            return false;
        }
        if (!init) {
            orderStack = npc.ordersStack;
            order = WorkOrder.getWorkOrder(orderStack);
            init = true;
            if (order == null || workIndex >= order.size()) {
                workIndex = 0;
            }
        }
        return orderStack != null && order != null && !order.isEmpty();
    }

    @Override
    public boolean continueExecuting() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        if (npc.getFoodRemaining() <= 0 || npc.shouldBeAtHome()) {
            return false;
        }
        return orderStack != null && order != null && !order.isEmpty();
    }

    @Override
    public void updateTask() {
        BlockPosition pos = order.get(workIndex).getPosition();
        double dist = npc.getDistanceSq(pos.x, pos.y, pos.z);
//  AWLog.logDebug("distance to site: "+dist);
        if (dist > 5.d * 5.d) {
//    AWLog.logDebug("moving to worksite..."+pos);
            npc.addAITask(TASK_MOVE);
            ticksAtSite = 0;
            moveToPosition(pos, dist);
        } else {
//    AWLog.logDebug("working at site....."+pos);
            npc.getNavigator().clearPathEntity();
            npc.removeAITask(TASK_MOVE);
            workAtSite();
        }
    }

    @Override
    public void startExecuting() {
        npc.addAITask(TASK_WORK);
    }

    protected void workAtSite() {
        ticksAtSite++;
        if (npc.ticksExisted % 10 == 0) {
            npc.swingItem();
        }
        if (ticksAtSite >= AWNPCStatics.npcWorkTicks) {
            ticksAtSite = 0;
            WorkEntry entry = order.get(workIndex);
            BlockPosition pos = entry.getPosition();
            TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
            if (te instanceof IWorkSite) {
                IWorkSite site = (IWorkSite) te;
                if (((IWorker) npc).canWorkAt(site.getWorkType())) {
                    if (site.hasWork()) {
                        npc.addExperience(AWNPCStatics.npcXpFromWork);
                        site.addEnergyFromWorker((IWorker) npc);
                    } else {
                        if (shouldMoveFromNoWork(entry)) {
                            setMoveToNextSite();
                        }
                    }
                    if (shouldMoveFromTimeAtSite(entry)) {
                        setMoveToNextSite();
                    }
                    return;
                }
            }
            setMoveToNextSite();
        }
    }

    protected boolean shouldMoveFromNoWork(WorkEntry entry) {
        if (order.getPriorityType() == WorkPriorityType.TIMED) {
            return false;
        } else if (order.getPriorityType() == WorkPriorityType.ROUTE) {
            return true;
        } else if (order.getPriorityType() == WorkPriorityType.PRIORITY_LIST) {
            return true;
        }
        return order.size() > 1;
    }

    protected boolean shouldMoveFromTimeAtSite(WorkEntry entry) {
        return order.getPriorityType() == WorkPriorityType.TIMED && ticksAtSite > entry.getWorkLength();
    }

    protected void setMoveToNextSite() {
        ticksAtSite = 0;
        moveRetryDelay = 0;
        workIndex = order.getPriorityType().getNextWorkIndex(workIndex, order.getEntries(), npc);
    }

    public void onOrdersChanged() {
        orderStack = npc.ordersStack;
        order = WorkOrder.getWorkOrder(orderStack);
        workIndex = 0;
        ticksAtSite = 0;
    }

    @Override
    public void resetTask() {
        ticksAtSite = 0;
        this.npc.removeAITask(TASK_WORK + TASK_MOVE);
    }

    public void readFromNBT(NBTTagCompound tag) {
        ticksAtSite = tag.getInteger("ticksAtSite");
        workIndex = tag.getInteger("workIndex");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("ticksAtSite", ticksAtSite);
        tag.setInteger("workIndex", workIndex);
        return tag;
    }


}
