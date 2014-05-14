package net.shadowmage.ancientwarfare.core.interfaces;

import java.util.EnumSet;

import net.minecraft.scoreboard.Team;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite.WorkType;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

/**
 * IWorker interface marks entities and tile-entities that are capable of doing 'work'
 * @author Shadowmage
 */
public interface IWorker
{

/**
 * get worker effectiveness.  base == 1.  higher values are more effective at most work-types
 * @return
 */
public float getWorkEffectiveness();

/**
 * return the team that this worker belongs to, null for none
 * @return
 */
public Team getTeam();

/**
 * return an enumSet of the valid work-types for this worker.
 * @return
 */
public EnumSet<WorkType> getWorkTypes();

/**
 * return a block-position denoting the location of this worker
 * @return
 */
public BlockPosition getPosition();

/**
 * return the work-site that is the current target of this worker
 * @return
 */
public IWorkSite getWorkSite();

/**
 * called by a work-site when the block is removed or the worker found to be invalid
 */
public void clearWorkSite();

}
