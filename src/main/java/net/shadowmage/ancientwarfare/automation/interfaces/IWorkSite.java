package net.shadowmage.ancientwarfare.automation.interfaces;

import net.minecraft.entity.player.EntityPlayer;

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
 * called by a worker to process a single work-unit.
 * @param worker
 */
public void doWork(IWorker worker);

/**
 * workers will call this before starting to work at a site to make sure that the site can have
 * more workers.  In turn the worksite calls IWorker.canWorkAt() to make sure that the work-site is
 * applicable for the worker.
 * 
 * @param worker
 * @return
 */
public boolean canHaveWorker(IWorker worker);

/**
 * called by a worker prior to him starting work
 * @param worker
 * @return
 */
public boolean addWorker(IWorker worker);

/**
 * called by a worker when he is done working at a site
 * can be called when the worker goes home for the night, is shut down (for blocks/engines),
 * or when the worker stops working due to no more work at the site.
 * @param worker
 */
public void removeWorker(IWorker worker);

}
