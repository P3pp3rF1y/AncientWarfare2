package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIPlayerOwnedFollowCommand extends NpcAI
{

public NpcAIPlayerOwnedFollowCommand(NpcBase npc)
  {
  super(npc);
  }

@Override
public boolean shouldExecute(){return npc.getIsAIEnabled() && npc.getCurrentCommand()!=null;}

@Override
public boolean continueExecuting(){return npc.getIsAIEnabled() && npc.getCurrentCommand()!=null;}

@Override
public void resetTask(){npc.handlePlayerCommand(null);}

@Override
public void startExecuting()
  {
  //TODO
  }

@Override
public void updateTask()
  {
  //TODO
  switch(npc.getCurrentCommand().type)
  {
  case ATTACK:
  break;
  case ATTACK_AREA:
  break;
  case CLEAR_COMMAND:
  npc.handlePlayerCommand(null);
  //nullify command
  break;
  case CLEAR_HOME:
  //set npc home to null, nullify command
  break;
  case CLEAR_UPKEEP:
  //set npc upkeep to null, nullify command
  break;
  case GUARD:
  //set npc move-to-entity and follow command (somehow allow for combat AI to still function)
  break;
  case MOVE:
  //set npc move-to position command
  break;
  case SET_HOME:
  //set npc home, nullify command
  break;
  case SET_UPKEEP:
  //set npc upkeep, nullify command
  break;
  }
  }

}
