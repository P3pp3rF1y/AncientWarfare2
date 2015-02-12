package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

public class NpcAIPlayerOwnedFollowCommand extends NpcAI {

    BlockPosition moveTargetPos = null;

    public NpcAIPlayerOwnedFollowCommand(NpcBase npc) {
        super(npc);
        setMutexBits(ATTACK | MOVE);
    }

    @Override
    public boolean shouldExecute() {
        return continueExecuting();
    }

    @Override
    public boolean continueExecuting() {
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
            if (npc.getAttackTarget() != null && (cmd.type == CommandType.ATTACK || cmd.type == CommandType.GUARD || cmd.type == CommandType.ATTACK_AREA)) {
                //allow command to persist until next run of the task
            } else {
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
            case CLEAR_COMMAND: {
                npc.setPlayerCommand(null);
                break;
            }
            case CLEAR_HOME: {
                npc.setHomeArea(0, 0, 0, -1);
                npc.setPlayerCommand(null);
                break;
            }
            case CLEAR_UPKEEP: {
                npc.setUpkeepAutoPosition(null);
                npc.setPlayerCommand(null);
                break;
            }
            case SET_HOME: {
                npc.setHomeArea(cmd.x, cmd.y, cmd.z, 16);
                npc.setPlayerCommand(null);
                break;
            }
            case SET_UPKEEP: {
                BlockPosition pos = new BlockPosition(cmd.x, cmd.y, cmd.z);
                npc.setUpkeepAutoPosition(pos);
                npc.setPlayerCommand(null);
                break;
            }
            case ATTACK: {
                //should already be handled by npc 'handle command' functionality when command first received
                npc.setPlayerCommand(null);
                break;
            }
            default: {
                break;
            }
        }

        cmd = npc.getCurrentCommand();//refresh command, it may have been set to null from previous switch
        if (cmd != null)//if command was not of an instant type, it should not be null and still needs handling
        {
            switch (cmd.type) {
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
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    private void handleMoveCommand(Command cmd) {
        if (moveTargetPos == null || moveTargetPos.x != cmd.x || moveTargetPos.y != cmd.y || moveTargetPos.z != cmd.z) {
            moveTargetPos = new BlockPosition(cmd.x, cmd.y, cmd.z);
        }
        double sqDist = npc.getDistanceSq(moveTargetPos);
        if (sqDist > 3 * 3) {
            moveToPosition(moveTargetPos, sqDist);//not finished moving...move along path (or at least try)
        } else {
            npc.setPlayerCommand(null);//finished moving..clear the command...
        }
    }

    /**
     * move towards the commanded guard-entity.
     */
    private void handleGuardCommand(Command cmd) {
        Entity e = cmd.getEntityTarget(npc.worldObj);
        if (e == null) {
            npc.setPlayerCommand(null);//clear the command if the target entity cannot be found
            return;
        }
        double sqDist = npc.getDistanceSqToEntity(e);
        if (sqDist > 3 * 3) {
            moveToEntity(e, sqDist);//move to entity...
        } else {
            npc.getNavigator().clearPathEntity();//clear path to stop moving
            if (e instanceof EntityHorse && e.riddenByEntity == null) {
                npc.mountEntity(e);
                npc.setPlayerCommand(null);//clear command if horse was mounted successfully..
            }
            //do not clear command, guard command is persistent
        }
    }

    private void handleAttackMoveCommand(Command cmd) {
        //move along path while looking for attack targets... -- if a target is found, the next 'shouldContinue' will break out of the AI task and allow the NPC to commence attack operations
        if (moveTargetPos == null || moveTargetPos.x != cmd.x || moveTargetPos.y != cmd.y || moveTargetPos.z != cmd.z) {
            moveTargetPos = new BlockPosition(cmd.x, cmd.y, cmd.z);
        }
        double sqDist = npc.getDistanceSq(moveTargetPos);
        if (sqDist > 3 * 3) {
            moveToPosition(moveTargetPos, sqDist);//not finished moving...move along path (or at least try)
        } else {
            npc.setPlayerCommand(null);//finished moving..clear the command...
        }
    }

}
