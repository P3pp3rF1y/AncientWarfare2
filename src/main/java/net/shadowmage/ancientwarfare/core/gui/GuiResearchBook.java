package net.shadowmage.ancientwarfare.core.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.RecipeResearched;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;

public class GuiResearchBook extends GuiContainerBase
{

ResearchGoal selectedGoal = null;

CompositeScrolled area;
public GuiResearchBook(ContainerBase container)
  {
  super(container, 400, 240, defaultBackground);
  }

@Override
public void initElements()
  {
  area = new CompositeScrolled(0, 0, 160+8+16, ySize);
  addGuiElement(area);
  }

@Override
public void setupElements()
  {
  Collection<ResearchGoal> goals = ResearchGoal.getResearchGoals();
  
  int totalHeight = 8;
  for(ResearchGoal goal : goals)
    {
    area.addGuiElement(new GoalButton(8, totalHeight, 160, 12, goal)
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
  Button button;
  button = new GoalButton(192, totalHeight, 200, 12, selectedGoal);
  addGuiElement(button);
  totalHeight+=12;
  
  if(selectedGoal!=null)
    {
    List<RecipeResearched> recipes = AWCraftingManager.INSTANCE.getRecipes();
    AWLog.logDebug("found recipe list of: "+recipes);
    List<RecipeResearched> list = new ArrayList<RecipeResearched>();
    for(RecipeResearched recipe : recipes)
      {
      AWLog.logDebug("recipe research set: "+recipe.getNeededResearch());
      if(recipe.getNeededResearch().contains(selectedGoal.getId()))
        {
        list.add(recipe);
        }
      }
    AWLog.logDebug("adding selected goal details. found recipes: "+list);
    ItemSlot slot;
    int x,y;
    for(int i = 0; i< list.size(); i++)
      {
      x = 192 + (18*(i%9));
      y = totalHeight + 18*(i/9);
      slot = new ItemSlot(x, y, list.get(i).getRecipeOutput(), this);
      addGuiElement(slot);
      }
    }
  }


private class GoalButton extends Button
{

ResearchGoal goal;
public GoalButton(int topLeftX, int topLeftY, int width, int height, ResearchGoal goal)
  {
  super(topLeftX, topLeftY, width, height, goal==null? "guistrings.no_selection" : StatCollector.translateToLocal(goal.getName()));
  this.goal = goal;
  }

}


}
