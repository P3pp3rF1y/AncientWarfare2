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

import net.minecraft.client.Minecraft;
import shadowmage.ancient_framework.client.gui.GuiContainerAdvanced;
import shadowmage.ancient_framework.client.gui.elements.GuiButtonSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiNumberInputLine;
import shadowmage.ancient_framework.client.gui.elements.GuiTextInputLine;
import shadowmage.ancient_framework.client.gui.elements.IGuiElement;

public class GuiTeamCreation extends GuiContainerAdvanced
{

String teamName;
int red;
int blue;
int green;

GuiButtonSimple doneButton;
GuiButtonSimple cancelButton;
GuiTextInputLine teamNameInput;
GuiNumberInputLine redInput;
GuiNumberInputLine blueInput;
GuiNumberInputLine greenInput;

GuiTeamControl parent;

public GuiTeamCreation(GuiTeamControl parent)
  {
  super(parent.container);
  this.parent = parent;
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
  return 120;
  }

@Override
public void renderExtraBackGround(int mouseX, int mouseY, float partialTime)
  {
  int targetY = 8;
  this.drawStringGui("Team Name: ", 8, targetY, WHITE);
  targetY+=18;
  targetY+=14;
  this.drawStringGui("Red: ", 8, targetY, WHITE);
  targetY+=18;
  this.drawStringGui("Green: ", 8, targetY, WHITE);
  targetY+=18;
  this.drawStringGui("Blue: ", 8, targetY, WHITE);
  }

@Override
public void updateScreenContents()
  {
  this.teamName = this.teamNameInput.getText();
  this.red = this.redInput.getIntVal();
  this.green = this.greenInput.getIntVal();
  this.blue = this.blueInput.getIntVal();
  }

@Override
public void setupControls()
  {
  int targetY = 8;
  this.doneButton = this.addGuiButton(0, 35, 16, "Create");
  this.doneButton.updateRenderPos(256-35-8, targetY);
  targetY+=18;
  
  this.cancelButton = this.addGuiButton(1, 35, 16, "Cancel");
  this.cancelButton.updateRenderPos(256-35-8, targetY);
  targetY = 18;
       
  this.teamNameInput = this.addTextField(3, 120, 14, 40, "");
  this.teamNameInput.updateRenderPos(8, targetY);
  targetY+=18;
  
  this.redInput = this.addNumberField(4, 40, 14, 20, "0").setAsIntegerValue().setIntegerValue(0);
  this.redInput.updateRenderPos(60, targetY);
  targetY+=18;
  
  this.greenInput = this.addNumberField(5, 40, 14, 20, "0").setAsIntegerValue().setIntegerValue(0);
  this.greenInput.updateRenderPos(60, targetY);
  targetY+=18;
  
  this.blueInput = this.addNumberField(6, 40, 14, 20, "0").setAsIntegerValue().setIntegerValue(0);
  this.blueInput.updateRenderPos(60, targetY);
  targetY+=18;
  }

@Override
public void updateControls()
  {
 
  }

@Override
public void onElementActivated(IGuiElement element)
  {
  if(element==this.doneButton)
    {
    int teamColor = 0;
    teamColor = (red<<24) | (blue<<16) | (green<<8) | (0xff << 0);
    Minecraft.getMinecraft().displayGuiScreen(parent);
    this.parent.container.gui = this.parent;
    this.parent.handleTeamCreation(teamName, teamColor);
    }
  else if(element==this.cancelButton)
    {
    Minecraft.getMinecraft().displayGuiScreen(parent);
    }
  }

}
