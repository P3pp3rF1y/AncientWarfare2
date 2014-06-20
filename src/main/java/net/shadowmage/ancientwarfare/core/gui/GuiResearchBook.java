package net.shadowmage.ancientwarfare.core.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.RecipeResearched;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;

public class GuiResearchBook extends GuiContainerBase
{

RecipeResearched selectedRecipe = null;
ResearchGoal selectedGoal = null;

CompositeScrolled area;

CompositeScrolled detailsArea;

boolean researchMode = true;

Checkbox modeBox;

public GuiResearchBook(ContainerBase container)
  {
  super(container, 400, 240, defaultBackground);
  }

@Override
public void initElements()
  {
  area = new CompositeScrolled(0, 40, xSize/2, ySize-40);
  addGuiElement(area);
  modeBox = new Checkbox(8, 8, 16, 16, "foo.mode")
    {
    @Override
    public void onToggled()
      {
      researchMode = checked();
      refreshGui();
      }
    };
  modeBox.setChecked(researchMode);
  addGuiElement(modeBox);
  
  detailsArea = new CompositeScrolled(xSize/2, 40, xSize/2, ySize-40);
  addGuiElement(detailsArea);
  }

@Override
public void setupElements()
  {
  area.clearElements();
  detailsArea.clearElements(); 
  if(researchMode){addResearchModeControls();}
  else{addRecipeModeControsl();}
  }

private void addRecipeModeControsl()
  {
  int totalHeight = 8;
  for(RecipeResearched recipe : AWCraftingManager.INSTANCE.getRecipes())
    {
    area.addGuiElement(new RecipeButton(8, totalHeight, recipe)
      {
      @Override
      protected void onPressed()
        {
        selectedRecipe=recipe;
        refreshGui();
        }
      });
    totalHeight+=12;
    }   
  area.setAreaSize(totalHeight);
  
  totalHeight = 8;
  detailsArea.addGuiElement(new RecipeButton(8, totalHeight, selectedRecipe));
  
  detailsArea.addGuiElement(new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.researched_needed")));
  totalHeight+=14;
  
  if(selectedRecipe!=null)
    {
    GoalButton button;
    ResearchGoal goal;
    for(int num : selectedRecipe.getNeededResearch())
      {
      goal = ResearchGoal.getGoal(num);
      if(goal!=null)
        {
        button = new GoalButton(192, totalHeight, goal);
        detailsArea.addGuiElement(button);
        totalHeight+=12;
        }
      }
    }  
  }

private void addResearchModeControls()
  {
  Collection<ResearchGoal> goals = ResearchGoal.getResearchGoals();
  
  int totalHeight = 8;
  for(ResearchGoal goal : goals)
    {
    area.addGuiElement(new GoalButton(8, totalHeight, goal)
      {
      @Override
      protected void onPressed()
        {
        selectedGoal = goal;
        refreshGui();
        }
      });
    totalHeight+=12;
    } 
  area.setAreaSize(totalHeight);
  
  totalHeight = 8;
  detailsArea.addGuiElement(new GoalButton(8, totalHeight, selectedGoal));
  totalHeight+=16;
  
  detailsArea.addGuiElement(new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.researched_items")));
  totalHeight+=14;
  
  if(selectedGoal!=null)
    {
    List<RecipeResearched> recipes = AWCraftingManager.INSTANCE.getRecipes();
    List<RecipeResearched> list = new ArrayList<RecipeResearched>();
    for(RecipeResearched recipe : recipes)
      {
      if(recipe.getNeededResearch().contains(selectedGoal.getId()))
        {
        list.add(recipe);
        }
      }
    ItemSlot slot;
    int x,y;
    for(int i = 0; i< list.size(); i++)
      {
      x = 192 + (18*(i%9));
      y = totalHeight + 18*(i/9);
      slot = new ItemSlot(x, y, list.get(i).getRecipeOutput(), this);
      detailsArea.addGuiElement(slot);
      }
    }  
  }

private class RecipeButton extends Button
{
RecipeResearched recipe;
public RecipeButton(int topLeftX, int topLeftY, RecipeResearched recipe)
  {
  super(topLeftX, topLeftY, 160, 12, "");
  this.recipe = recipe;
  this.text = recipe==null ? StatCollector.translateToLocal("guistrings.no_selection") : recipe.getRecipeOutput().getDisplayName();
  this.setText(text);
  }
}

private class GoalButton extends Button
{
ResearchGoal goal;
public GoalButton(int topLeftX, int topLeftY, ResearchGoal goal)
  {
  super(topLeftX, topLeftY, 160, 10, goal==null? StatCollector.translateToLocal("guistrings.no_selection") : StatCollector.translateToLocal(goal.getName()));
  this.goal = goal;
  }
}


}
