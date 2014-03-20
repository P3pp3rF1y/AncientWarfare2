package net.shadowmage.ancientwarfare.automation.interfaces;

/**
 * IWorker interface marks entities and tile-entities that are capable of doing 'work'
 * @author Shadowmage
 */
public interface IWorker
{

public float getWorkEffectiveness();

/**
 * validates a work-site vs. the workers applicable work-types.
 * @param site
 * @return
 */
public boolean canWorkAt(IWorkSite site);

public void clearWorkSite();
}
