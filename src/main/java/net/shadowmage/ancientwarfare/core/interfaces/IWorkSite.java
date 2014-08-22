package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public interface IWorkSite extends ITorqueTile
{

/**
 * workers should call this before calling doWork() to make sure that the site
 * actually has work to do.
 * @return
 */
public boolean hasWork();

/**
 * can be called by a worker if hasWork() returns true.
 * @param worker
 */
public void addEnergyFromWorker(IWorker worker);

public void addEnergyFromPlayer(EntityPlayer player);

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

/**
 * Called when an upgrade item is used on this worksite.  It is the responsibility of the worksite to query the upgrade item/upgrade type, and apply any necessary upgrades.
 * @return true if the upgrade was valid and an item from the stack should be consumed.
 */
public boolean onUpgradeItemUsed(ItemStack stack);

public static enum WorkType
{
/**
 * any change to reg. names will fubar npc-leveling system, as they use these names to store accumulated xp
 */
MINING("work_type.mining"),
FARMING("work_type.farming"),
FORESTRY("work_type.forestry"),
CRAFTING("work_type.crafting"),
RESEARCH("work_type.research"),
NONE("work_type.none");
public final String regName;
WorkType(String regName){this.regName=regName;}
}

}
