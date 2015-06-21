package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcCourier;
import net.shadowmage.ancientwarfare.npc.orders.RoutingOrder;

public class NpcAIPlayerOwnedCourier extends NpcAI {

    boolean init;

    int routeIndex;
    int ticksToWork;
    int ticksAtSite;

    RoutingOrder order;
    ItemStack routeStack;

    NpcCourier courier;

    public NpcAIPlayerOwnedCourier(NpcBase npc) {
        super(npc);
        courier = (NpcCourier) npc;
        this.setMutexBits(ATTACK + MOVE);
    }

    @Override
    public boolean shouldExecute() {
        if (!init) {
            init = true;
            routeStack = npc.ordersStack;
            order = RoutingOrder.getRoutingOrder(routeStack);
            if ((order != null && routeIndex >= order.size()) || order == null) {
                routeIndex = 0;
            }
        }
        return continueExecuting();
    }

    @Override
    public boolean continueExecuting() {
        if (!npc.getIsAIEnabled() || npc.shouldBeAtHome()) {
            return false;
        }
        return courier.backpackInventory != null && order != null && !order.isEmpty();
    }

    @Override
    public void startExecuting() {
        npc.addAITask(TASK_WORK);
    }

    @Override
    public void updateTask() {
        BlockPosition pos = order.get(routeIndex).getTarget();
        double dist = npc.getDistanceSq(pos.x, pos.y, pos.z);
        if (dist > 5.d * 5.d) {
            npc.addAITask(TASK_MOVE);
            ticksAtSite = 0;
            ticksToWork = 0;
            moveToPosition(pos, dist);
        } else {
            moveRetryDelay = 0;
            npc.getNavigator().clearPathEntity();
            npc.removeAITask(TASK_MOVE);
            workAtSite();
        }
    }

    @Override
    public void resetTask() {
        ticksToWork = 0;
        ticksAtSite = 0;
        moveRetryDelay = 0;
        npc.getNavigator().clearPathEntity();
        npc.removeAITask(TASK_WORK + TASK_MOVE);
    }

    public void workAtSite() {
        if (ticksToWork == 0) {
            startWork();
        } else {
            ticksAtSite++;
            if (npc.ticksExisted % 10 == 0) {
                npc.swingItem();
            }
            if (ticksAtSite > ticksToWork) {
                setMoveToNextSite();
            }
        }
    }

    private void startWork() {
        IInventory target = getTargetInventory();
        if (target != null) {
            ticksAtSite = 0;
            int moved = order.handleRouteAction(order.get(routeIndex), courier.backpackInventory, target);
            courier.updateBackpackItemContents();
            if (moved > 0) {
                ticksToWork = AWNPCStatics.npcWorkTicks * moved;
                int lvl = npc.getLevelingStats().getLevel();
                ticksToWork -= lvl * moved;
                if (ticksToWork <= 0) {
                    ticksToWork = 0;
                }
                npc.addExperience(moved * AWNPCStatics.npcXpFromMoveItem);
                return;
            }
        }
        setMoveToNextSite();
    }

    private IInventory getTargetInventory() {
        BlockPosition pos = order.get(routeIndex).getTarget();
        TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
        if (te instanceof IInventory) {
            return (IInventory) te;
        }
        return null;
    }

    public void setMoveToNextSite() {
        ticksAtSite = 0;
        ticksToWork = 0;
        moveRetryDelay = 0;
        routeIndex++;
        if (routeIndex >= order.size()) {
            routeIndex = 0;
        }
    }

    public void onOrdersChanged() {
        routeStack = npc.ordersStack;
        order = RoutingOrder.getRoutingOrder(routeStack);
        routeIndex = 0;
        ticksAtSite = 0;
        ticksToWork = 0;
        moveRetryDelay = 0;
    }

    public void readFromNBT(NBTTagCompound tag) {
        routeIndex = tag.getInteger("routeIndex");
        ticksAtSite = tag.getInteger("ticksAtSite");
        ticksToWork = tag.getInteger("ticksToWork");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("routeIndex", routeIndex);
        tag.setInteger("ticksAtSite", ticksAtSite);
        tag.setInteger("ticksToWork", ticksToWork);
        return tag;
    }

}
