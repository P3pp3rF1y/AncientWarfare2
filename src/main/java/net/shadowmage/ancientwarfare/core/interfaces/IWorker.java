package net.shadowmage.ancientwarfare.core.interfaces;

import java.util.EnumSet;

import net.minecraft.scoreboard.Team;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite.WorkType;

/**
 * IWorker interface marks entities and tile-entities that are capable of doing 'work'
 * @author Shadowmage
 */
public interface IWorker
{

public float getWorkEffectiveness();

public Team getTeam();

public EnumSet<WorkType> getWorkTypes();
}
