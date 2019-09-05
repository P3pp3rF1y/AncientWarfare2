package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

public class NpcAIPlayerOwnedFollowCommand extends NpcAI<NpcPlayerOwned> {

	private BlockPos moveTargetPos = null;

	public NpcAIPlayerOwnedFollowCommand(NpcPlayerOwned npc) {
		super(npc);
		setMutexBits(ATTACK | MOVE);
	}

	@Override
	public boolean shouldExecute() {
		if (!npc.getIsAIEnabled()) {
			return false;
		}
		Command cmd = npc.getCurrentCommand();
		return cmd != Command.NONE && (!(cmd.type == CommandType.GUARD || cmd.type == CommandType.ATTACK_AREA) || npc.getAttackTarget() == null);
	}

	@Override
	public void resetTask() {
		Command cmd = npc.getCurrentCommand();
		if (cmd != Command.NONE && (npc.getAttackTarget() == null || !cmd.type.isPersistent())) {
			npc.handlePlayerCommand(Command.NONE);
		}
	}

	@Override
	public void startExecuting() {
		//noop
	}

	@Override
	public void updateTask() {
		Command cmd = npc.getCurrentCommand();
		handleCommand(cmd);
		if (!cmd.type.isPersistent()) {
			npc.setPlayerCommand(Command.NONE);
		}
	}

	@SuppressWarnings("squid:S1199")
	private void handleCommand(Command cmd) {
		switch (cmd.type)//handle instant type commands
		{
			case CLEAR_HOME: {
				npc.detachHome();
				break;
			}
			case CLEAR_UPKEEP: {
				npc.setUpkeepAutoPosition(null);
				break;
			}
			case SET_HOME: {
				npc.setHomePosAndDistance(cmd.pos, npc.getHomeRange());
				break;
			}
			case SET_UPKEEP: {
				npc.setUpkeepAutoPosition(cmd.pos);
				break;
			}
			case CLEAR_COMMAND:
			case ATTACK: {
				//should already be handled by npc 'handle command' functionality when command first received
				npc.setPlayerCommand(Command.NONE);
				break;
			}
			case ATTACK_AREA: {
				//TODO this likely needs an implementaion that will prioritize attacking targets over moving
				handleMoveCommand(cmd);
				break;
			}
			case GUARD: {
				handleGuardCommand(cmd);
				break;
			}
			case MOVE: {
				handleMoveCommand(cmd);
				break;
			}
		}
	}

	/*
	 * move towards the commanded guard-entity.
	 */
	private void handleGuardCommand(Command cmd) {
		Entity e = cmd.getEntityTarget(npc.world);
		if (e == null) {
			npc.setPlayerCommand(Command.NONE);//clear the command if the target entity cannot be found
			return;
		}
		double sqDist = npc.getDistanceSq(e);
		if (sqDist > MIN_RANGE) {
			moveToEntity(e, sqDist);//move to entity...
		} else {
			npc.getNavigator().clearPath();//clear path to stop moving
			if (e instanceof EntityHorse) {
				if (!npc.isRiding() && e.getPassengers().isEmpty()) {
					npc.startRiding(e);
					e.prevRotationYaw = e.rotationYaw = npc.rotationYaw % 360F;
					npc.setPlayerCommand(Command.NONE);//clear command if horse was mounted successfully..
				} else if (npc.isRiding() && npc.getRidingEntity() == e) {
					npc.dismountRidingEntity();
					npc.setPlayerCommand(Command.NONE);
				}
			}
			//do not clear command, guard command is persistent
		}
	}

	private void handleMoveCommand(Command cmd) {
		if (moveTargetPos == null || moveTargetPos != cmd.pos) {
			moveTargetPos = cmd.pos;
		}
		double sqDist = npc.getDistanceSq(moveTargetPos);
		if (sqDist > MIN_RANGE) {
			moveToPosition(moveTargetPos, sqDist);//not finished moving...move along path (or at least try)
		} else {
			npc.setPlayerCommand(Command.NONE);//finished moving..clear the command...
		}
	}
}
