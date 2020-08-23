package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.orders.TradeOrder;

import javax.annotation.Nonnull;

/*
 * replaces upkeep ai for player owned trader when an orders item is equipped.<br>
 * manages moving the trader through a trade route, stopping for specified time at each point;<br>
 * manages moving towards and withdrawing upkeep at specified point in trade route (or auto if no trade order present);<br>
 * manages depositing income and withdrawing new tradables from specified points<br>
 *
 * @author Shadowmage
 */
public class NpcAIPlayerOwnedTrader extends NpcAI<NpcPlayerOwned> {

	/*
	 * state flags, to track what state the AI is currently in
	 */
	private boolean shelter;
	private boolean upkeep;
	private boolean restock;
	private boolean deposit;
	private boolean waiting;
	private boolean atShelter;
	private boolean atUpkeep;
	private boolean atDeposit;
	private boolean atWithdraw;
	private boolean atWaypoint;

	private int delayCounter;
	private int currentWaypointIndex;
	private BlockPos currentWaypoint;
	private BlockPos shelterPoint;
	private TradeOrder orders;

	public NpcAIPlayerOwnedTrader(NpcPlayerOwned npc) {
		super(npc);
		setMutexBits(MOVE + ATTACK);
	}

	public void onOrdersUpdated() {
		orders = TradeOrder.getTradeOrder(npc.ordersStack);
		currentWaypoint = null;
		shelterPoint = null;
		upkeep = false;
		restock = false;
		deposit = false;
		waiting = false;
		atDeposit = false;
		atShelter = false;
		atUpkeep = false;
		atWaypoint = false;
		atWithdraw = false;
		currentWaypointIndex = 0;
		delayCounter = 0;
		if (orders != null && orders.getRoute().size() > 0) {
			currentWaypoint = orders.getRoute().get(currentWaypointIndex).getPosition();
		}
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && orders != null && currentWaypoint != null;
	}

	@Override
	public void startExecuting() {
		//NOOP
	}

	@Override
	public void resetTask() {
		//NOOP
	}

	@Override
	public void updateTask() {
		if (npc.shouldBeAtHome() || shelter) {
			updateShelter();
		} else if (upkeep) {
			updateUpkeep();
		} else if (restock) {
			updateRestock();
		} else {
			updatePatrol();
		}
	}

	private void updateShelter() {
		npc.addAITask(TASK_GO_HOME);
		shelter = true;
		if (atShelter) {
			if (!npc.shouldBeAtHome()) {
				shelter = false;
				atShelter = false;
				shelterPoint = null;
				upkeep = false;
				atUpkeep = false;
				deposit = false;
				restock = false;
				atDeposit = false;
				atWithdraw = false;
				waiting = false;
				delayCounter = 0;
				npc.removeAITask(TASK_GO_HOME);
			}//end shelter code, return to previously current route point - if was interrupted in the middle of upkeep, will restart upkeep
		} else if (shelterPoint == null) {
			int index = currentWaypointIndex - 1;
			if (index < 0) {
				index = orders.getRoute().size() - 1;
			}
			BlockPos wp2 = orders.getRoute().get(index).getPosition();
			double d1 = npc.getDistanceSq(currentWaypoint);
			double d2 = npc.getDistanceSq(wp2);
			shelterPoint = d1 < d2 ? currentWaypoint : wp2;
		} else {
			double d = npc.getDistanceSq(shelterPoint);
			if (d > MIN_RANGE) {
				npc.addAITask(TASK_MOVE);
				moveToPosition(shelterPoint, d);
			} else {
				npc.removeAITask(TASK_MOVE);
				atShelter = true;
			}
		}
	}

	private void updateUpkeep() {
		npc.addAITask(TASK_UPKEEP);
		if (atUpkeep) {
			if (tryWithdrawUpkeep()) {
				atUpkeep = false;
				upkeep = false;
				restock = true;
				deposit = true;
				npc.removeAITask(TASK_UPKEEP);
				npc.removeAITask(TASK_IDLE_HUNGRY);
			}
		} else if (npc.getUpkeepPoint().isPresent()) {
			npc.getUpkeepPoint().ifPresent(upkeepPoint -> {
				double d = npc.getDistanceSq(upkeepPoint);
				if (d > MIN_RANGE) {
					npc.addAITask(TASK_MOVE);
					moveToPosition(upkeepPoint, d);
				} else {
					npc.removeAITask(TASK_MOVE);
					atUpkeep = true;
				}
			});
		} else//no upkeep point, display no upkeep task/state icon
		{
			npc.addAITask(TASK_IDLE_HUNGRY);
			npc.removeAITask(TASK_UPKEEP);
		}
	}

	private boolean tryWithdrawUpkeep() {
		return npc.getUpkeepPoint()
				.map(upkeepPoint -> WorldTools.getItemHandlerFromTile(npc.world, upkeepPoint, npc.getUpkeepBlockSide()).map(h -> npc.withdrawFood(h)).orElse(false))
				.orElse(false);
	}

	private void updateRestock() {
		if (deposit) {
			updateDeposit();
		} else {
			updateWithdraw();
		}
	}

	private void updateDeposit() {
		if (atDeposit) {
			doDeposit();
			deposit = false;
			atDeposit = false;
		} else if (orders.getRestockData().getDepositPoint() != null) {
			BlockPos p = orders.getRestockData().getDepositPoint();
			double d = npc.getDistanceSq(p);
			if (d > MIN_RANGE) {
				npc.addAITask(TASK_MOVE);
				moveToPosition(p, d);
			} else {
				npc.removeAITask(TASK_MOVE);
				atDeposit = true;
			}
		} else//no deposit point
		{
			deposit = false;//kick into withdraw mode
		}
	}

	private void updateWithdraw() {
		if (atWithdraw) {
			doWithdraw();
			setNextWaypoint();
			restock = false;
			atWithdraw = false;
		} else if (orders.getRestockData().getWithdrawPoint() != null) {
			BlockPos p = orders.getRestockData().getWithdrawPoint();
			double d = npc.getDistanceSq(p);
			if (d > MIN_RANGE) {
				npc.addAITask(TASK_MOVE);
				moveToPosition(p, d);
			} else {
				npc.removeAITask(TASK_MOVE);
				atWithdraw = true;
			}
		} else//no withdraw point
		{
			restock = false;
			setNextWaypoint();
		}
	}

	private void updatePatrol() {
		if (atWaypoint) {
			if (waiting) {
				delayCounter++;
				if (delayCounter >= orders.getRoute().get(currentWaypointIndex).getDelay()) {
					delayCounter = 0;
					waiting = false;
					atWaypoint = false;
					if (orders.getRoute().get(currentWaypointIndex).shouldUpkeep()) {
						upkeep = true;
					} else {
						setNextWaypoint();
					}
				}
			} else {
				waiting = true;
				delayCounter = 0;
			}
		} else {
			npc.addAITask(TASK_MOVE);
			double d = npc.getDistanceSq(currentWaypoint);
			if (d < MIN_RANGE) {
				atWaypoint = true;
				waiting = false;
				npc.removeAITask(TASK_MOVE);
			} else {
				moveToPosition(currentWaypoint, d);
			}
		}
	}

	private void setNextWaypoint() {
		currentWaypointIndex++;
		if (currentWaypointIndex >= orders.getRoute().size()) {
			currentWaypointIndex = 0;
		}
		currentWaypoint = orders.getRoute().get(currentWaypointIndex).getPosition();
	}

	private void doDeposit() {
		@Nonnull ItemStack backpack = npc.getHeldItemMainhand();
		if (!backpack.isEmpty() && backpack.getItem() instanceof ItemBackpack) {
			IItemHandler inv = backpack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			BlockPos pos = orders.getRestockData().getDepositPoint();
			//noinspection ConstantConditions
			WorldTools.getItemHandlerFromTile(npc.world, pos, orders.getRestockData().getDepositSide())
					.ifPresent(handler -> orders.getRestockData().doDeposit(inv, handler));
		}
	}

	private void doWithdraw() {
		@Nonnull ItemStack backpack = npc.getHeldItemMainhand();
		if (!backpack.isEmpty() && backpack.getItem() instanceof ItemBackpack) {
			IItemHandler inv = backpack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			BlockPos pos = orders.getRestockData().getWithdrawPoint();
			//noinspection ConstantConditions
			WorldTools.getItemHandlerFromTile(npc.world, pos, orders.getRestockData().getWithdrawSide())
					.ifPresent(handler -> orders.getRestockData().doWithdraw(inv, handler));
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		orders = TradeOrder.getTradeOrder(npc.ordersStack);
		currentWaypointIndex = tag.getInteger("waypoint");
		currentWaypoint = (orders == null || orders.getRoute().size() == 0) ? null : orders.getRoute().get(currentWaypointIndex).getPosition();
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setInteger("waypoint", currentWaypointIndex);
		return tag;
	}

}
