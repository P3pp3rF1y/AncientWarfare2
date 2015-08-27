package net.shadowmage.ancientwarfare.core.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.RecipeResearched;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

import java.util.*;

public class GuiResearchBook extends GuiContainerBase {

    private RecipeResearched selectedRecipe = null;
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

        for (RecipeResearched recipe : AWCraftingManager.INSTANCE.getRecipes()) {
            area.addGuiElement(new RecipeButton(8, totalHeight, recipe));
            totalHeight += 12;
        }
        area.setAreaSize(totalHeight);

        totalHeight = 8;
        detailsArea.addGuiElement(getRecipeButton(8, totalHeight, selectedRecipe));
        totalHeight += 14;

        if (selectedRecipe != null) {
            Set<Integer> depIds = selectedRecipe.getNeededResearch();
            boolean canShow = true;
            for (int num : depIds) {
                if (!ResearchTracker.INSTANCE.hasPlayerCompleted(mc.theWorld, mc.thePlayer.getCommandSenderName(), num)) {
                    canShow = false;
                    break;
                }
            }
            if(canShow){
                ItemStack[] ingredients = new ItemStack[selectedRecipe.getRecipeSize()];
                for(int i = 0; i < ingredients.length; i++){
                    Object object = selectedRecipe.getInput()[i];
                    if(object instanceof ItemStack){
                        ingredients[i] = (ItemStack) object;
                    }else if(object instanceof Iterable){
                        ingredients[i] = (ItemStack) ((Iterable) object).iterator().next();
                    }
                }
                for(int i = 0; i < selectedRecipe.getRecipeWidth(); i++){
                    for(int j = 0; j < selectedRecipe.getRecipeHeight(); j++){
                        detailsArea.addGuiElement(new ItemSlot(9 + 18 * i, totalHeight + 18 * j, ingredients[i + j * selectedRecipe.getRecipeWidth()], this));
                    }
                }
                totalHeight += (selectedRecipe.getRecipeHeight()-1) * 9;
                detailsArea.addGuiElement(new Label(8 + 18 * (selectedRecipe.getRecipeWidth()+1), totalHeight + 2, "->"));
            }
            detailsArea.addGuiElement(new ItemSlot(9 + 18 * (selectedRecipe.getRecipeWidth()+2), totalHeight, selectedRecipe.getRecipeOutput(), this));
            if(canShow)
                totalHeight += (selectedRecipe.getRecipeHeight()+1) * 9;
            else
                totalHeight += 20;
            detailsArea.addGuiElement(new Label(8, totalHeight, "guistrings.research.research_needed"));
            totalHeight += 14;
            GoalButton button;
            ResearchGoal goal;
            for (int num : depIds) {
                goal = ResearchGoal.getGoal(num);
                if (goal != null) {
                    button = new GoalButton(8, totalHeight, goal);
                    detailsArea.addGuiElement(button);
                    totalHeight += 12;
                }
            }
        }
    }

    private void addResearchModeControls() {
        List<ResearchGoal> goals = new ArrayList<ResearchGoal>();
        goals.addAll(ResearchGoal.getResearchGoals());
        Collections.sort(goals, new ResearchSorter());
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
            List<RecipeResearched> recipes = AWCraftingManager.INSTANCE.getRecipes();
            List<RecipeResearched> list = new ArrayList<RecipeResearched>();
            for (RecipeResearched recipe : recipes) {
                if (recipe.getNeededResearch().contains(selectedGoal.getId())) {
                    list.add(recipe);
                }
            }
            if(!list.isEmpty()) {
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
            if(!deps.isEmpty()) {
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

    private class RecipeSlot extends ItemSlot{

        private final RecipeResearched researched;
        public RecipeSlot(int i, int totalHeight, RecipeResearched recipe, ITooltipRenderer render) {
            super(8 + (18 * (i % 9)), totalHeight + 18 * (i / 9), recipe.getRecipeOutput(), render);
            setRenderItemQuantity(false);
            researched = recipe;
        }

        @Override
        public void onSlotClicked(ItemStack stack) {
            selectedRecipe = researched;
            researchMode = false;
            refreshGui();
        }
    }

    private Button getRecipeButton(int topLeftX, int topLeftY, RecipeResearched recipe){
        return new Button(topLeftX, topLeftY, 160, 12, recipe == null ? "guistrings.no_selection" : recipe.getRecipeOutput().getDisplayName());
    }

    private class RecipeButton extends Button {
        final RecipeResearched recipe;

        public RecipeButton(int topLeftX, int topLeftY, RecipeResearched recipe) {
            super(topLeftX, topLeftY, 160, 12, recipe == null ? "guistrings.no_selection" : recipe.getRecipeOutput().getDisplayName());
            this.recipe = recipe;
        }

        @Override
        protected void onPressed() {
            selectedRecipe = recipe;
            refreshGui();
        }
    }

    private Button getResearchButton(int topLeftX, int topLeftY, ResearchGoal goal){
        return new Button(topLeftX, topLeftY, 160, 10, goal == null ? "guistrings.no_selection" : goal.getName());
    }

    private class GoalButton extends Button {
        final ResearchGoal goal;

        public GoalButton(int topLeftX, int topLeftY, ResearchGoal goal) {
            super(topLeftX, topLeftY, 160, 10, goal == null ? "guistrings.no_selection" : goal.getName());
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
            String nameA = StatCollector.translateToLocal(arg0.getName());
            String nameB = StatCollector.translateToLocal(arg1.getName());
            return nameA.compareTo(nameB);
        }
    }

}
