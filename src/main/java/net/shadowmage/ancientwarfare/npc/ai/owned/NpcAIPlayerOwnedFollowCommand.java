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
		if (cmd == null) {
			return false;
		}
		if (cmd.type == CommandType.GUARD || cmd.type == CommandType.ATTACK_AREA)//if it is an attack or entity-targeting task
		{
			return npc.getAttackTarget() == null;//only continue with task while attack target is null, else persist command until next run and let the attack ai run
		}
		return true;//else it was not one of the aformentioned commands OR attack target==null, continue with command
	}

	@Override
	public void resetTask() {
		Command cmd = npc.getCurrentCommand();
		if (cmd != null) {
			//allow command to persist until next run of the task
			if (npc.getAttackTarget() == null || !cmd.type.isPersistent()) {
				npc.handlePlayerCommand(null);
			}
		}
	}

	@Override
	public void startExecuting() {
		//TODO
	}

	@Override
	public void updateTask() {
		Command cmd = npc.getCurrentCommand();
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
				npc.setPlayerCommand(null);
				break;
			}
			case ATTACK_AREA: {
				handleAttackMoveCommand(cmd);
				break;
			}
			case GUARD: {
				handleGuardCommand(cmd);
				break;
			}
			case MOVE: {
				handleMoveCommand(cmd);
				return;
			}
		}
		if (!cmd.type.isPersistent()) {
			npc.setPlayerCommand(null);
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
			npc.setPlayerCommand(null);//finished moving..clear the command...
		}
	}

	/*
	 * move towards the commanded guard-entity.
	 */
	private void handleGuardCommand(Command cmd) {
		Entity e = cmd.getEntityTarget(npc.world);
		if (e == null) {
			npc.setPlayerCommand(null);//clear the command if the target entity cannot be found
			return;
		}
		double sqDist = npc.getDistanceSq(e);
		if (sqDist > MIN_RANGE) {
			moveToEntity(e, sqDist);//move to entity...
		} else {
			npc.getNavigator().clearPath();//clear path to stop moving
			if (e instanceof EntityHorse && e.getPassengers().isEmpty()) {
				npc.startRiding(e);
				e.prevRotationYaw = e.rotationYaw = npc.rotationYaw % 360F;
				npc.setPlayerCommand(null);//clear command if horse was mounted successfully..
			}
			//do not clear command, guard command is persistent
		}
	}

	private void handleAttackMoveCommand(Command cmd) {
		//move along path while looking for attack targets... -- if a target is found, the next 'shouldContinue' will break out of the AI task and allow the NPC to commence attack operations
		if (moveTargetPos == null || moveTargetPos != cmd.pos) {
			moveTargetPos = cmd.pos;
		}
		double sqDist = npc.getDistanceSq(moveTargetPos);
		if (sqDist > MIN_RANGE) {
			moveToPosition(moveTargetPos, sqDist);//not finished moving...move along path (or at least try)
		} else {
			npc.setPlayerCommand(null);//finished moving..clear the command...
		}
	}

}
