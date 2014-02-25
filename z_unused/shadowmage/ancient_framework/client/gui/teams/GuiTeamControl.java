/**
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package shadowmage.ancient_framework.client.gui.teams;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_framework.client.gui.GuiContainerAdvanced;
import shadowmage.ancient_framework.client.gui.elements.GuiButtonSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiScrollableArea;
import shadowmage.ancient_framework.client.gui.elements.GuiString;
import shadowmage.ancient_framework.client.gui.elements.GuiTab;
import shadowmage.ancient_framework.client.gui.elements.IGuiElement;
import shadowmage.ancient_framework.client.render.RenderTools;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.container.ContainerBase;
import shadowmage.ancient_framework.common.container.ContainerTeamControl;
import shadowmage.ancient_framework.common.teams.TeamData;
import shadowmage.ancient_framework.common.teams.TeamEntry;
import shadowmage.ancient_framework.common.teams.TeamPlayerEntry;
import shadowmage.ancient_framework.common.teams.TeamTracker;

public class GuiTeamControl extends GuiContainerAdvanced
{

GuiTab teamTab;
GuiTab changeTab;
GuiTab adminTab;

GuiTab activeTab;
GuiScrollableArea area;

GuiButtonSimple newTeamButton;

int errorMessageCount;
String errorMessage;

ContainerTeamControl container;

public GuiTeamControl(ContainerBase container)
  {
  super(container);
  this.container = (ContainerTeamControl) container;
  this.shouldCloseOnVanillaKeys = true;  
  }

@Override
public int getXSize()
  {
  return 256;
  }

@Override
public int getYSize()
  {
  return 240;
  }

@Override
protected void renderBackgroundImage(String tex)
  {
  if(tex!=null)
    {
    RenderTools.drawQuadedTexture(guiLeft, guiTop+13, this.xSize, this.ySize-13, 256, 240, tex, 0, 0);
    }
  }

@Override
public void renderExtraBackGround(int mouseX, int mouseY, float partialTime)
  {
  if(errorMessageCount>0 && errorMessage!=null)
    {
    this.drawStringGui(errorMessage, 0, -20, 0xff0000ff);   
    }
  }

@Override
public void updateScreenContents()
  {
  if(errorMessageCount>0)
    {
    this.errorMessageCount--;
    if(this.errorMessageCount<=0)
      {
      this.errorMessage = null;
      }
    }
  }

@Override
public void setupControls()
  {  
  area = new GuiScrollableArea(0, this, 8, 8+16, 256-16, 240-16-16, 240-16-16);
  this.guiElements.put(0, area);
  this.teamTab = addGuiTab(1, 3, 0, 78, 16, "Current Team");
  teamTab.enabled = true;
  this.activeTab = teamTab;
  this.changeTab = addGuiTab(2, 78+3, 0, 78, 16, "Change Teams");
  changeTab.enabled = false;  
  this.adminTab = addGuiTab(3, 3+78+78, 0, 78, 16, "Team Admin");
  adminTab.enabled = false;  
//  if(this.container.currentTeamEntry.getRankOf(container.player.getEntityName())<7)
//    {
//    adminTab.hidden = true;
//    }
  }

@Override
public void updateControls()
  {
  area.elements.clear();
  if(this.activeTab==this.teamTab)
    {
    this.addTeamMembers();
    }
  else if(this.activeTab==this.changeTab)
    {
    this.addTeamSelection();
    }
  else if(this.activeTab==this.adminTab)
    {
    this.addTeamAdmin();
    }  
  }

protected void addTeamMembers()
  {
  TeamEntry entry = this.container.currentTeamEntry;
  int targetY = 0;
  int index = 0;
  GuiString string;
  
  string = new GuiString(index, area, 80, 12, "Team Members: ");
  string.updateRenderPos(0, targetY);
  area.elements.add(string);  
  targetY+=14;
  
  for(TeamPlayerEntry playerEntry : entry.getPlayerEntries())
    {
    string = new GuiString(index, area, 80, 12, playerEntry.getPlayerName());
    string.updateRenderPos(0, targetY);
    area.elements.add(string);
    
    string = new GuiString(index, area, 80, 12, "Rank: "+playerEntry.getPlayerRank());
    string.updateRenderPos(120, targetY);
    area.elements.add(string);
    
    index++;
    targetY +=14;
    }
  }

protected HashMap<GuiButtonSimple, TeamEntry> teamApplyMap = new HashMap<GuiButtonSimple, TeamEntry>();

protected void addTeamSelection()
  {
  TeamData data = TeamTracker.instance().getTeamData(container.player.worldObj);
  
  int targetY = 0;
  int index = 0;
  GuiString string;
  GuiButtonSimple applyButton;
  
  newTeamButton = new GuiButtonSimple(index, area, 120, 14, "Create New Team");
  newTeamButton.updateRenderPos(0, targetY);
  area.elements.add(newTeamButton);
  targetY+=16;
  index++;
    
  string = new GuiString(index, area, 80, 12, "Select Team:");
  string.updateRenderPos(0, targetY);
  area.elements.add(string);  
  targetY+=16;
  index++;
    
  for(TeamEntry teamEntry : data.getTeamEntries())
    {
    string = new GuiString(index, area, 80, 12, teamEntry.teamName);
    string.updateRenderPos(0, targetY+1);
    area.elements.add(string);
    
    applyButton = new GuiButtonSimple(index, area, 40, 14, "Apply");
    applyButton.updateRenderPos(120, targetY);
    area.elements.add(applyButton);
    if(teamEntry.teamName.equals(container.currentTeamEntry.teamName))
      {
      applyButton.enabled = false;
      }
    teamApplyMap.put(applyButton, teamEntry);
    
    index++;
    targetY +=16;
    }
  }

protected void addTeamAdmin()
  {
  
  }



@Override
public void handleDataFromContainer(NBTTagCompound tag)
  {
  AWLog.logDebug("receiving error message");
  if(tag.hasKey("createFail"))
    {
    this.errorMessage = "Could not create team";
    this.errorMessageCount = 200;
    }
  this.refreshGui();
  }

@Override
public void onElementActivated(IGuiElement element)
  {
  if(element==this.teamTab || element==this.changeTab || element==this.adminTab)
    {
    teamTab.enabled = element==teamTab;
    changeTab.enabled = element==changeTab;
    adminTab.enabled = element==adminTab;
    this.activeTab = (GuiTab) element;
    this.refreshGui();
    }
  if(element==this.newTeamButton)
    {
    Minecraft.getMinecraft().displayGuiScreen(new GuiTeamCreation(this));
    }
  }

public void handleTeamCreation(String teamName, int hexColor)
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setBoolean("newTeam", true);
  tag.setString("teamName", teamName);
  tag.setInteger("color", hexColor);
  tag.setString("leaderName", player.getEntityName());
  this.sendDataToServer(tag);
  }
}
