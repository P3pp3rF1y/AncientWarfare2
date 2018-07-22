package net.shadowmage.ancientwarfare.core.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ResearchRegistry {
	private ResearchRegistry() {}

	private static final Map<String, ResearchGoal> researchGoals = new HashMap<>();

	@Nullable
	public static ResearchGoal getResearch(String researchName) {
		return researchGoals.get(researchName);
	}

	public static boolean researchExists(String researchName) {
		return researchGoals.containsKey(researchName);
	}

	public static Collection<ResearchGoal> getAllResearchGoals() {
		return researchGoals.values();
	}

	public static class ResearchParser implements IRegistryDataParser {
		@Override
		public String getName() {
			return "research";
		}

		@Override
		public void parse(JsonObject json) {
			String name = JsonUtils.getString(json, "name");
			int time = JsonUtils.getInt(json, "time");
			Set<String> dependencies = getDependencies(json);
			Set<Ingredient> resources = getResources(json);

			researchGoals.put(name, new ResearchGoal(name, dependencies, resources, time));
		}

		private Set<Ingredient> getResources(JsonObject json) {
			JsonArray res = JsonUtils.getJsonArray(json, "resources");
			JsonContext context = new JsonContext(AncientWarfareCore.MOD_ID);
			return StreamSupport.stream(res.spliterator(), false).map(e -> CraftingHelper.getIngredient(e, context)).collect(Collectors.toSet());
		}

		private Set<String> getDependencies(JsonObject json) {
			JsonArray deps = JsonUtils.getJsonArray(json, "dependencies");
			return StreamSupport.stream(deps.spliterator(), false).map(e -> JsonUtils.getString(e, "")).collect(Collectors.toSet());
		}
	}
}
