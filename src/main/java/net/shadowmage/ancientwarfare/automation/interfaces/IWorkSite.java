package net.shadowmage.ancientwarfare.automation.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public interface IWorkSite
{

/**
 * called when the player interacts/clicks on the work-site
 * some work-sites may implement a manual-work mode to do work
 * per player click.
 * @param player
 */
public void doPlayerWork(EntityPlayer player);

/**
 * workers should call this before calling doWork() to make sure that the site
 * actually has work to do.
 * @return
 */
public boolean hasWork();

/**
 * called by a worker to process a single work-unit.  The worker should have called hasWork just
 * prior to calling doWork
 * @param worker
 */
public void doWork(IWorker worker);

/**
 * workers will call this before starting to work at a site to make sure that the site can have
 * more workers.  validates with the worker to see if worker can work at this worksite by checking
 * this.getWorkType() vs worker.getWorkTypes()
 * 
 * @param worker
 * @return
 */
public boolean canHaveWorker(IWorker worker);

/**
 * called by a worker prior to him starting work
 * @param worker
 * @return true if worker was already present or successfully added
 */
public boolean addWorker(IWorker worker);

/**
 * called by a worker when he is done working at a site
 * can be called when the worker goes home for the night, is shut down (for blocks/engines),
 * or when the worker stops working due to no more work at the site.
 * (workers should maintain their own internal designated work-site reference, to know where to return to)
 * @param worker
 */
public void removeWorker(IWorker worker);

/**
 * called by workers to validate work-type when IWorker.canWorkAt(IWorkSite) is called
 * workers should be responsible for maintaining their own list of acceptable work types
 * @return
 */
public WorkType getWorkType();

public Team getTeam();

public BlockPosition getWorkBoundsMin();

public BlockPosition getWorkBoundsMax();

public boolean hasWorkBounds();

public static enum WorkType
{
MINING,
FARMING,
FORESTRY,
ANIMAL_HUSBANDRY,
CONSTRUCTION,
CRAFTING,
RESEARCH
}

}
