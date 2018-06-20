package net.shadowmage.ancientwarfare.core.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.ResearchRecipeBase;
import net.shadowmage.ancientwarfare.core.crafting.ShapedResearchRecipe;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;
import net.shadowmage.ancientwarfare.core.registry.ResearchRegistry;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GuiResearchBook extends GuiContainerBase {

	private ResearchRecipeBase selectedRecipe = null;
	private ResearchGoal selectedGoal = null;

	private CompositeScrolled area;
	private CompositeScrolled detailsArea;

	private boolean researchMode = true;

	private Checkbox modeBox;

	public GuiResearchBook(ContainerBase container) {
		super(container, 400, 240);
	}

	@Override
	public void initElements() {
		area = new CompositeScrolled(this, 0, 40, xSize / 2, ySize - 40);
		addGuiElement(area);
		modeBox = new Checkbox(8, 8, 16, 16, "guistrings.research.research_mode") {
			@Override
			public void onToggled() {
				researchMode = checked();
				refreshGui();
			}
		};
		modeBox.setChecked(researchMode);
		addGuiElement(modeBox);

		detailsArea = new CompositeScrolled(this, xSize / 2, 40, xSize / 2, ySize - 40);
		addGuiElement(detailsArea);
	}

	@Override
	public void setupElements() {
		area.clearElements();
		detailsArea.clearElements();
		modeBox.setChecked(researchMode);
		if (researchMode) {
			addResearchModeControls();
		} else {
			addRecipeModeControls();
		}
	}

	private void addRecipeModeControls() {
		int totalHeight = 8;

		for (ResearchRecipeBase recipe : AWCraftingManager.getRecipes().stream().sorted(Comparator.comparing(r -> r.getRecipeOutput().getDisplayName()))
				.collect(Collectors.toCollection(LinkedHashSet::new))) {
			area.addGuiElement(new RecipeButton(8, totalHeight, recipe));
			totalHeight += 12;
		}
		area.setAreaSize(totalHeight);

		totalHeight = 8;
		detailsArea.addGuiElement(getRecipeButton(8, totalHeight, selectedRecipe));
		totalHeight += 14;

		if (selectedRecipe != null) {
			String depResearch = selectedRecipe.getNeededResearch();
			boolean canShow = ResearchTracker.INSTANCE.hasPlayerCompleted(mc.world, mc.player.getName(), depResearch);
			int recipeWidth = 3;
			int recipeHeight = 3;

			if (selectedRecipe instanceof ShapedResearchRecipe) {
				recipeWidth = ((ShapedResearchRecipe) selectedRecipe).getRecipeWidth();
				recipeHeight = ((ShapedResearchRecipe) selectedRecipe).getRecipeHeight();
			}

			if (canShow) {
				NonNullList<ItemStack> ingredients = NonNullList.withSize(recipeWidth * recipeHeight, ItemStack.EMPTY);
				for (int i = 0; i < selectedRecipe.getIngredients().size(); i++) {
					Ingredient ingredient = selectedRecipe.getIngredients().get(i);
					if (ingredient.getMatchingStacks().length > 0) {
						ingredients.set(i, ingredient.getMatchingStacks()[0]);
					}
				}
				for (int i = 0; i < recipeWidth; i++) {
					for (int j = 0; j < recipeHeight; j++) {
						detailsArea.addGuiElement(new ItemSlot(9 + 18 * i, totalHeight + 18 * j, ingredients.get(i + j * recipeWidth), this));
					}
				}
				totalHeight += (recipeHeight - 1) * 9;
				detailsArea.addGuiElement(new Label(8 + 18 * (recipeWidth + 1), totalHeight + 2, "->"));
			}
			detailsArea.addGuiElement(new ItemSlot(9 + 18 * (recipeWidth + 2), totalHeight, selectedRecipe.getRecipeOutput(), this));
			if (canShow)
				totalHeight += (recipeHeight + 1) * 9;
			else
				totalHeight += 20;
			detailsArea.addGuiElement(new Label(8, totalHeight, "guistrings.research.research_needed"));
			totalHeight += 14;
			GoalButton button;
			ResearchGoal goal;
			goal = ResearchRegistry.getResearch(depResearch);
			if (goal != null) {
				button = new GoalButton(8, totalHeight, goal);
				detailsArea.addGuiElement(button);
			}
		}
	}

	private void addResearchModeControls() {
		List<ResearchGoal> goals = new ArrayList<>(ResearchRegistry.getAllResearchGoals());
		goals.sort(new ResearchSorter());
		int totalHeight = 8;
		for (ResearchGoal goal : goals) {
			area.addGuiElement(new GoalButton(8, totalHeight, goal));
			totalHeight += 12;
		}
		area.setAreaSize(totalHeight);

		totalHeight = 8;
		detailsArea.addGuiElement(getResearchButton(8, totalHeight, selectedGoal));
		totalHeight += 16;

		if (selectedGoal != null) {
			Collection<ResearchRecipeBase> recipes = AWCraftingManager.getRecipes();
			List<ResearchRecipeBase> list = new ArrayList<>();
			for (ResearchRecipeBase recipe : recipes) {
				if (recipe.getNeededResearch().equals(selectedGoal.getName())) {
					list.add(recipe);
				}
			}
			if (!list.isEmpty()) {
				detailsArea.addGuiElement(new Label(8, totalHeight, "guistrings.research.researched_items"));
				totalHeight += 14;
				ItemSlot slot;
				for (int i = 0; i < list.size(); i++) {
					slot = new RecipeSlot(i, totalHeight, list.get(i), this);
					detailsArea.addGuiElement(slot);
				}
				totalHeight += 18 * (1 + list.size() / 9);
			}
			Set<ResearchGoal> deps = selectedGoal.getDependencies();
			if (!deps.isEmpty()) {
				detailsArea.addGuiElement(new Label(8, totalHeight, "guistrings.research.research_needed"));
				totalHeight += 14;
				GoalButton button;
				for (ResearchGoal goal : deps) {
					button = new GoalButton(8, totalHeight, goal);
					detailsArea.addGuiElement(button);
					totalHeight += 12;
				}
			}
		}
	}

	private class RecipeSlot extends ItemSlot {

		private final ResearchRecipeBase researched;

		public RecipeSlot(int i, int totalHeight, ResearchRecipeBase recipe, ITooltipRenderer render) {
			super(8 + (18 * (i % 9)), totalHeight + 18 * (i / 9), recipe.getRecipeOutput(), render);
			setRenderItemQuantity(false);
			researched = recipe;
		}

		@Override
		public void onSlotClicked(ItemStack stack, boolean rightClicked) {
			selectedRecipe = researched;
			researchMode = false;
			refreshGui();
		}
	}

	private Button getRecipeButton(int topLeftX, int topLeftY, ResearchRecipeBase recipe) {
		return new Button(topLeftX, topLeftY, 160, 12, recipe == null ? "guistrings.no_selection" : recipe.getRecipeOutput().getDisplayName());
	}

	private class RecipeButton extends Button {
		final ResearchRecipeBase recipe;

		public RecipeButton(int topLeftX, int topLeftY, ResearchRecipeBase recipe) {
			super(topLeftX, topLeftY, 160, 12, recipe == null ? "guistrings.no_selection" : recipe.getRecipeOutput().getDisplayName());
			this.recipe = recipe;
		}

		@Override
		protected void onPressed() {
			selectedRecipe = recipe;
			refreshGui();
		}
	}

	private Button getResearchButton(int topLeftX, int topLeftY, ResearchGoal goal) {
		return new Button(topLeftX, topLeftY, 160, 10, goal == null ? "guistrings.no_selection" : goal.getUnlocalizedName());
	}

	private class GoalButton extends Button {
		final ResearchGoal goal;

		public GoalButton(int topLeftX, int topLeftY, ResearchGoal goal) {
			super(topLeftX, topLeftY, 160, 10, goal == null ? "guistrings.no_selection" : goal.getUnlocalizedName());
			this.goal = goal;
		}

		@Override
		protected void onPressed() {
			selectedGoal = goal;
			researchMode = true;
			refreshGui();
		}
	}

	private class ResearchSorter implements Comparator<ResearchGoal> {
		@Override
		public int compare(ResearchGoal arg0, ResearchGoal arg1) {
			String nameA = I18n.format(arg0.getName());
			String nameB = I18n.format(arg1.getName());
			return nameA.compareTo(nameB);
		}
	}

}
