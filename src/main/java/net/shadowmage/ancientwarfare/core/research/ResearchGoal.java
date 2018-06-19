package net.shadowmage.ancientwarfare.core.research;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.registry.ResearchRegistry;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class ResearchGoal {

	private final Random random;

	private final String researchName;
	private final Set<String> dependencies;
	private final Set<Ingredient> researchResources;
	private int researchTime;

	private Set<String> resolvedFullDependencies;

	public ResearchGoal(String name, Set<String> dependencies, Set<Ingredient> researchResources, int researchTime) {
		researchName = name;
		this.dependencies = dependencies;
		this.researchResources = researchResources;
		this.researchTime = researchTime;
		random = new Random(researchName.hashCode());
	}

	public int getTotalResearchTime() {
		return researchTime;
	}

	public String getName() {
		return researchName;
	}

	public String getUnlocalizedName() {
		return getUnlocalizedName(researchName);
	}

	public static String getUnlocalizedName(String researchName) {
		return "research." + researchName;
	}

	@Override
	public boolean equals(Object o) {
		return this == o || o instanceof ResearchGoal && researchName.equals(((ResearchGoal) o).researchName);
	}

	@Override
	public int hashCode() {
		return researchName.hashCode();
	}

	public NonNullList<ItemStack> getResourcesForDisplay() {
		return researchResources.stream()
				.filter(i -> i.getMatchingStacks().length > 0)
				.map(i -> i.getMatchingStacks()[random.nextInt(i.getMatchingStacks().length)])
				.collect(Collectors.toCollection(NonNullList::create));
	}

	public Set<ResearchGoal> getDependencies() {
		return dependencies.stream().map(ResearchRegistry::getResearch).collect(Collectors.toSet());
	}

	boolean canResearch(Set<String> knownResearch) {
		Set<String> fullDependencies = resolveFullDependeciesFor(this);
		return knownResearch.containsAll(fullDependencies);
	}

	public boolean tryStart(IItemHandler handler) {
		if (!AWCoreStatics.enableResearchResourceUse) {
			return true;
		}

		NonNullList<ItemStack> inventoryMatch = NonNullList.create();
		IItemHandlerModifiable clonedHandler = InventoryTools.cloneItemHandler(handler);
		for (Ingredient ingredient : this.researchResources) {
			ItemStack stackMatch = AWCraftingManager.getIngredientInventoryMatch(clonedHandler, ingredient);
			if (stackMatch.isEmpty()) {
				return false;
			}
			inventoryMatch.add(stackMatch);
		}

		InventoryTools.removeItems(handler, inventoryMatch);
		return true;
	}

	/*
	 * Return a set of research goal numbers comprising the entire dependency tree for the input
	 * research goal.  This dependency set shall contain every direct dependency for the input goal, and
	 * any dependencies of those goals (recursive until base).  The returned set shall only contain valid,
	 * loaded goal numbers.
	 */
	private static Set<String> resolveFullDependeciesFor(ResearchGoal goal) {
		if (goal.resolvedFullDependencies != null) {
			return goal.resolvedFullDependencies;
		}
		Set<String> foundDependencies = new HashSet<>();
		LinkedList<String> openList = new LinkedList<>();
		openList.addAll(goal.dependencies);
		Set<String> gDeps;
		String dep;
		ResearchGoal g1;
		while (!openList.isEmpty()) {
			dep = openList.poll();
			foundDependencies.add(dep);
			g1 = ResearchRegistry.getResearch(dep);
			gDeps = g1.dependencies;
			for (String goalName : gDeps) {
				if (!foundDependencies.contains(goalName)) {
					foundDependencies.add(goalName);
					openList.add(goalName);
				}
			}
		}
		goal.resolvedFullDependencies = foundDependencies;
		return foundDependencies;
	}

}
