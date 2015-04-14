package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.scoreboard.Team;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite.WorkType;

/**
 * IWorker interface marks entities and tile-entities that are capable of doing 'work'
 *
 * @author Shadowmage
 */
public interface IWorker {

    /**
     * get worker effectiveness.  base == 1.  higher values are more effective at most work-types
     *
     * @param type TODO
     */
    public float getWorkEffectiveness(WorkType type);

    /**
     * return the team that this worker belongs to, null for none
     */
    public Team getWorkerTeam();


    public boolean canWorkAt(WorkType type);

}
