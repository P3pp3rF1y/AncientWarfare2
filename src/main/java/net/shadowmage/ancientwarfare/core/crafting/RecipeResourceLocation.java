package net.shadowmage.ancientwarfare.core.crafting;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeResourceLocation {
	public static final RecipeResourceLocation NO_RECIPE_REGISTRY_NAME = new RecipeResourceLocation(RecipeType.NONE, "", "");

	public RecipeType getRecipeType() {
		return recipeType;
	}

	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}

	private final RecipeType recipeType;
	private final ResourceLocation resourceLocation;

	public RecipeResourceLocation(RecipeType recipeType, ResourceLocation resourceLocation) {
		this.recipeType = recipeType;
		this.resourceLocation = resourceLocation;
	}

	public RecipeResourceLocation(RecipeType recipeType, String resourceDomain, String resourcePath) {
		this(recipeType, new ResourceLocation(resourceDomain, resourcePath));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		RecipeResourceLocation other = (RecipeResourceLocation) o;
		return recipeType == other.recipeType && resourceLocation.equals(other.resourceLocation);
	}

	@Override
	public int hashCode() {
		int result = recipeType.hashCode();
		result = 31 * result + resourceLocation.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return this.recipeType.getName() + "|" + resourceLocation.toString();
	}

	private static final Pattern VALID_PATTERN = Pattern.compile("([a-z_]+)\\|(.+:.+)");

	public static RecipeResourceLocation deserialize(String recipeResourceLocation) {
		Matcher match = VALID_PATTERN.matcher(recipeResourceLocation);
		if (match.find() && !match.group(1).equals(RecipeType.NONE.getName())) {
			return new RecipeResourceLocation(RecipeType.deserialize(match.group(1)), new ResourceLocation(match.group(2)));
		}

		return NO_RECIPE_REGISTRY_NAME;
	}

	public enum RecipeType implements IStringSerializable {
		NONE, RESEARCH, REGULAR;

		@Override
		public String getName() {
			return this.name().toLowerCase();
		}

		public static RecipeType deserialize(String name) {
			for (RecipeType type : values()) {
				if (type.getName().equals(name)) {
					return type;
				}
			}
			return NONE;
		}
	}
}
