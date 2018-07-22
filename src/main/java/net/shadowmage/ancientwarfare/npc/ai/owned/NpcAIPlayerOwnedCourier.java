package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.owner.IOwnable;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcCourier;
import net.shadowmage.ancientwarfare.npc.orders.RoutingOrder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NpcAIPlayerOwnedCourier extends NpcAI<NpcCourier> {

	private boolean init;
	private int routeIndex;
	private int ticksToWork;
	private int ticksAtSite;
	private RoutingOrder order;
	@Nonnull
	private ItemStack routeStack;

	@SuppressWarnings("squid:S2637")
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
		return shouldContinueExecuting();
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !(!npc.getIsAIEnabled() || npc.shouldBeAtHome()) && npc.backpackInventory != null && order != null && !order.isEmpty();
	}

	@Override
	public void startExecuting() {
		npc.addAITask(TASK_WORK);
	}

	@Override
	public void updateTask() {
		BlockPos pos = order.get(routeIndex).getTarget();
		double dist = npc.getDistanceSq(pos);
		if (dist > AWNPCStatics.npcActionRange * AWNPCStatics.npcActionRange) {
			npc.addAITask(TASK_MOVE);
			ticksAtSite = 0;
			ticksToWork = 0;
			moveToPosition(pos, dist);
		} else {
			moveRetryDelay = 0;
			npc.getNavigator().clearPath();
			npc.removeAITask(TASK_MOVE);
			workAtSite();
		}
	}

	@Override
	public void resetTask() {
		ticksToWork = 0;
		ticksAtSite = 0;
		moveRetryDelay = 0;
		npc.getNavigator().clearPath();
		npc.removeAITask(TASK_WORK + TASK_MOVE);
	}

	private void workAtSite() {
		if (ticksToWork == 0) {
			startWork();
		} else {
			ticksAtSite++;
			if (npc.ticksExisted % 10 == 0) {
				npc.swingArm(EnumHand.MAIN_HAND);
			}
			if (ticksAtSite > ticksToWork) {
				setMoveToNextSite();
			}
		}
	}

	private void startWork() {
		IItemHandler target = getTargetHandler();
		if (target != null) {
			ticksAtSite = 0;
			int moved = order.handleRouteAction(order.get(routeIndex), npc.backpackInventory, target);
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

	@Nullable
	private IItemHandler getTargetHandler() {
		RoutingOrder.RoutePoint point = order.get(routeIndex);
		return WorldTools.getTile(npc.world, point.getTarget())
				.filter(t -> t.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, point.getBlockSide()))
				.map(t -> getTargetHandler(point.getBlockSide(), t)).orElse(null);
	}

	private IItemHandler getTargetHandler(EnumFacing side, TileEntity te) {
		if (te instanceof IOwnable) {
			IOwnable ownableTE = (IOwnable) te;
			if (!npc.hasCommandPermissions(ownableTE.getOwner())) {
				return null;
			}
		}
		//noinspection ConstantConditions
		return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
	}

	private void setMoveToNextSite() {
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
