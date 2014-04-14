package net.shadowmage.ancientwarfare.core.research;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.config.Statics;
import net.shadowmage.ancientwarfare.core.util.StringTools;

public class ResearchGoal
{

private static HashMap<Integer, ResearchGoal> goalsByID = new HashMap<Integer, ResearchGoal>();
private static HashMap<String, ResearchGoal> goalsByName = new HashMap<String, ResearchGoal>();

private static boolean hasInit = false;

int researchId;
String researchName;
Set<Integer> dependencies;
public ResearchGoal(int id, String name)
  {
  researchId = id;
  researchName = name;
  dependencies = new HashSet<Integer>();
  }

public ResearchGoal addDependencies(int... deps)
  {
  for(int i = 0; i < deps.length; i++)
    {
    dependencies.add(deps[i]);
    }
  return this;
  }

/**
 * return the direct dependencies for this goal -- does not include any sub-dependencies -- see {@link #resolveDependeciesFor(ResearchGoal)}
 * @return
 */
public Set<Integer> getDependencies()
  {
  return dependencies;
  }

public static void initializeResearch()
  {
  if(hasInit){return;}
  hasInit = true;
  parseGoalNames(StringTools.getResourceLines(Statics.resourcePath+"research_data.csv")); 
  parseGoalDependencies(StringTools.getResourceLines(Statics.resourcePath+"research_dependencies.csv"));
  }

private static void parseGoalNames(List<String> lines)
  {
  String[] split;
  int id;
  String name;
  ResearchGoal goal;
  for(String line : lines)
    {
    split = StringTools.parseStringArray(line);
    id = StringTools.safeParseInt(split[0]);
    name = split[1];
    goal = new ResearchGoal(id, name);
    AWLog.logDebug("Loading research goal from disk...id: "+id+" :: "+name);
    goalsByID.put(id, goal);
    goalsByName.put(name, goal);
    } 
  }

private static void parseGoalDependencies(List<String> lines)
  {
  String[] split;  
  String name;
  String dep;
  for(String line : lines)
    {
    split = StringTools.parseStringArray(line);
    name = split[0];
    dep = split[1];
    AWLog.logDebug("parsed dependency for: "+name+" of: "+dep);
    if(goalsByName.containsKey(name) && goalsByName.containsKey(dep))
      {
      goalsByName.get(name).addDependencies(goalsByName.get(dep).researchId);
      }
    } 
  }

/**
 * Return a set of ResearchGoals corresponding to the input collection of goal numbers.<br>
 * Invalid goal numbers, or duplicate input numbers, will be ignored.  The returned set will<br>
 * only contain valid research goals, with no duplicates.
 * @param researchNums
 * @return
 */
public static Set<ResearchGoal> getGoalsFor(Collection<Integer> researchNums)
  {
  Set<ResearchGoal> out = new HashSet<ResearchGoal>();
  for(Integer i : researchNums)
    {
    if(goalsByID.containsKey(i))
      {
      out.add(goalsByID.get(i));
      }
    }  
  return out;
  }

/**
 * Return a set of research goal numbers comprising the entire dependency tree for the input
 * research goal.  This dependency set shall contain every direct dependency for the input goal, and
 * any dependencies of those goals (recursive until base).
 * @param goal
 * @return
 */
public static Set<Integer> resolveDependeciesFor(ResearchGoal goal)
  {
  Set<Integer> foundDependencies = new HashSet<Integer>();
  LinkedList<Integer> openList = new LinkedList<Integer>();  
  openList.addAll(goal.dependencies);
  Set<Integer> gDeps;  
  Integer dep;
  ResearchGoal g1;
  while(!openList.isEmpty())
    {
    dep = openList.poll();
    foundDependencies.add(dep);
    g1 = goalsByID.get(dep);
    gDeps = g1.dependencies;
    for(Integer i : gDeps)
      {
      if(!foundDependencies.contains(i))
        {
        foundDependencies.add(i);
        openList.add(i);
        }
      }
    }   
  return foundDependencies;
  }

}
