package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcCourier;
import net.shadowmage.ancientwarfare.npc.orders.RoutingOrder;

public class NpcAIPlayerOwnedCourier extends NpcAI<NpcCourier> {

    boolean init;

    int routeIndex;
    int ticksToWork;
    int ticksAtSite;

    RoutingOrder order;
    ItemStack routeStack;

    public NpcAIPlayerOwnedCourier(NpcCourier npc) {
        super(npc);
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
        return npc.backpackInventory != null && order != null && !order.isEmpty();
    }

    @Override
    public void startExecuting() {
        npc.addAITask(TASK_WORK);
    }

    @Override
    public void updateTask() {
        BlockPos pos = order.get(routeIndex).getTarget();
        double dist = npc.getDistanceSq(pos.x, pos.y, pos.z);
        if (dist > AWNPCStatics.npcActionRange * AWNPCStatics.npcActionRange) {
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
            int moved = order.handleRouteAction(order.get(routeIndex), npc.backpackInventory, target);
            npc.updateBackpackItemContents();
            if (moved > 0) {
                ticksToWork = (AWNPCStatics.npcWorkTicks - npc.getLevelingStats().getLevel()) * moved;
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
        BlockPos pos = order.get(routeIndex).getTarget();
        TileEntity te = npc.world.getTileEntity(pos.x, pos.y, pos.z);
        if (te instanceof IInventory) {
            if(te instanceof IOwnable){
                String name = ((IOwnable) te).getOwnerName();
                if(name != null && !npc.hasCommandPermissions(name)){
                    return null;
                }
            }
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
