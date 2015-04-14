package net.shadowmage.ancientwarfare.core.research;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.StringTools;

import java.util.*;

public class ResearchGoal {

    private static boolean hasInit = false;

    private static HashMap<Integer, ResearchGoal> goalsByID = new HashMap<Integer, ResearchGoal>();
    private static HashMap<String, ResearchGoal> goalsByName = new HashMap<String, ResearchGoal>();

    private final int researchId;
    private final String researchName;
    private Set<Integer> dependencies;//parsed shallow-dependency list

    private List<ItemStack> researchResources = new ArrayList<ItemStack>();
    private int researchTime;

    /**
     * set the first time dependencies for this goal are queried.  further queries for full-dependencies
     * will return this cached set
     */
    private Set<Integer> resolvedDependencies;//full dependency list

    public ResearchGoal(int id, String name) {
        researchId = id;
        researchName = name;
        dependencies = new HashSet<Integer>();
    }

    public void addResource(ItemStack resource) {
        this.researchResources.add(resource);
    }

    public void setResearchTime(int time) {
        this.researchTime = time;
    }

    public int getTotalResearchTime() {
        return researchTime;
    }

    public String getName() {
        return researchName;
    }

    public int getId() {
        return researchId;
    }

    public ResearchGoal addDependencies(int... deps) {
        for (int i = 0; i < deps.length; i++) {
            dependencies.add(deps[i]);
        }
        return this;
    }

    public List<ItemStack> getResources() {
        return researchResources;
    }

    /**
     * return the direct dependencies for this goal -- does not include any sub-dependencies -- see {@link #resolveDependeciesFor(ResearchGoal)}
     */
    public Set<Integer> getDependencies() {
        return dependencies;
    }

    public boolean canResearch(Set<Integer> knownResearch) {
        Set<Integer> fullDependencies = resolveDependeciesFor(this);
        return knownResearch.containsAll(fullDependencies);
    }

    public boolean tryStart(IInventory inventory, int side) {
        boolean canStart = true;
        if (!AWCoreStatics.enableResearchResourceUse) {
            return true;
        }
        for (ItemStack stack : this.researchResources) {
            if (InventoryTools.getCountOf(inventory, side, stack) < stack.stackSize) {
                canStart = false;
                break;
            }
        }
        if (canStart) {
            for (ItemStack stack : this.researchResources) {
                InventoryTools.removeItems(inventory, side, stack, stack.stackSize);
            }
        }
        return canStart;
    }

    public static void initializeResearch() {
        if (hasInit) {
            return;
        }
        hasInit = true;
        parseGoalNames(StringTools.getResourceLines(AWCoreStatics.resourcePath + "research_data.csv"));
        parseGoalDependencies(StringTools.getResourceLines(AWCoreStatics.resourcePath + "research_dependencies.csv"));
        parseGoalResources(StringTools.getResourceLines(AWCoreStatics.resourcePath + "research_resources.csv"));
    }

    private static void parseGoalNames(List<String> lines) {
        String[] split;
        int id;
        int time;
        String name;
        ResearchGoal goal;
        int lineNumber = 1;
        try {
            for (String line : lines) {
                split = StringTools.parseStringArray(line);
                id = StringTools.safeParseInt(split[0]);
                name = split[1].startsWith("research.") ? split[1] : "research." + split[1];
                time = AWCoreStatics.getResearchTimeFor(name, StringTools.safeParseInt(split[2]));
                goal = new ResearchGoal(id, name);
                goalsByID.put(id, goal);
                goalsByName.put(name, goal);
                goal.setResearchTime(time);
                lineNumber++;
            }
        } catch (Exception e) {
            AWLog.logError("Caught error parsing research goal data, line number (ignoring comment lines): " + lineNumber + " line: " + lines.get(lineNumber - 1));
            e.printStackTrace();
        }
    }

    private static void parseGoalDependencies(List<String> lines) {
        String[] split;
        String name;
        String dep;
        for (String line : lines) {
            split = StringTools.parseStringArray(line);
            name = split[0].startsWith("research.") ? split[0] : "research." + split[0];
            dep = split[1].startsWith("research.") ? split[1] : "research." + split[1];
            if (goalsByName.containsKey(name) && goalsByName.containsKey(dep)) {
                goalsByName.get(name).addDependencies(goalsByName.get(dep).researchId);
            }
        }
    }

    private static void parseGoalResources(List<String> lines) {
        String[] split;
        String name;
        for (String line : lines) {
            split = StringTools.parseStringArray(line);
            name = split[0].startsWith("research.") ? split[0] : "research." + split[0];
            if (!goalsByName.containsKey(name)) {
                AWLog.logError("Could not locate goal for name: " + name);
                continue;
            }
            ItemStack stack = StringTools.safeParseStack(split[1], split[2], split[3]);
            if (stack == null) {
                AWLog.logError("Could not locate item for name: " + split[1]);
                continue;
            }
            goalsByName.get(name).addResource(stack);
        }
    }

    public static ResearchGoal getGoal(String name) {
        return goalsByName.get(name);
    }

    public static ResearchGoal getGoal(int id) {
        return goalsByID.get(id);
    }

    /**
     * Return a set of ResearchGoals corresponding to the input collection of goal numbers.<br>
     * Invalid goal numbers, or duplicate input numbers, will be ignored.  The returned set will<br>
     * only contain valid research goals, with no duplicates.
     */
    public static Set<ResearchGoal> getGoalsFor(Collection<Integer> researchNums) {
        Set<ResearchGoal> out = new HashSet<ResearchGoal>();
        for (Integer i : researchNums) {
            if (goalsByID.containsKey(i)) {
                out.add(goalsByID.get(i));
            }
        }
        return out;
    }

    /**
     * Return a set of research goal numbers comprising the entire dependency tree for the input
     * research goal.  This dependency set shall contain every direct dependency for the input goal, and
     * any dependencies of those goals (recursive until base).  The returned set shall only contain valid,
     * loaded goal numbers.
     */
    public static Set<Integer> resolveDependeciesFor(ResearchGoal goal) {
        if (goal.resolvedDependencies != null) {
            return goal.resolvedDependencies;
        }
        Set<Integer> foundDependencies = new HashSet<Integer>();
        LinkedList<Integer> openList = new LinkedList<Integer>();
        openList.addAll(goal.dependencies);
        Set<Integer> gDeps;
        Integer dep;
        ResearchGoal g1;
        while (!openList.isEmpty()) {
            dep = openList.poll();
            foundDependencies.add(dep);
            g1 = goalsByID.get(dep);
            gDeps = g1.dependencies;
            for (Integer i : gDeps) {
                if (!foundDependencies.contains(i)) {
                    foundDependencies.add(i);
                    openList.add(i);
                }
            }
        }
        goal.resolvedDependencies = foundDependencies;
        return foundDependencies;
    }

    public static Set<Integer> getResearchableGoalsFor(Collection<Integer> knownResearch, Collection<Integer> queuedResearch, int inProgress) {
        Set<Integer> totalKnowledge = new HashSet<Integer>();
        totalKnowledge.addAll(knownResearch);
        totalKnowledge.addAll(queuedResearch);
        if (inProgress >= 0) {
            totalKnowledge.add(inProgress);
        }
        Set<Integer> researchableGoals = new HashSet<Integer>();
        ResearchGoal goal;
        for (Integer g : goalsByID.keySet()) {
            if (totalKnowledge.contains(g)) {
                continue;
            }
            goal = goalsByID.get(g);
            if (goal.canResearch(totalKnowledge)) {
                researchableGoals.add(goal.getId());
            }
        }
        return researchableGoals;
    }

    public static Collection<ResearchGoal> getResearchGoals() {
        return goalsByName.values();
    }

}
