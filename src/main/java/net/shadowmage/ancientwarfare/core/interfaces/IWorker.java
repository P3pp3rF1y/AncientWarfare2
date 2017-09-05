package net.shadowmage.ancientwarfare.core.interfaces;

import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite.WorkType;

/*
 * Mark entities that are capable of doing 'work'
 *
 * @author Shadowmage
 */
public interface IWorker {

    /*
     * get worker effectiveness.  base == 1.  higher values are more effective at most work-types
     *
     * @param type the type of work to perform
     */
    public float getWorkEffectiveness(WorkType type);

    public boolean canWorkAt(WorkType type);

    public double getWorkRangeSq();

}
